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

import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Title: DemoAuthenticationToken
 * Description: TODO(这里用一句话描述这个类的作用)
 *
 * @author yuhong
 * @Company com.jdschool
 * @date 2019/2/25 19:20
 */
public class DemoAuthenticationToken extends UsernamePasswordAuthenticationToken {

    @Getter
    private String domain;

    public DemoAuthenticationToken(Object principal, Object credentials, String domain) {
        super(principal, credentials);
        this.domain = domain;
        super.setAuthenticated(false);
    }

    public DemoAuthenticationToken(Object principal, Object credentials, String domain,
                                   Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
        this.domain = domain;
//        必须手动
        super.setAuthenticated(true);
    }

}
