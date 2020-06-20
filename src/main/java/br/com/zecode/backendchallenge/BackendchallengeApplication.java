package br.com.zecode.backendchallenge;

import br.com.zecode.backendchallenge.model.BusinessPartner;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;


@Caching
@SpringBootApplication
public class BackendchallengeApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendchallengeApplication.class, args);
    }

    @Bean
    @Primary
    public RedisCacheConfiguration defaultCacheConfig(ObjectMapper objectMapper) {
        Jackson2JsonRedisSerializer<BusinessPartner> serializer = new Jackson2JsonRedisSerializer<>(BusinessPartner.class);
        serializer.setObjectMapper(objectMapper);
        return RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
                .prefixKeysWith("");
    }

    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> webServerFactoryCustomizer() {
        return factory -> factory.setContextPath("/ze-service");
    }


}
