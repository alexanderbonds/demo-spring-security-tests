package ru.abondarenko.dev.spring.controllers.config;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithCustomUserSecurityContextConfig.class)
public @interface WithCustomUser {
    String id();
    String email();
    String[] authorities();
}
