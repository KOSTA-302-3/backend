package web.mvc.santa_backend.common.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.*;
import web.mvc.santa_backend.post.entity.dbtest.RedisFeedBacks;
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

    @Value("${spring.data.redis.password}")
    private String password;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {

        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);

        // 2. 패스워드 설정 (RedisPassword 객체 사용)
        config.setPassword(RedisPassword.of(password));

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .useSsl()  // <--- 이 부분이 AWS ElastiCache 접속 시 필수입니다.
                // .and().commandTimeout(Duration.ofMillis(60000)) // 필요시 타임아웃 설정 등 추가
                .build();

        return new LettuceConnectionFactory(config,clientConfig);
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

        // 1. 순수 데이터 저장을 위한 ObjectMapper 설정
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // 날짜(LocalDateTime) 처리
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // 날짜를 배열 대신 문자열로
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // 모르는 필드 있어도 무시

        // 2. Serializer 생성 (익명 클래스로 간단하게 구현)
        RedisSerializer<RedisPosts> serializer = new RedisSerializer<RedisPosts>() {
            @Override
            public byte[] serialize(RedisPosts value) throws SerializationException {
                try {
                    return value == null ? null : mapper.writeValueAsBytes(value);
                } catch (Exception e) { throw new SerializationException("Serialize Error", e); }
            }

            @Override
            public RedisPosts deserialize(byte[] bytes) throws SerializationException {
                try {
                    return (bytes == null || bytes.length == 0) ? null : mapper.readValue(bytes, RedisPosts.class);
                } catch (Exception e) { throw new SerializationException("Deserialize Error", e); }
            }
        };

        // 3. 설정 적용
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);

        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        return template;
    }

    @Bean
    public RedisTemplate<String, RedisFeedBacks> redisFeedBackTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, RedisFeedBacks> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 1. 순수 데이터 저장을 위한 ObjectMapper 설정
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // 날짜(LocalDateTime) 처리
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // 날짜를 배열 대신 문자열로
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // 모르는 필드 있어도 무시

        // 2. Serializer 생성 (익명 클래스로 간단하게 구현)
        RedisSerializer<RedisFeedBacks> serializer = new RedisSerializer<RedisFeedBacks>() {
            @Override
            public byte[] serialize(RedisFeedBacks value) throws SerializationException {
                try {
                    return value == null ? null : mapper.writeValueAsBytes(value);
                } catch (Exception e) { throw new SerializationException("Serialize Error", e); }
            }

            @Override
            public RedisFeedBacks deserialize(byte[] bytes) throws SerializationException {
                try {
                    return (bytes == null || bytes.length == 0) ? null : mapper.readValue(bytes, RedisFeedBacks.class);
                } catch (Exception e) { throw new SerializationException("Deserialize Error", e); }
            }
        };

        // 3. 설정 적용
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);

        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

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