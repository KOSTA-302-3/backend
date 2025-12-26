package web.mvc.santa_backend.common.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import web.mvc.santa_backend.post.entity.dbtest.RedisPosts;

import java.time.Duration;

@Configuration
@EnableRedisRepositories
@EnableCaching
public class RedisConfig {

    /**
     * [중요] RedisConnectionFactory Bean 삭제함!
     * application.properties 에 적힌 host, port, password, ssl 설정을
     * Spring Boot가 자동으로 읽어서 ConnectionFactory를 생성해줍니다.
     */

    /**
     * 1. 범용 RedisTemplate (Queue, 일반 데이터용)
     * - Python과 통신하는 Queue에 데이터를 넣을 때 이 템플릿을 사용하세요.
     * - @class 정보 없이 순수 JSON으로 저장됩니다.
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Key는 문자열
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Value는 순수 JSON (타입 정보 없음)
        GenericJackson2JsonRedisSerializer jsonSerializer = getJsonSerializer();
        
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        return template;
    }

    /**
     * 2. RedisPosts 전용 Template
     * - 기존 엔티티 로직 호환성 유지
     */
    @Bean
    public RedisTemplate<String, RedisPosts> redisPostsTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, RedisPosts> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Key
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Value (커스텀 JSON 설정)
        GenericJackson2JsonRedisSerializer jsonSerializer = getJsonSerializer();

        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        return template;
    }

    /**
     * 3. CacheManager
     * - @Cacheable 사용 시 순수 JSON으로 저장
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .computePrefixWith(cacheName -> cacheName + ":")
                .entryTtl(Duration.ofHours(1))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(getJsonSerializer())); 

        return RedisCacheManager.builder(factory)
                .cacheDefaults(config)
                .build();
    }

    /**
     * [공통] JSON Serializer 설정 (핵심)
     */
    private GenericJackson2JsonRedisSerializer getJsonSerializer() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Java 8 날짜 처리
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // 타임스탬프 대신 날짜 포맷 사용
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // 모르는 필드 무시
        
        // activateDefaultTyping() 제거됨 -> Python 호환성 확보 및 에러 방지
        
        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }
}