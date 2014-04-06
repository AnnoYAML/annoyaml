package annoyaml.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import annoyaml.interceptor.IDeserializationInterceptor;
import annoyaml.interceptor.ISerializationInterceptor;

@Target(value={ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface YAML {
	String value();
	boolean encrypt() default false;
	Class<?> targetType() default YAMLTargetTypeUnspecified.class;
	Class<? extends ISerializationInterceptor>[] serializationInterceptors() default YAMLSerializationDefaultInterceptor.class;
	Class<? extends IDeserializationInterceptor>[] deserializationInterceptors() default YAMLDeserializationDefaultInterceptor.class;
}
