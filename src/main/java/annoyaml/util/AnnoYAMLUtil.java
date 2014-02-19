package annoyaml.util;

import java.lang.reflect.Field;

import annoyaml.exception.AnnoYAMLException;

public class AnnoYAMLUtil {
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
			field = AnnoYAMLUtil.resolveField(inputClass.getSuperclass(), fieldName);
		}

		return field;
	}
}
