package annoyaml.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import annoyaml.interceptor.ISerializationInterceptor;

public class YAMLSerializationDefaultInterceptor implements
		ISerializationInterceptor {
	public Object serialize(YAML yamlAnnotation, Object containingObject,
			Method getterMethod, Field field, String yamlFieldName,
			Object yamlFieldValue) {
		return yamlFieldValue;
	}
}
