package annoyaml.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import annoyaml.interceptor.IDeserializationInterceptor;

public class YAMLDeserializationDefaultInterceptor implements
		IDeserializationInterceptor {
	public Object deserialize(YAML yamlAnnotation, Object containingObject,
			Field field, String yamlFieldName, Object yamlFieldValue) {
		return null;
	}
	
	public Object deserialize(YAML yamlAnnotation, Object containingObject,
			Method setterMethod, String yamlFieldName, Object yamlFieldValue) {
		return null;
	}
}
