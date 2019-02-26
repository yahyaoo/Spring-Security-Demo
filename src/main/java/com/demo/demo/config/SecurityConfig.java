/*
 *  Copyright 2019 JDSchool Ltd.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.demo.demo.config;

import com.demo.demo.security.authentication.DemoUserDetailsAuthenticationProvider;
import com.demo.demo.security.filter.DemoAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Title: SecurityConfig
 * Description: TODO(这里用一句话描述这个类的作用)
 *
 * @author yuhong
 * @Company com.jdschool
 * @date 2019/2/25 11:36
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/css/**", "/index").permitAll()
                .antMatchers("/user/**").authenticated()
                .and()
                .formLogin().loginPage("/login")
                .and()
                .logout()
                .logoutUrl("/logout")
        ;
    }

    private DemoAuthenticationFilter authenticationFilter() throws Exception {
        DemoAuthenticationFilter authenticationFilter = new DemoAuthenticationFilter();
        authenticationFilter.setAuthenticationManager(authenticationManagerBean());
        authenticationFilter.setAuthenticationFailureHandler(simpleUrlAuthenticationFailureHandler());
        return authenticationFilter;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authenticationManagerBuilder) {
        authenticationManagerBuilder.authenticationProvider(authenticationProvider());

    }

    private AuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
//        daoAuthenticationProvider.setUserDetailsService(this.userDetailsService);
//        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        DemoUserDetailsAuthenticationProvider daoAuthenticationProvider = new DemoUserDetailsAuthenticationProvider(this.passwordEncoder(), this.userDetailsService);
        return daoAuthenticationProvider;
    }

    private SimpleUrlAuthenticationFailureHandler simpleUrlAuthenticationFailureHandler() {
        return new SimpleUrlAuthenticationFailureHandler("/login?error=true");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

}
