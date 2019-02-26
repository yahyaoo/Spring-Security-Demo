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

package com.demo.demo.controller;

import com.demo.demo.entity.DemoUser;
import com.demo.demo.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

/**
 * Title: WebController
 * Description: TODO(这里用一句话描述这个类的作用)
 *
 * @author yuhong
 * @Company com.jdschool
 * @date 2019/2/25 13:23
 */
@Controller
public class WebController {

    private final TestService testService;

    @Autowired
    public WebController(TestService testService) {
        this.testService = testService;
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/index";
    }

    @RequestMapping("/index")
    @Secured("ROLE_USER")
    public String index(Model model) {
        getDomain().ifPresent(d -> model.addAttribute("domain", d));
        return "index";
    }

    @RequestMapping("/user/index")
    public String userIndex(Model model, @AuthenticationPrincipal DemoUser user) {
        getDomain().ifPresent(d -> model.addAttribute("domain", d));

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            DemoUser userDetails = (DemoUser) principal;
            System.out.println(userDetails.getDomain() + ",username:" + userDetails.getUsername() + ",password:" + userDetails.getPassword());
        }

        String demo = this.testService.demo(user);
        System.out.println(demo);

        for (GrantedAuthority grantedAuthority : user.getAuthorities()) {
            System.out.println(grantedAuthority.getAuthority());
        }
        return "user/index";
    }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    private Optional<String> getDomain() {
        Authentication auth = SecurityContextHolder.getContext()
                .getAuthentication();
        String domain = null;
        if (auth != null && !auth.getClass().equals(AnonymousAuthenticationToken.class)) {
            DemoUser user = (DemoUser) auth.getPrincipal();
            domain = user.getDomain();
        }
        return Optional.ofNullable(domain);
    }

}
