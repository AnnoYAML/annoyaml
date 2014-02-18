package annoyaml;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import annoyaml.annotation.YAML;
import annoyaml.annotation.YAMLSerializable;
import annoyaml.annotation.YAMLSkip;

public class AnnoYAMLSerializer {
	
	private YAMLConfiguration yamlConfiguration;
	
	public AnnoYAMLSerializer() {
		this.yamlConfiguration = new YAMLConfiguration();
	}
	
	public AnnoYAMLSerializer(YAMLConfiguration yamlConfiguration) {
		this.yamlConfiguration = yamlConfiguration;
	}
	
	public String serialize(Object o) {
		Map<String, Object> map = serializeToMap(o);
		Yaml yaml = new Yaml();
		StringWriter sw = new StringWriter();
		yaml.dump(map, sw);
		return sw.toString();
	}
	
	public Map<String, Object> serializeToMap(Object o) {
		return serializeToMap(new IdentityHashMap<Object, Boolean>(), o);
	}
	public Map<String, Object> serializeToMap(IdentityHashMap<Object, Boolean> cycleCheck, Object o) {
		Map<String, Object> yamlMap = new HashMap<String, Object>();

		// Make sure we haven't seen this object before (prevents cycles)
		if (cycleCheck.get(o) != null) {
			return yamlMap;
		} else {
			cycleCheck.put(o, true);
		}
		
		// Make sure object is marked YAMLSerializable
		if (!isYAMLSerializable(o)) {
			return yamlMap;
		}

		final BeanInfo beanInfo;
		try {
			beanInfo = Introspector.getBeanInfo(o.getClass());
		} catch (IntrospectionException e) {
			throw new RuntimeException(e);
		}

		Class<?> type = o.getClass();
		PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();

		// Search all bean properties for YAML annotations
		for (PropertyDescriptor descriptor : descriptors) {
			String name = descriptor.getName();

			// ignore the "getClass" method
			if ("class".equalsIgnoreCase(name)) {
				continue;
			}

			Class<?> childType = descriptor.getPropertyType();

			// Skip the property if it is marked with @YAMLSkip
			if (descriptor.getReadMethod().getAnnotation(YAMLSkip.class) != null) {
				continue;
			}
			
			Method method = descriptor.getReadMethod();
			Field field = this.resolveField(type, name);

			YAML yamlAnnotation = method.getAnnotation(YAML.class);
			YAMLSerializable serializableAnnotation = childType.getAnnotation(YAMLSerializable.class);

			// if we found the field, then use it
			if (field != null) {
				// check for skip and continue if found
				if(field.getAnnotation(YAMLSkip.class) != null) {
					continue;
				}
				
				// if we didn't get a method annotation, check for a field one
				if (yamlAnnotation == null) {
					yamlAnnotation = field.getAnnotation(YAML.class);
				}
			}

			// If no yaml annotation exists and the child is not yaml serializable,
			// then continue
			if (yamlAnnotation == null && serializableAnnotation == null) {
				continue;
			}

			Object value = this.getValueOfProperty(o, method, field);

			// Ignore null values
			if (value == null) {
				continue;
			}

			// recursively descend into further classes
			// marked with the PuppetSerializable annotation
			if (serializableAnnotation != null) {
				Map<String, Object> subMap = serializeToMap(cycleCheck, value);
				subMap.put(yamlAnnotation.value(), subMap);
			} else if (yamlAnnotation != null) {
				if (value instanceof Collection) {
					List<Object> objects = new ArrayList<Object>();
					for (Object subVal : (Collection)value) {
						objects.add(serializeToMap(cycleCheck, subVal));
					}
					yamlMap.put(yamlAnnotation.value(), objects);
				} else if (value instanceof Object[]) {
					List<Object> objects = new ArrayList<Object>();
					for (Object subVal : (Object[])value) {
						objects.add(serializeToMap(cycleCheck, subVal));
					}
					yamlMap.put(yamlAnnotation.value(), objects);
				} else if (value instanceof Map) {
					Map<Object, Object> inputMap = (Map<Object, Object>)value;
					Map<Object, Object> outputMap = new HashMap<Object, Object>();
					for (Map.Entry<Object, Object> entry : inputMap.entrySet()) {
						outputMap.put(entry.getKey(), serializeToMap(cycleCheck, entry.getValue()));
					}
					yamlMap.put(yamlAnnotation.value(), outputMap);
				} else {
					String lineString = value.toString();
					if (yamlAnnotation.encrypt() && yamlConfiguration.getEncryptor() != null) {
						lineString = yamlConfiguration.getEncryptor().encrypt(lineString);
					}
					yamlMap.put(yamlAnnotation.value(), lineString);
				}
			}
		}
		return yamlMap;
	}

	/**
	 * Retrieve the value from the getter method or the field (depending
	 * upon which is available)
	 * 
	 * @param o
	 * @param method
	 * @param field
	 * @return
	 */
	private Object getValueOfProperty(Object o, Method method, Field field) {
		// read!
		Object value = null;
		try {
			method.setAccessible(true);
			value = method.invoke(o, new Object[] {});
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}

		// read from property directly if the value
		// is still null
		if (value == null) {
			if (field != null) {
				try {
					field.setAccessible(true);
					value = field.get(o);
				} catch (IllegalArgumentException e) {
				} catch (IllegalAccessException e) {
				}
			}
		}

		return value;
	}

	private Field resolveField(Class<?> inputClass, String fieldName) {
		if (Class.class.equals(inputClass) || inputClass == null
				|| inputClass.isInterface()) {
			return null;
		}

		Field field = null;

		try {
			field = inputClass.getDeclaredField(fieldName);
			field.setAccessible(true);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchFieldException e) {
			// ignore
		}

		if (field == null) {
			field = this.resolveField(inputClass.getSuperclass(), fieldName);
		}

		return field;
	}

	private boolean isYAMLSerializable(Object o) {
		Annotation annotation = o.getClass().getAnnotation(YAMLSerializable.class);
		return annotation != null;
	}
	
	public void setYamlConfiguration(YAMLConfiguration yamlConfiguration) {
		this.yamlConfiguration = yamlConfiguration;
	}
	
	public YAMLConfiguration getYamlConfiguration() {
		return yamlConfiguration;
	}
}
