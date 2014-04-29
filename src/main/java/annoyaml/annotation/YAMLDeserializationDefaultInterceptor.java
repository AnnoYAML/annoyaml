package annoyaml.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import annoyaml.interceptor.IDeserializationInterceptor;

public class YAMLDeserializationDefaultInterceptor implements
		IDeserializationInterceptor {
	public Object deserialize(YAML yamlAnnotation, Object containingObject,
			Method setterMethod, Field field, String yamlFieldName,
			Object yamlFieldValue) {
		return yamlFieldValue;
	}
}
