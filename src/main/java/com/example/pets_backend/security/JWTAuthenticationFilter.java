package com.example.pets_backend.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.pets_backend.response.ResultData;
import com.example.pets_backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.LinkedHashMap;

import static com.example.pets_backend.ConstantValues.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final UserService userService;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, UserService userService) {
        super(authenticationManager);
        this.userService = userService;
        setFilterProcessesUrl(LOGIN);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        User scUser = (User) authResult.getPrincipal();
        String access_token = "Bearer " + generateAccessToken(request, scUser.getUsername(), scUser.getPassword());
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("token", access_token);
        String uid = userService.findByEmail(scUser.getUsername()).getUid();
        map.put("uid", uid);
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), ResultData.success(map));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(500);
        new ObjectMapper().writeValue(response.getOutputStream(), ResultData.fail(500, failed.getMessage()));
    }

    private String generateAccessToken(HttpServletRequest request, String username, String password) {
        return JWT.create()
                .withSubject(username)
                .withClaim("password", password)
                .withIssuer(request.getRequestURL().toString())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME_MILLIS))
                .sign(Algorithm.HMAC256(SECRET.getBytes(StandardCharsets.UTF_8)));
    }
}
