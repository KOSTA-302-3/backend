package web.mvc.santa_backend.common.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import web.mvc.santa_backend.post.entity.dbtest.RedisPosts;

import java.time.Duration;

@Configuration
@EnableRedisRepositories
@EnableCaching
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    /**
     * 공통으로 사용할 JSON Serializer 설정 (Deprecated 해결 핵심)
     * GenericJackson2JsonRedisSerializer 대신 사용됩니다.
     */
    private Jackson2JsonRedisSerializer<Object> jsonSerializer() {
        ObjectMapper objectMapper = new ObjectMapper();

        // 객체의 클래스 타입 정보를 JSON에 포함하도록 설정 (이게 없으면 읽어올 때 에러남)
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        return new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);
    }

    /**
     * 1. RedisTemplate 설정 (Queue 등 직접 조작용)
     * 주석 해제 및 설정 적용 완료
     */
    @Bean
    public RedisTemplate<String, RedisPosts> redisPostsTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, RedisPosts> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Key는 String
        template.setKeySerializer(new StringRedisSerializer());

        // Value는 위에서 만든 JSON Serializer 사용 (RedisPosts로 형변환되어 들어감)
        // (타입 안전성을 위해 RedisPosts 전용 Serializer를 만들어도 되지만,
        //  Object용을 써도 내부적으로 잘 동작합니다. 여기서는 명확성을 위해 Object용 사용)
        template.setValueSerializer(jsonSerializer());

        // HashKey/Value 설정 (Hash 자료구조 쓸 때 필요)
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(jsonSerializer());

        return template;
    }

    /**
     * 2. RedisCacheManager 설정 (@Cacheable 용)
     * Deprecated 된 GenericJackson2JsonRedisSerializer 교체
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .computePrefixWith(cacheName -> cacheName + ":") // 캐시 키 앞에 "이름:" 붙임
                .entryTtl(Duration.ofHours(1)) // 기본 유효시간 1시간

                // Key 직렬화
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))

                // Value 직렬화 (여기가 수정됨: Generic... -> jsonSerializer())
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer()));

        return RedisCacheManager.builder(factory)
                .cacheDefaults(config)
                .build();
    }
}