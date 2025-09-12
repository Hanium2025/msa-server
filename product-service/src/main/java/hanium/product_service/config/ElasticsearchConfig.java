//package hanium.product_service.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.elasticsearch.client.ClientConfiguration;
//import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
//import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
//import org.springframework.lang.NonNull;
//
//@Configuration
//@EnableElasticsearchRepositories
//public class ElasticsearchConfig extends ElasticsearchConfiguration {
//
//    @Value("${spring.elasticsearch.uris}")
//    private String host;
//
//    @Override
//    @NonNull
//    public ClientConfiguration clientConfiguration() {
//        return ClientConfiguration.builder()
//                .connectedTo(host)
//                .build();
//    }
//}
