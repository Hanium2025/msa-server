package hanium.product_service.service.impl;

import hanium.product_service.dto.request.CreateChatroomRequestDTO;
import hanium.product_service.elasticsearch.ProductSearchElasticRepository;
import hanium.product_service.elasticsearch.ProductSearchIndexer;
import hanium.product_service.grpc.ProductGrpcService;
import hanium.product_service.repository.ChatroomRepository;
import hanium.product_service.util.SweetTrackerUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

//grpc 서버는 띄우지 X
@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=net.devh.boot.grpc.server.autoconfigure.GrpcServerMetricAutoConfiguration",
        "grpc.server.port=-1",

})
@Testcontainers
@ActiveProfiles("test")
public class ChatServiceConcurrencyTest {

    @Autowired
    ChatServiceImpl chatServiceImpl;
    @Autowired
    ChatroomRepository chatroomRepository;
    @Autowired
    JdbcTemplate jdbc;

    @MockBean
    SweetTrackerUtil sweetTrackerUtil;

    @MockBean
    DeliveryServiceImpl deliveryServiceImpl;
    @MockBean
    ProductGrpcService productGrpcService;

    @MockBean
    TossPaymentServiceImpl  tossPaymentServiceImpl;
    @MockBean
    ProductSearchIndexer productSearchIndexer;
    @MockBean
    ProductSearchElasticRepository productSearchElasticRepository;

    @Test
    @DisplayName("더블클릭: 같은 (buyer, seller, product) 동시 2요청 → 단 1행 + 동일 ID")
    void doubleClick_sameBuyerSameSellerSameProduct() throws Exception{
        Long productId = 10L; //고정
        Long senderId = 1L, receiverId = 2L; //고정
        int threads = 2; //더블클릭
        var req = CreateChatroomRequestDTO.builder()
                .productId(productId).senderId(senderId).receiverId(receiverId).build();


        var start = new CountDownLatch(1);
        var done = new CountDownLatch(threads);
        var pool = Executors.newFixedThreadPool(threads);
        var ids = Collections.synchronizedList(new ArrayList<Long>());
        var failures = new java.util.concurrent.ConcurrentLinkedQueue<Throwable>();

        Runnable task = () ->{
            await(start);
            try{
                var res = chatServiceImpl.createChatroom(req);
                ids.add(res.getChatroomId());
            }catch(Exception ex){
                failures.add(ex);
            }finally {
                done.countDown();
            }
        };

        // 스레드 수만큼 제출
        for (int i = 0; i < threads; i++) {
            pool.submit(task);
        }

        start.countDown();
        boolean finished = done.await(10, TimeUnit.SECONDS);
        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.SECONDS);

        assertThat(finished).as("스레드가 시간 내 끝나야 함").isTrue();
        // 두 요청 모두 성공해야 진짜 그린
        assertThat(failures).as("예외 없이 모든 요청이 성공해야 함").isEmpty();

        assertThat(ids).hasSize(threads);
        assertThat(new java.util.HashSet<>(ids)).hasSize(1);

        Long rowCount = jdbc.queryForObject(
                "SELECT COUNT(*) FROM chatroom WHERE product_id=? AND sender_id=? AND receiver_id=?",
                Long.class, productId, senderId, receiverId
        );

        assertThat(rowCount).isEqualTo(1L);

        var okIds = ids.stream().filter(id -> id > 0).collect(Collectors.toSet());
        assertThat(okIds).hasSize(1);

    }

    @Test
    @DisplayName("네트워크 지연/재전송: 같은 (buyer, seller, product) 동시 2요청 → 단 1행 + 동일 ID")
    void doubleClick_sameBuyerSameSellerSameProduct_With_SlowTask() throws Exception{
        Long productId = 10L; //고정
        Long senderId = 1L, receiverId = 2L; //고정
        int threads = 2; //더블클릭
        var req = CreateChatroomRequestDTO.builder()
                .productId(productId).senderId(senderId).receiverId(receiverId).build();


        var start = new CountDownLatch(1);
        var done = new CountDownLatch(threads);
        var pool = Executors.newFixedThreadPool(threads);
        var ids = Collections.synchronizedList(new ArrayList<Long>());
        var failures = new java.util.concurrent.ConcurrentLinkedQueue<Throwable>();

        //처음 클릭
        Runnable task = () ->{
            await(start);
            try{
                var res = chatServiceImpl.createChatroom(req);
                ids.add(res.getChatroomId());
            }catch(Exception ex){
                failures.add(ex);
            }finally {
                done.countDown();
            }
        };

        //네트워크 지연으로 인한 클리
        Runnable slowTask = () -> {
            try {
                start.await();
                Thread.sleep(ThreadLocalRandom.current().nextInt(3, 25)); // 3~25ms 지연
                var res = chatServiceImpl.createChatroom(req);
                ids.add(res.getChatroomId());
            } catch (Throwable t) { failures.add(t); }
            finally { done.countDown(); }
        };

        // 한 개는 즉시, 한 개는 지연
        pool.submit(task);
        pool.submit(slowTask);

        start.countDown();
        boolean finished = done.await(10, TimeUnit.SECONDS);
        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.SECONDS);

        assertThat(finished).as("스레드가 시간 내 끝나야 함").isTrue();
        // 두 요청 모두 성공해야 진짜 그린
        assertThat(failures).as("예외 없이 모든 요청이 성공해야 함").isEmpty();

        assertThat(ids).hasSize(threads);
        assertThat(new java.util.HashSet<>(ids)).hasSize(1);

        Long rowCount = jdbc.queryForObject(
                "SELECT COUNT(*) FROM chatroom WHERE product_id=? AND sender_id=? AND receiver_id=?",
                Long.class, productId, senderId, receiverId
        );

        assertThat(rowCount).isEqualTo(1L);

        var okIds = ids.stream().filter(id -> id > 0).collect(Collectors.toSet());
        assertThat(okIds).hasSize(1);

    }

    private void await(CountDownLatch start) {
        try { start.await(); } catch (InterruptedException ignored) {}
    }
}
