package com.example.StepByStep.configs;

import com.example.StepByStep.entities.CustomOAuth2User;
import com.example.StepByStep.services.CustomOAuth2UserService;
import com.example.StepByStep.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {
    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomOAuth2UserService oauthUserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests()
                .requestMatchers("/registration", "/login", "/activate/*", "/oauth/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .and().logout().permitAll()
                .and()
                .oauth2Login()
                .loginPage("/login").defaultSuccessUrl("/")
                .userInfoEndpoint()
                .userService(oauthUserService)
                .and()
                .successHandler((request, response, authentication) -> {
                    CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
                    UserDetails user = null;
                    try {
                        user = userService.loadUserByUsername(oauthUser.getName());
                    } catch (UsernameNotFoundException e) {
                        userService.processOAuthPostLogin(oauthUser.getEmail(), oauthUser.getName());
                        user = userService.loadUserByUsername(oauthUser.getName());
                    }
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    try {
                        response.sendRedirect("/");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
        return http.build();
    }
    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userService).passwordEncoder(passwordEncoder)
                .and().build();
    }
}
