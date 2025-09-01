package hanium.product_service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericToStringSerializer;

@Configuration
@EnableRedisRepositories
@RequiredArgsConstructor
public class RedisConfig {

    private final RedisProperties redisProperties;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(redisProperties.getHost(), redisProperties.getPort());
    }

    @Bean
    public RedisTemplate<Long, Long> redisTemplate() {
        RedisTemplate<Long, Long> template = new RedisTemplate<>();

        // Long-String 직렬화
        GenericToStringSerializer<Long> longSerializer = new GenericToStringSerializer<>(Long.class);
        template.setKeySerializer(longSerializer);
        template.setValueSerializer(longSerializer);
        template.setHashKeySerializer(longSerializer);
        template.setHashValueSerializer(longSerializer);
        template.setConnectionFactory(redisConnectionFactory());

        return template;
    }
}