package org.example.quizizz.security;

import org.example.quizizz.common.constants.PermissionCode;
import org.example.quizizz.service.Interface.IRedisService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final IRedisService redisService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String path = request.getRequestURI();

        if (
                path.startsWith("/api/v1/auth") ||
                        path.startsWith("/swagger-ui") ||
                        path.startsWith("/v3/api-docs") ||
                        path.startsWith("/swagger-resources") ||
                        path.startsWith("/webjars") ||
                        path.equals("/swagger-ui.html") ||
                        path.equals("/error")
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = request.getHeader("Authorization");
        if (token == null) {
            token = request.getHeader("accessToken");
        }

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);

            try {
                if (!jwtUtil.validateToken(token)) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token không hợp lệ");
                    return;
                }
                if (redisService.isTokenBlacklisted(token)) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token đã bị thu hồi");
                    return;
                }
                Long userId = jwtUtil.getUserIdFromToken(token);
                Collection<SimpleGrantedAuthority> authorities = getAuthoritiesFromRedis(userId);

                UserDetails userDetails = User.withUsername(userId.toString())
                        .password("")
                        .authorities(authorities)
                        .build();

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, authorities
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token không hợp lệ");
                return;
            }
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Thiếu token xác thực");
            return;
        }
        filterChain.doFilter(request, response);
    }

    private Collection<SimpleGrantedAuthority> getAuthoritiesFromRedis(Long userId) {
        Set<PermissionCode> permissions = redisService.getUserPermissions(userId);
        if (permissions == null || permissions.isEmpty()) {
            return Collections.emptyList();
        }

        Collection<SimpleGrantedAuthority> authorities = permissions.stream()
                .map(PermissionCode::getCode)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return authorities;
    }
}
