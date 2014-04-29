package annoyaml.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

import annoyaml.exception.AnnoYAMLException;

public class ReflectionUtil {
	public static <T> T instantiateClass(Class<T> clazz) {
		try {
			return clazz.newInstance();
		} catch (Exception e) {
			throw new AnnoYAMLException(e);
		}
	}
	
	public static PropertyDescriptor[] listPropertyDescriptors(Object o) {
		final BeanInfo beanInfo;
		try {
			beanInfo = Introspector.getBeanInfo(o.getClass());
		} catch (IntrospectionException e) {
			throw new AnnoYAMLException(e);
		}

		PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
		return descriptors;
	}

	public static Field resolveField(Class<?> inputClass, String fieldName) {
		if (Class.class.equals(inputClass) || inputClass == null
				|| inputClass.isInterface()) {
			return null;
		}
	
		Field field = null;
	
		try {
			field = inputClass.getDeclaredField(fieldName);
			field.setAccessible(true);
		} catch (SecurityException e) {
			throw new AnnoYAMLException(e);
		} catch (NoSuchFieldException e) {
			// ignore
		}
	
		if (field == null) {
			field = ReflectionUtil.resolveField(inputClass.getSuperclass(), fieldName);
		}
	
		return field;
	}
}
