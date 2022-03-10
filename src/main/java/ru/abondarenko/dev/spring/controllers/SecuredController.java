package ru.abondarenko.dev.spring.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.abondarenko.dev.spring.model.SecretRequest;
import ru.abondarenko.dev.spring.model.UserInfo;

@RestController
@RequestMapping("/secured")
public class SecuredController {

    @GetMapping
    public String getUnsecuredInfo() {
        return "not-secured-string";
    }

    @GetMapping("/has_role")
    @PreAuthorize("hasRole('ROLE_READ_SECURED_INFO')")
    public String getSecuredByRoleInfo() {
        return "secret-string-for-role";
    }

    @GetMapping("/authenticated")
    @PreAuthorize("isAuthenticated()")
    public String getSecuredByAuthenticationInfo(@AuthenticationPrincipal UserInfo user) {
        return "secret-string-for-user-" + user.getEmail();
    }

    @PostMapping("/has_email")
    @PreAuthorize("#secretRequest.email == authentication.principal.email")
    public String getSecuredByEmailInfo(@RequestBody SecretRequest secretRequest) {
        return "secret-string-for-request-" + secretRequest.getQuery();
    }
}
