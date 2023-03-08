package com.example.demo.core.security;

import com.example.demo.core.security.helpers.AuthorizationSchemas;
import com.example.demo.core.security.helpers.JwtProperties;
import com.example.demo.domain.user.UserDetailsImpl;
import com.example.demo.domain.user.command.UserCommandService;
import com.example.demo.domain.user.query.UserQueryService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private final UserQueryService userQueryService;
    private final JwtProperties jwtProperties;

    public JWTAuthorizationFilter(UserCommandService userCommandService, UserQueryService userQueryService, JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.userQueryService = userQueryService;
    }

    private String resolveToken(String token) {
        if (token != null && token.startsWith(AuthorizationSchemas.BEARER.toString())) {
            byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
            return Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(keyBytes))
                    .build()
                    .parseClaimsJws(token.replace(AuthorizationSchemas.BEARER + " ", ""))
                    .getBody()
                    .getSubject();
        } else {
            return null;
        }
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String authToken = request.getHeader(HttpHeaders.AUTHORIZATION);
            UserDetails userDetails = new UserDetailsImpl(userQueryService.findById(UUID.fromString(resolveToken(authToken))));
            SecurityContextHolder.getContext()
                    .setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, null,
                            userDetails.getAuthorities()));
        } catch (RuntimeException e) {
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request, response);
    }
}
