package org.example.expert.config;

import org.example.expert.domain.common.service.UserDetailsServiceImpl;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

	@Bean
	public UserDetailsService userDetailsService(UserRepository userRepository) {
		return new UserDetailsServiceImpl(userRepository);
	}

	@Bean
	public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder){
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder);
		return provider;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, JwtFilter jwtFilter) throws Exception{
		http
			// jwt는 세션을 쓰지 않아서 꺼둠
			.csrf(csrf -> csrf.disable())
			// JWT는 (stateless)무상태 인증이므로 session 생성안함
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
				// 로그인 요청 -> 누구나 접근 가능
				.requestMatchers("/auth/**").permitAll()
				// /admin/** -> 관리자만 접근 가능
				.requestMatchers("/admin/**").hasRole("ADMIN")
				// 나머지는 로그인 인증 팔요
				.anyRequest().authenticated()
			)
			// UsernamePasswordAuthenticationFilter 이전에 jwtFilter를 등록해서 JWT를 먼저 검사하고, 시큐리티 컨텍스트에 인증정보를 넣음
			.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
		return configuration.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder(){
		return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
	}

}
