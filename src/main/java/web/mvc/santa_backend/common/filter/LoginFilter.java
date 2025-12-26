package web.mvc.santa_backend.common.filter;

import com.google.gson.Gson;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import web.mvc.santa_backend.admin.entity.Bans;
import web.mvc.santa_backend.admin.repository.BansRepository;
import web.mvc.santa_backend.common.security.CustomUserDetails;
import web.mvc.santa_backend.common.security.JWTUtil;
import web.mvc.santa_backend.user.entity.Users;
import web.mvc.santa_backend.user.repository.UserRepository;
import web.mvc.santa_backend.user.service.UserService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final BansRepository bansRepository;
    private final UserRepository userRepository;
    
    @Autowired
    @Lazy
    private UserService userService;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil,
                      BansRepository bansRepository, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.bansRepository = bansRepository;
        this.userRepository = userRepository;
        setFilterProcessesUrl("/api/login");
    }

    /**
     * 로그인 요청 받아 인증 시도
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {


        // 1. 클라이언트 로그인 요청시 id, password 받아서 출력
        String username = super.obtainUsername(request);    //id
        String password = super.obtainPassword(request);    //password
        log.info("username = {}", username);
        log.info("password = {}", password);

        // 2. spring security 에서 username, password를 검증하기 위해 Username...Token에 담음
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(username, password);

        // 3. token을 AuthenticationManager 에 전달 -> Provider -> UserDetailsServicve db연결 -> CustomUserDetails 생성
        Authentication authentication = authenticationManager.authenticate(authToken);// CustomUserDetails 정보 반환
        log.info("authentication = {}",authentication);

        return authentication;
    }

    /**
     * 로그인 성공 시 JWT 생성해서 반환
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authentication) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        log.info("로그인 성공...");

        //UserDetailsService
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        Users user = customUserDetails.getUser();
        
        // 정지 상태면 로그인 차단
        if (isUserSuspended(user)) {
            response.setContentType("text/html;charset=UTF-8");
            response.setStatus(403);
            Map<String, Object> errorMap = new HashMap<>();
            errorMap.put("errMsg", "정지된 계정입니다. 관리자에게 문의하세요.");
            Gson gson = new Gson();
            response.getWriter().print(gson.toJson(errorMap));
            return;
        }

        /*
        하나의 유저가 여러개의 권한을 가질수 있어서 collection으로 반환
        기본 제너릭이 GrantedAuthority이고 GrantedAuthority를 상속받은 자식들이 Role 이 된다
        우리는 하나의 권한만 지정했다..USER
        */
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        // role을 다시 DB에서 찾을 필요 없이 security 가 넣어 준 Authentication.authorities에서 바로 사용한 것
        //String role = auth.getAuthority(); // JWT 에 ROLE_USER or ROLE_ADMIN 로 저장됨
        String role = customUserDetails.getUser().getRole().toString(); // prefix 없이 ADMIN or USER 로 저장
        System.out.println("role: " + role);

        //토큰 생성 (password는 JWTUtil에서 안 담음!!)
        String token = jwtUtil.createJwt(
                customUserDetails.getUser(), role, 1000L*60*30L);   //1초*60*10 30분
        System.out.println("@@@@@@@@@@@@@@@@@@ getUser " + customUserDetails.getUser() + " @@@@@@@@@@@@@@@@@@");
        // 응답할 헤더를 설정
        // Bearer 뒤에 공백을 준다. 관례적인 prefix
        response.addHeader("Authorization", "Bearer " + token);

        Map<String, Object> map = new HashMap<>();
        map.put("userId", user.getUserId());
        map.put("username", user.getUsername());
        map.put("role", user.getRole().toString());

        System.out.println("유저 id"+user.getUserId());

        Gson gson= new Gson();
        String arr = gson.toJson(map);

        Cookie cookie = new Cookie("Authorization", token);
        cookie.setHttpOnly(true);
        cookie.setSecure("https".equals(request.getHeader("X-Forwarded-Proto")));  // https 적용 시 true로 변경, 테스트는 로컬이니까  false 로
        cookie.setPath("/");
        cookie.setMaxAge(60 * 30);




        // 응답에 쿠키 태워 보내기
        response.addCookie(cookie);


        response.getWriter().print(arr);
    }

    /**
     * 로그인 실패 시
     * CustomMemberDetailsService에서 null이 떨어지면 이곳으로 리턴
     * 응답 메세지를 Json형태로 프론트로 넘기기 위해서 Gson 라이브러리 사용
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed) throws IOException {

        response.setContentType("text/html;charset=UTF-8");

        log.info("로그인 실패...");
        log.error("failed = {}", failed);

        //로그인 실패시 401 응답 코드 반환
        response.setStatus(401);
        Map<String, Object> map = new HashMap<>();
        map.put("errMsg", "정보를 다시 확인해주세요.");

        Gson gson= new Gson();
        String arr = gson.toJson(map);
        response.getWriter().print(arr);
    }
    
    /**
     * 유저가 정지 상태인지 확인
     */
    private boolean isUserSuspended(Users user) {
        // bans 테이블에서 유효한 정지 기록 확인 (finishedAt > now)
        LocalDateTime now = LocalDateTime.now();
        return bansRepository.findByUser_UserId(user.getUserId()).stream()
                .anyMatch(ban -> ban.getFinishedAt().isAfter(now));
    }
}
