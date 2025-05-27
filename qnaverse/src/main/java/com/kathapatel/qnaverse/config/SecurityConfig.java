package com.kathapatel.qnaverse.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.kathapatel.qnaverse.service.CustomUserDetailsService;



@Configuration
@EnableWebSecurity  // activates spring security 
public class SecurityConfig {
	
	// injecting custom service 
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    
    //password encoder uses BCrypt to store hashing algorithms 
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    //configure the primary authentication provider and links user service and password encoder together 
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        
        authProvider.setUserDetailsService(customUserDetailsService);
        
       
        authProvider.setPasswordEncoder(passwordEncoder());
        
        return authProvider;
    }
    
    /**
     * Defines the main security rules for HTTP requests
     * This is where we specify which URLs require login and how the login process works
     */

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(authenticationProvider()) 
            .authorizeHttpRequests(auth -> auth
            		// These specific paths should be accessible to everyone (no login required)
            		.requestMatchers(
                            "/", 
                            "/css/**",
                            "/js/**",
                            "/images/**", 
                            "/webjars/**", 
                            "/error",     
                            "/users/login",
                            "/users/register",
                            "/users/save", 
                            "/users/forgot-password",
                            "/users/reset-password",
                            "/WEB-INF/views/login.jsp",
                            "/WEB-INF/views/register.jsp",
                            "/WEB-INF/views/forgot-password.jsp",
                            "/WEB-INF/views/reset-password.jsp"
                         ).permitAll()
            		// Any other request not listed above must be authenticated (user needs to be logged in)
        		.anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/users/login")
                .loginProcessingUrl("/users/login") 
                .usernameParameter("email")
                .passwordParameter("password")
                .defaultSuccessUrl("/home", true) 
                .permitAll() 
            )
            .logout(logout -> logout
                .logoutUrl("/users/logout") 
                .logoutSuccessUrl("/users/login?logout") 
                .invalidateHttpSession(true) 
                .deleteCookies("JSESSIONID") 
                .permitAll() 
            );
        return http.build();
    }
}
