package com.ktdsuniversity.edu.global.security.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.ktdsuniversity.edu.domain.user.dao.UserDao;
import com.ktdsuniversity.edu.global.security.handler.LoginFailureHandler;
import com.ktdsuniversity.edu.global.security.handler.LoginSuccessHandler;
import com.ktdsuniversity.edu.global.security.jwt.filter.JwtAuthenticationFilter;
import com.ktdsuniversity.edu.global.security.oauth.SecurityOAtuhService;
import com.ktdsuniversity.edu.global.security.oauth.handler.CustomOAuth2SuccessHandler;

@Configuration
@EnableWebSecurity(debug=true)
@EnableMethodSecurity
public class SecurityConfig {
	
	@Autowired
	private UserDao userDao;
	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Autowired
    private CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
    @Autowired
    private SecurityOAtuhService securityOAtuhService;
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		
		
		// CustomFilter FilterChain에 등록.
		http.addFilterBefore(this.jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
		
		
//		http.csrf(csrf -> csrf.disable());
		http.csrf(csrf -> csrf.ignoringRequestMatchers("/auth", "/api/**", "/oauth2/**"));
		
					

		// CORSFilter 활성화
		http.cors(cors /*CorsCofigurer*/->{
			CorsConfigurationSource corsSource = (request) -> {
				CorsConfiguration corsConfig = new CorsConfiguration();
				corsConfig.setAllowCredentials(true);
				corsConfig.setAllowedOrigins(List.of("http://localhost:5173")); // 누가 우리에게 요청할 것이
				corsConfig.setAllowedHeaders(List.of("*")); // 요청할 때 어떤 Request Header를 보낼것이냐
				corsConfig.setAllowedMethods(List.of("POST", "GET","PUT","FETCH", "OPTION","DELETE")); // 요청할 때 어떤 메소드로 요청할 것이냐.
				return corsConfig;
			}; 
			
			cors.configurationSource(corsSource);
		});
		
		//OAuth2
		http.oauth2Login(oAuthLogin -> {
	            oAuthLogin.userInfoEndpoint(config -> {
	                config.userService(this.securityOAtuhService);
	            });
	            oAuthLogin.successHandler(this.customOAuth2SuccessHandler);
	        });
		
//	    http.authorizeHttpRequests(auth -> auth
//	            // 캠페인 메인은 누구나 접근 가능
//	            .requestMatchers("/api/v1/campaign/main").permitAll()
//
//	            // 로그인/회원가입, 정적 리소스 등도 보통 열어둔다 (있으면 추가)
//	            .requestMatchers("/login", "/authenticate", "/css/**", "/js/**", "/img/**").permitAll()
//
//	            // (나중에 다른 API들을 인증 필요로 하고 싶으면 여기서 지정)
//	            // .requestMatchers("/api/**").authenticated()
//
//	            // 그 외는 일단 다 허용
//	            .anyRequest().permitAll()
//	        );
		
		// 인증과 관련된 필터에 대한 조건 명시.
		http.formLogin((formLogin) -> {
		//formLogin.defaultSuccessUrl("/list"))
		formLogin.loginPage("/login");
		// SecurityAuthenticationProvider가 동작될 Endpoint
		formLogin.loginProcessingUrl("/authenticate");
		formLogin.usernameParameter("logId");
		formLogin.passwordParameter("password");
		formLogin.successHandler(new LoginSuccessHandler(this.userDao));
		formLogin.failureHandler(new LoginFailureHandler(this.userDao));
		});
		return http.build();
	}
}
