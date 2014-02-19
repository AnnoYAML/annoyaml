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
import annoyaml.annotation.YAMLFlatten;
import annoyaml.annotation.YAMLSerializable;
import annoyaml.annotation.YAMLSkip;
import annoyaml.exception.AnnoYAMLException;
import annoyaml.util.AnnoYAMLUtil;

public class AnnoYAMLSerializer {
	
	private YAMLConfiguration yamlConfiguration;
	
	public AnnoYAMLSerializer() {
		this.yamlConfiguration = new YAMLConfiguration();
	}
	
	public AnnoYAMLSerializer(YAMLConfiguration yamlConfiguration) {
		this.yamlConfiguration = yamlConfiguration;
	}
	
	public String serialize(Object o) {
		Map<Object, Object> map = serializeToMap(o);
		Yaml yaml = new Yaml();
		StringWriter sw = new StringWriter();
		yaml.dump(map, sw);
		return sw.toString();
	}
	
	public Map<Object, Object> serializeToMap(Object o) {
		return serializeToMap(new IdentityHashMap<Object, Boolean>(), o);
	}
	@SuppressWarnings("rawtypes")
	public Map<Object, Object> serializeToMap(IdentityHashMap<Object, Boolean> cycleCheck, Object o) {
		Map<Object, Object> yamlMap = new HashMap<Object, Object>();

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
			throw new AnnoYAMLException(e);
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
			Field field = AnnoYAMLUtil.resolveField(type, name);

			YAML yamlAnnotation = method.getAnnotation(YAML.class);
			YAMLFlatten yamlFlatten = method.getAnnotation(YAMLFlatten.class);
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
				if (yamlFlatten == null) {
					yamlFlatten = field.getAnnotation(YAMLFlatten.class);
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
			
			// prevent redoing the same values
			if (cycleCheck.get(value) != null) {
				continue;
			}

			// recursively descend into further classes
			// marked with the PuppetSerializable annotation
			if (serializableAnnotation != null) {
				Map<Object, Object> subMap = serializeToMap(cycleCheck, value);
				if (yamlFlatten != null || yamlAnnotation == null) {
					yamlMap.putAll(subMap);
				} else {
					yamlMap.put(yamlAnnotation.value(), subMap);
				}
			} else if (yamlAnnotation != null) {
				if (value instanceof Collection) {
					List<Object> objects = new ArrayList<Object>();
					for (Object subVal : (Collection)value) {
						Object entryValue = isBasicType(subVal) ? subVal : serializeToMap(cycleCheck, subVal);
						objects.add(entryValue);
					}
					yamlMap.put(yamlAnnotation.value(), objects);
				} else if (value instanceof Object[]) {
					List<Object> objects = new ArrayList<Object>();
					for (Object subVal : (Object[])value) {
						Object entryValue = isBasicType(subVal) ? subVal : serializeToMap(cycleCheck, subVal);
						objects.add(entryValue);
					}
					yamlMap.put(yamlAnnotation.value(), objects);
				} else if (value instanceof Map) {
					@SuppressWarnings("unchecked")
					Map<Object, Object> inputMap = (Map<Object, Object>)value;
					Map<Object, Object> outputMap = new HashMap<Object, Object>();
					for (Map.Entry<Object, Object> entry : inputMap.entrySet()) {
						Object entryValue = isBasicType(entry.getValue()) ? entry.getValue() : serializeToMap(cycleCheck, entry.getValue()); 
						outputMap.put(entry.getKey(), entryValue);
					}
					if (yamlFlatten != null) {
						for (Map.Entry<Object, Object> entry : outputMap.entrySet()) {
							yamlMap.put(yamlAnnotation.value() + "::" + entry.getKey(), entry.getValue());
						}
					} else {
						yamlMap.put(yamlAnnotation.value(), outputMap);
					}
				} else {
					Object resultValue = value;
					if (isYAMLSerializable(resultValue)) {
						resultValue = serializeToMap(cycleCheck, resultValue);
						yamlMap.put(yamlAnnotation.value(), resultValue);
					} else {
						if (value.getClass().isEnum()) {
							resultValue = value.toString();
						} else {
							resultValue = value;
						}
						if (yamlAnnotation.encrypt() && yamlConfiguration.getEncryptor() != null) {
							resultValue = yamlConfiguration.getEncryptor().encrypt(resultValue.toString());
						}
						yamlMap.put(yamlAnnotation.value(), resultValue);
					}
				}
			}
		}
		return yamlMap;
	}
	
	private boolean isBasicType(Object val) {
		return val == null || 
				val instanceof String || 
				val instanceof Boolean || 
				val instanceof Byte || 
				val instanceof Character || 
				val instanceof Long || 
				val instanceof Double || 
				val instanceof Float || 
				val instanceof Integer;
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
