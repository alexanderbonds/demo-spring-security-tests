package ru.abondarenko.dev.spring.controllers.config;

import lombok.val;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import ru.abondarenko.dev.spring.model.UserInfo;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WithCustomUserSecurityContextConfig implements WithSecurityContextFactory<WithCustomUser> {
    @Override
    public SecurityContext createSecurityContext(WithCustomUser annotation) {
        val securityContext = SecurityContextHolder.createEmptyContext();
        val token = new UsernamePasswordAuthenticationToken(
                new UserInfo(
                        Long.parseLong(annotation.id()),
                        annotation.email()),
                null,
                Stream.of(annotation.authorities()).map(SimpleGrantedAuthority::new).collect(Collectors.toList())
        );
        securityContext.setAuthentication(token);

        return securityContext;
    }
}
