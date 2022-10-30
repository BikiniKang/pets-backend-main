package com.example.pets_backend.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.pets_backend.response.ResultData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.example.pets_backend.ConstantValues.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    public JWTAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, TokenExpiredException, ServletException {
        filterChain.doFilter(request, response);
        // TODO: activate Authorization when required
//        if (List.of(LOGIN, REGISTER, VERIFY).contains(request.getServletPath())) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//        try {
//            String authorizationHeader = request.getHeader(AUTHORIZATION);
//            String token = authorizationHeader.substring(AUTHORIZATION_PREFIX.length());
//            // parse the token
//            JWTVerifier verifier = JWT.require(ALGORITHM).build();
//            DecodedJWT decodedJWT = verifier.verify(token);
//            String email = decodedJWT.getSubject();
//            String password = decodedJWT.getClaim("password").asString();
//            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
//            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
//            // check the token
//            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password, authorities);
//            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//            // if no exceptions thrown, pass the filter
//            filterChain.doFilter(request, response);
//        } catch (TokenExpiredException exception) {
//            log.error(exception.getMessage());
//            response.setContentType(APPLICATION_JSON_VALUE);
//            response.setStatus(401);
//            new ObjectMapper().writeValue(response.getOutputStream(), ResultData.fail(401, exception.getMessage()));
//        } catch (Exception exception) {
//            log.error(exception.getMessage());
//            response.setContentType(APPLICATION_JSON_VALUE);
//            response.setStatus(500);
//            new ObjectMapper().writeValue(response.getOutputStream(), ResultData.fail(500, "Invalid token"));
//        }
    }
}
