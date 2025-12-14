package co.penny.dronedelivery.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Extracts Bearer tokens, validates them, and seeds the security context with the authenticated principal.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 1) Pull bearer token from the header if present.
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            // No bearer token present; proceed without touching security context.
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        try {
            // 2) Validate + parse claims. Any parsing problem bubbles as JwtException.
            Claims claims = jwtService.parseToken(token); // will throw if invalid/expired
            String username = claims.getSubject();
            String roleValue = claims.get("role", String.class);
            if (!StringUtils.hasText(username) || !StringUtils.hasText(roleValue)) {
                // Missing mandatory claims; let the chain continue without auth.
                filterChain.doFilter(request, response);
                return;
            }
            Role role = Role.valueOf(roleValue);
            String droneId = claims.get("droneId", String.class);
            UserPrincipal principal = new UserPrincipal(username, role, droneId);
            // Build an authentication token with a single ROLE_* authority derived from claim.
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(principal, token,
                            List.of(new SimpleGrantedAuthority("ROLE_" + role.name())));
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            // 3) Continue chain with authenticated context populated.
            filterChain.doFilter(request, response);
        } catch (IllegalArgumentException | JwtException e) {
            // Token invalid or expired: short-circuit with 401.
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or expired token");
        }
    }
}
