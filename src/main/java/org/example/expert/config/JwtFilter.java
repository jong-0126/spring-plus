package org.example.expert.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        if(requestURI.startsWith("/auth")){
            filterChain.doFilter(request, response);
            return;
        }

        String token = jwtUtil.resolveToken(request);

        if(token != null && jwtUtil.validateToken(token)){
            try{
                Claims claims = jwtUtil.extractClaims(token);

                Long userId = Long.parseLong(claims.getSubject());
                String email = claims.get("email", String.class);
                String userRole = claims.get("userRole", String.class);

                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + userRole);
                UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(email, null, Collections.singletonList(authority));

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }catch (Exception e){
                log.warn("JWT 처리 중 오류 발생: {}", e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "토큰 처리 실패");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
