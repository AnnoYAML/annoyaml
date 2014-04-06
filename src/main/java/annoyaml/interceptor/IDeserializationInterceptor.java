package annoyaml.interceptor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import annoyaml.annotation.YAML;

public interface IDeserializationInterceptor {
	public Object deserialize(YAML yamlAnnotation, Object containingObject, Method setterMethod, String yamlFieldName, Object yamlFieldValue);
	public Object deserialize(YAML yamlAnnotation, Object containingObject, Field field, String yamlFieldName, Object yamlFieldValue);
}
