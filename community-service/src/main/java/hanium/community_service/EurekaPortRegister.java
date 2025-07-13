package hanium.community_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaRegistration;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
/**
 * 동적으로 할당된 포트를 Eureka 서버에 반영하는 컴포넌트입니다.
 *
 * Spring Boot 애플리케이션이 시작된 이후 실제 할당된 HTTP 포트를
 * Eureka 인스턴스 설정에 적용하여, 클라이언트가 정확한 포트로 접근할 수 있도록 합니다.
 *
 * 이 설정은 server.port=0 과 같이 포트를 자동 할당하는 경우에 필수입니다.
 */
@Component
public class EurekaPortRegister {

    @Autowired
    private  WebServerApplicationContext webServerAppCtxt;
    @Qualifier("eurekaRegistration")
    @Autowired
    private EurekaRegistration eurekaRegistration;
    @EventListener
    public void onApplicationReady(ApplicationReadyEvent event) {
        // 실제 할당된 HTTP 포트 조회
        int actualPort = webServerAppCtxt.getWebServer().getPort();
        System.out.println("실제 할당된 HTTP 포트: " + actualPort);

        // Eureka 등록 정보에 실제 포트 반영
        eurekaRegistration.getInstanceConfig().setNonSecurePort(actualPort);

    }
}