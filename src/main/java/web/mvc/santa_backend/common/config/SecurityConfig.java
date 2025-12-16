package web.mvc.santa_backend.common.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import web.mvc.santa_backend.common.filter.JWTFilter;
import web.mvc.santa_backend.common.filter.LoginFilter;
import web.mvc.santa_backend.common.security.JWTUtil;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    //AuthenticationManager가 인자로 받을 AuthenticationConfiguraion 객체 생성자 주입
    private final AuthenticationConfiguration authenticationConfiguration;

    private final JWTUtil jwtUtil;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        log.info("bCryptPasswordEncoder called...");
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // csrf disable (csrf공격을 방어하기 위한 토큰 주고 받는 부분을 비활성화)
        http.csrf((auth) -> auth.disable());
        // http basic 인증 방식 disable
        http.httpBasic((auth) -> auth.disable());
        // Form 로그인 방식 disable -> React, JWT 인증 방식으로 변경
        // disable 설정하면 security의 UsernamePasswordAuthenticationFilter 비활성화
        http.formLogin((auth) -> auth.disable());

        http.cors(cors->cors.configurationSource(corsConfigurationSource()));



        // 모두 허용 (임시)
        //http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());


        // 경로별 인가 작업 (필요한 거 추가!)
        http.authorizeHttpRequests((auth) ->
                auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers("/index", "/api/user", "/api/user/**").permitAll()
                        // swagger 설정
                        .requestMatchers(
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        // GET 요청 누구나 접근 가능
                        //.requestMatchers(HttpMethod.GET, "/api/user/**").permitAll()
                        // Follow 인증 필요
                        .requestMatchers("/api/follow/**").authenticated()
                        .requestMatchers("/test").authenticated()
                        // POST 요청 인증 필요
                        //.requestMatchers(HttpMethod.POST, "/posts").authenticated()
                        .requestMatchers("/api/admin").hasRole("ADMIN")
                        .anyRequest().authenticated());


        // 필터 추가(교체)
        // UsernamePasswordAuthenticationFilter 는 form login(security의 기본 로그인)을 진행하는 필터
        // form login을 위에서 disable 했고, 우리는 이 필터를 상속받은 LoginFilter로 jwt 방식 로그인을 할 것
        // addFilterAt:  UsernamePasswordAuthenticationFilter 자리에 LoginFilter 가 실행되도록 설정
        http.addFilterAt(
                new LoginFilter(
                        this.authenticationManager(authenticationConfiguration),    // AuthenticationManager
                        jwtUtil),
                UsernamePasswordAuthenticationFilter.class);

        // LoginFilter(Authentication 담당) 이전에 JWTFilter(JWT 검증) 실행
        http.addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:5173"));

        configuration.setAllowedMethods(Collections.singletonList("*"));

        configuration.setAllowedHeaders(Collections.singletonList("*"));

        configuration.setAllowCredentials(true);

        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
