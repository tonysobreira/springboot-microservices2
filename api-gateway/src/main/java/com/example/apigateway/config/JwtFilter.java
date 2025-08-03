package com.example.apigateway.config;

import java.security.Key;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtFilter extends AbstractGatewayFilterFactory<JwtFilter.Config> {

	@Value("${jwt.secret}")
	private String secret;

	public JwtFilter() {
		super(Config.class);
	}

	@Override
	public GatewayFilter apply(Config config) {
		return (exchange, chain) -> {
			// Get the original request path before any filters (e.g., StripPrefix)
            String originalPath = exchange.getRequest().getPath().toString();
            String pathPattern = config.getPathPattern();
            // Log for debugging
            System.out.println("JwtFilter: originalPath=" + originalPath + ", pathPattern=" + pathPattern);

            // Skip JWT validation for /auth/** or if pathPattern is null
            if (pathPattern == null || originalPath.startsWith("/auth/")) {
                System.out.println("JwtFilter: Skipping JWT validation for " + originalPath);
                return chain.filter(exchange);
            }

            // Apply JWT validation for paths matching the pattern (e.g., /users/**, /products/**)
            String regexPattern = pathPattern.replace("/**", "(?:/.*)?");
            if (!originalPath.matches(regexPattern)) {
                System.out.println("JwtFilter: Path " + originalPath + " does not match " + regexPattern + ", skipping validation");
                return chain.filter(exchange);
            }

			
            // Proceed with JWT validation
            System.out.println("JwtFilter: Validating JWT for " + originalPath);
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                System.out.println("JwtFilter: Missing or invalid Authorization header for " + originalPath);
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String token = authHeader.substring(7);
            try {
            	Jwts.parserBuilder().setSigningKey(getSignKey()).build().parse(token);
                System.out.println("JwtFilter: JWT validated successfully for " + originalPath);
                return chain.filter(exchange);
            } catch (Exception e) {
                System.out.println("JwtFilter: JWT validation failed for " + originalPath + ": " + e.getMessage());
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            
		};
	}
	
	private Key getSignKey() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
	}

	public static class Config {
		private String pathPattern;

		public String getPathPattern() {
			return pathPattern;
		}

		public void setPathPattern(String pathPattern) {
			this.pathPattern = pathPattern;
		}
	}
}