package hanium.user_service;
import com.netflix.appinfo.EurekaInstanceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaRegistration;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


@Component
public class EurekaPortRegister {

    @Autowired
    private WebServerApplicationContext webServerAppCtxt;
    @Qualifier("eurekaRegistration")
    @Autowired
    private EurekaRegistration eurekaRegistration;
    @EventListener
    public void onApplicationReady(ApplicationReadyEvent event) {
        int actualPort = webServerAppCtxt.getWebServer().getPort();
        System.out.println("실제 할당된 HTTP 포트: " + actualPort);
        // TODO: Eureka Instance 정보에 이 포트 반영 (필요시)

        // 실제 포트를 Eureka 인스턴스에 반영
        eurekaRegistration.getInstanceConfig().setNonSecurePort(actualPort);

    }
}