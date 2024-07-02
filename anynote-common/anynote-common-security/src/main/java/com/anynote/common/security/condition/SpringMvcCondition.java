package com.anynote.common.security.condition;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Objects;


public class SpringMvcCondition implements Condition {


    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        // 检查是否存在 Spring MVC 的关键类

        return Objects.requireNonNull(context.getClassLoader()).getResource("org/springframework/web/servlet/DispatcherServlet.class") != null;
    }
}
