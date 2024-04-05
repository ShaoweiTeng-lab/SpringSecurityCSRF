package com.kucw.security.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
@EnableWebSecurity
public class MySecurityConfig {

    @Bean
    public InMemoryUserDetailsManager userDetailsManager() {
        UserDetails userTest1 = User
                .withUsername("test1")
                .password("{noop}111")
                .roles("ADMIN", "USER")
                .build();

        UserDetails userTest2 = User
                .withUsername("test2")
                .password("{noop}222")
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(userTest1, userTest2);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                // 設定 Session 的創建機制
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
                // 設定CSRF 保護 , 當前端發post put  delete 時 需檢查 XSRF-TOKEN
                .csrf(csrf ->// 表示在 cookie 中回傳 XSRF-TOKEN 給前端
                        csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                                .csrfTokenRequestHandler(csrfTokenRequestAttributeHandler())
                                // 忽略特定api
                                .ignoringRequestMatchers("/register","/userLogin")
                 )
                .httpBasic(Customizer.withDefaults())
                .formLogin(Customizer.withDefaults())

                .authorizeHttpRequests(request -> request
                        .requestMatchers("/register","/userLogin").permitAll()
                        .anyRequest().authenticated()
                )

                .build();
    }
    private CsrfTokenRequestAttributeHandler csrfTokenRequestAttributeHandler(){
        CsrfTokenRequestAttributeHandler csrfTokenRequestAttributeHandler = new CsrfTokenRequestAttributeHandler();
        csrfTokenRequestAttributeHandler.setCsrfRequestAttributeName(null);
        return csrfTokenRequestAttributeHandler;
    }
}
