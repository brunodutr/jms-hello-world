package br.com.bdutra.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.ws.rs.core.MediaType;

@Retention(RUNTIME)
@Target({ METHOD })
public @interface JMSListener {

	String destination();
	String destinationResponse() default "";
	String destinationError() default "";
	String consumes() default MediaType.TEXT_PLAIN;
}
