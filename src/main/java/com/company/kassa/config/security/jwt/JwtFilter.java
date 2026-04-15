package com.company.kassa.config.security.jwt;

import com.company.kassa.config.security.CustomUserDetailService;
import com.company.kassa.config.security.UserPrincipal;
import com.company.kassa.utils.TenantContext;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final CustomUserDetailService userDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String jwt = authHeader.substring(7);

        try {
            Claims claims = jwtService.extractAllClaims(jwt);

            if (!"access".equals(claims.get("tokenType"))) {
                sendUnauthorized(response, "Access token required");
                return;
            }

            String username = claims.getSubject();
            Long yattId = claims.get("yattId", Long.class);

            UserPrincipal userDetails =
                    (UserPrincipal) userDetailsService.loadUserByUsernameAndYattId(username, yattId);

            if (!jwtService.isAccessTokenValid(jwt, userDetails)) {
                sendUnauthorized(response, "Invalid or expired token");
                return;
            }

            TenantContext.setTenantId(yattId);

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );

            SecurityContextHolder.getContext().setAuthentication(authToken);

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            e.printStackTrace(); // 🔥 qo‘sh
            SecurityContextHolder.clearContext();
            sendUnauthorized(response, "Invalid token");
        } finally {
            TenantContext.clear();
        }
    }

    private String generateDeviceKey(String ip, String userAgent, Long userId) {
        String raw = userId + "|" + ip + "|" + userAgent;
        return Integer.toHexString(raw.hashCode());
    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("""
                {
                  "success": false,
                  "message": "%s"
                }
                """.formatted(message));
    }
}
