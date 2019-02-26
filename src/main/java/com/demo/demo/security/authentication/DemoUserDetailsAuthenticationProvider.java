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

package com.demo.demo.security.authentication;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;

/**
 * Title: DemoUserDetailsAuthenticationProvider
 * Description: TODO(这里用一句话描述这个类的作用)
 *
 * @author yuhong
 * @Company com.jdschool
 * @date 2019/2/25 19:39
 */
public class DemoUserDetailsAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
    /**
     * The plaintext password used to perform
     * PasswordEncoder#matches(CharSequence, String)}  on when the user is
     * not found to avoid SEC-2056.
     */
    private static final String USER_NOT_FOUND_PASSWORD = "userNotFoundPassword";

    private PasswordEncoder passwordEncoder;
    private UserDetailsService userDetailsService;

    /**
     * The password used to perform
     * {@link PasswordEncoder#matches(CharSequence, String)} on when the user is
     * not found to avoid SEC-2056. This is necessary, because some
     * {@link PasswordEncoder} implementations will short circuit if the password is not
     * in a valid format.
     */
    private String userNotFoundEncodedPassword;

    public DemoUserDetailsAuthenticationProvider(PasswordEncoder passwordEncoder, UserDetailsService userDetailsService) {
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws AuthenticationException {
        if (usernamePasswordAuthenticationToken.getCredentials() == null) {
            logger.debug("Authentication failed: no credentials provided");
            throw new BadCredentialsException(messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }
        String presentedPassword = usernamePasswordAuthenticationToken.getCredentials().toString();
        if (!passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
            logger.debug("Authentication failed: password does not match stored value");
            throw new BadCredentialsException(messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }
    }

    @Override
    protected void doAfterPropertiesSet() throws Exception {
        Assert.notNull(this.userDetailsService, "A UserDetailsService must be set");
        this.userNotFoundEncodedPassword = this.passwordEncoder.encode(USER_NOT_FOUND_PASSWORD);
    }

    @Override
    protected UserDetails retrieveUser(String s, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws AuthenticationException {
        DemoAuthenticationToken demoAuthenticationToken = (DemoAuthenticationToken) usernamePasswordAuthenticationToken;
        UserDetails loadedUser;
        try {
            loadedUser = this.userDetailsService.loadUserByUsername(demoAuthenticationToken.getPrincipal().toString());
        } catch (UsernameNotFoundException notFound) {
            if (demoAuthenticationToken.getCredentials() != null) {
                String presentedPassword = demoAuthenticationToken.getCredentials().toString();
                this.passwordEncoder.matches(presentedPassword, userNotFoundEncodedPassword);
            }
            throw notFound;
        } catch (Exception repositoryProblem) {
            throw new InternalAuthenticationServiceException(repositoryProblem.getMessage(), repositoryProblem);
        }
        if (loadedUser == null) {
            throw new InternalAuthenticationServiceException("UserDetailsService returned null, " + "which is an interface contract violation");
        }
        return loadedUser;
    }
}
