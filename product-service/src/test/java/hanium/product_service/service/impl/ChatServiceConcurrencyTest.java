package hanium.product_service.service.impl;

import hanium.product_service.dto.request.CreateChatroomRequestDTO;
import hanium.product_service.grpc.ProductGrpcService;
import hanium.product_service.repository.ChatroomRepository;
import hanium.product_service.util.SweetTrackerUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

//grpc 서버는 띄우지 X
@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=net.devh.boot.grpc.server.autoconfigure.GrpcServerMetricAutoConfiguration",
        "grpc.server.port=-1"
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

    @RepeatedTest(value = 2, name = "{displayName} {currentRepetition}/{totalRepetitions}")
    @DisplayName("동시에 같은 키로 2번 생성 시도해도 1행만 생성되고 같은 방 ID 반환")
    void concurrent_createOrGet_onlyOneRowCreated(RepetitionInfo rep) throws Exception{
        Long productId = 10L + rep.getCurrentRepetition();
        Long senderId = 1L, receiverId = 2L;
        int threads = 2;
        var req = CreateChatroomRequestDTO.builder()
                .productId(productId).senderId(senderId).receiverId(receiverId).build();


        var start = new CountDownLatch(1);
        var done = new CountDownLatch(threads);
        var pool = Executors.newFixedThreadPool(threads);
        var ids = Collections.synchronizedList(new ArrayList<Long>());
        var failures = new java.util.concurrent.atomic.AtomicInteger(0);

        Runnable task = () ->{
            await(start);
            try{
                var res = chatServiceImpl.createChatroom(req);
                ids.add(res.getChatroomId());
            }catch(Exception ex){
                failures.incrementAndGet();
            }finally {
                done.countDown();
            }
        };

        pool.submit(task);
        pool.submit(task);
        start.countDown();
        done.await(5, TimeUnit.SECONDS);
        pool.shutdown();

        // 두 요청 모두 성공해야 진짜 그린
        assertThat(failures.get()).as("예외 없이 두 요청 모두 성공해야 함").isZero();
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
