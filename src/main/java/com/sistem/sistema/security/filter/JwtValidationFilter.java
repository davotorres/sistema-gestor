package com.sistem.sistema.security.filter;



import static com.sistem.sistema.security.TokenJwtConfig.CONTENT_TYPE;
import static com.sistem.sistema.security.TokenJwtConfig.HEADER_AUTHORIZATION;
import static com.sistem.sistema.security.TokenJwtConfig.PREFIX_TOKEN;
import static com.sistem.sistema.security.TokenJwtConfig.SECRET_KEY;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sistem.sistema.security.SimpleGrantedAuthorityJson;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtValidationFilter extends BasicAuthenticationFilter{

    public JwtValidationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

            String header = request.getHeader(HEADER_AUTHORIZATION);


            //Si no esta el token o el token no comienza con "Bearer " la aplicacion no le dara acceso al sistema
            if(header == null || !header.startsWith(PREFIX_TOKEN)){
                chain.doFilter(request, response);
                return;
            }

            String token = header.replace(PREFIX_TOKEN, "");

            try {
                Claims claim = Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token).getPayload();
                String username = claim.getSubject();
                Object authoritiesClaim = claim.get("authorities");

                Collection<? extends GrantedAuthority> authorities = Arrays.asList(
                    new ObjectMapper()
                    .addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityJson.class)
                    .readValue(authoritiesClaim.toString().getBytes(), SimpleGrantedAuthority[].class)
                    );

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                chain.doFilter(request, response);


            } catch (JwtException e) {
                Map<String, String> body = new HashMap<>();

                body.put("error", e.getMessage());
                body.put("message", "El Jwt es invalido!");

                response.getWriter().write(new ObjectMapper().writeValueAsString(body));
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType(CONTENT_TYPE);
            }
    }
    

}
