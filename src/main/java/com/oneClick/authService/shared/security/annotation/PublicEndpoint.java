package com.oneClick.authService.shared.security.annotation;

// src/main/java/com/oneClick/authService_be/annotation/PublicEndpoint.java

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PublicEndpoint {
}
