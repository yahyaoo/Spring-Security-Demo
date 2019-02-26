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

package com.demo.demo.security.filter;

import com.demo.demo.security.authentication.DemoAuthenticationToken;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Title: DemoAuthenticationFilter
 * Description: TODO(这里用一句话描述这个类的作用)
 *
 * @author yuhong
 * @Company com.jdschool
 * @date 2019/2/25 13:16
 */
public class DemoAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private static final String SPRING_SECURITY_FORM_DOMAIN_KEY = "domain";

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        UsernamePasswordAuthenticationToken authRequest = getAuthRequest(request);
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    private UsernamePasswordAuthenticationToken getAuthRequest(HttpServletRequest request) {
        String username = obtainUsername(request);
        String password = obtainPassword(request);
        String domain = obtainDomain(request);

        if (username == null) {
            username = "";
        }
        if (password == null) {
            password = "";
        }
        if (domain == null) {
            domain = "";
        }

//        String usernameDomain = String.format("%s%s%s", username.trim(), String.valueOf(Character.LINE_SEPARATOR), domain);
//        return new UsernamePasswordAuthenticationToken(usernameDomain, password);
        return new DemoAuthenticationToken(username, password, domain);
    }

    private String obtainDomain(HttpServletRequest request) {
        return request.getParameter(SPRING_SECURITY_FORM_DOMAIN_KEY);
    }
}
