package annoyaml.interceptor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import annoyaml.annotation.YAML;

public interface ISerializationInterceptor {
	public Object serialize(YAML yamlAnnotation, Object containingObject, Method getterMethod, Field field, String yamlFieldName, Object yamlFieldValue);
}
