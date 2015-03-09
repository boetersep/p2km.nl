package cc.boeters.p2000monitor.support;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD,
		ElementType.PARAMETER })
public @interface Stage {
	cc.boeters.p2000monitor.support.StageResolver.Stage value() default cc.boeters.p2000monitor.support.StageResolver.Stage.PRODUCTION;
}
