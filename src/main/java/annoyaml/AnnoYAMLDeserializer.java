package annoyaml;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import annoyaml.annotation.YAML;
import annoyaml.annotation.YAMLTargetTypeUnspecified;
import annoyaml.exception.AnnoYAMLException;
import annoyaml.interceptor.IDeserializationInterceptor;
import annoyaml.interceptor.ISerializationInterceptor;
import annoyaml.util.ReflectionUtil;

public class AnnoYAMLDeserializer {

	public <T extends Object> T deserialize(Class<T> clazz, String yamlDocument) {
		InputStream is = new ByteArrayInputStream(yamlDocument.getBytes());
		return deserialize(clazz, is);
	}
	
	public <T extends Object> T deserialize(Class<T> clazz, InputStream is) {
		Yaml yaml = new Yaml();
		@SuppressWarnings({ "unchecked" })
		Map<Object, Object> mainMap = (Map<Object, Object>)yaml.load(is);
		return deserialize(clazz, mainMap); 
	}
	
	private <T extends Object> T deserialize(Class<T> clazz, Map<Object, Object> map) {
		T result;
		try {
			result = clazz.newInstance();
		} catch (InstantiationException e) {
			throw new YAMLException(e);
		} catch (IllegalAccessException e) {
			throw new YAMLException(e);
		}
		
		for (Map.Entry<Object, Object> entry : map.entrySet()) {
			String key = (String)entry.getKey();
			Object value = entry.getValue();
			
			setValue(result, key, value);
		}
		
		return result;
	}
	
	private void setValue(Object obj, String key, Object value) {
		if (value == null) {
			// ignore
			return;
		}
		
		Class<?> type = obj.getClass();
		PropertyDescriptor[] descriptors = ReflectionUtil.listPropertyDescriptors(obj);
		for (PropertyDescriptor descriptor : descriptors) {
			Field field = ReflectionUtil.resolveField(type, descriptor.getName());
			Method writeMethod = descriptor.getWriteMethod();
			Method readMethod = descriptor.getReadMethod();
			
			YAML yamlAnn = field != null ? field.getAnnotation(YAML.class) : null;
			if (yamlAnn == null) {
				yamlAnn = readMethod != null ? readMethod.getAnnotation(YAML.class) : null;
			}
			if (yamlAnn == null) {
				yamlAnn = writeMethod != null ? writeMethod.getAnnotation(YAML.class) : null;
			}
			
			if (yamlAnn != null && yamlAnn.value().equals(key)) {
				Class expectedType = getExpectedType(field, writeMethod);
				
				Class<? extends IDeserializationInterceptor>[] interceptorClasses = yamlAnn.deserializationInterceptors();
				for (Class<? extends IDeserializationInterceptor> interceptorClass : interceptorClasses) {
					IDeserializationInterceptor deserializationInterceptor = ReflectionUtil.instantiateClass(interceptorClass);
					value = deserializationInterceptor.deserialize(yamlAnn, obj, writeMethod, field, descriptor.getName(), value);
				}
				
				if (Collection.class.isAssignableFrom(expectedType)) {
					try {
						Class actualExpectedType = expectedType;
						if (expectedType.isInterface()) {
							actualExpectedType = ArrayList.class;
						}
						Collection coll = (Collection)actualExpectedType.newInstance();
						Class collParameterType =  yamlAnn.targetType();
						if (collParameterType == YAMLTargetTypeUnspecified.class) {
							throw new YAMLException("Target type must be specified for YAML property: " + key);
						}
						for (Object subVal : (Collection)value) {
							Object deserialized = deserialize(collParameterType, (Map)subVal);
							coll.add(deserialized);
						}
						value = coll;
					} catch (IllegalAccessException e) {
						throw new YAMLException(e);
					} catch (InstantiationException e) {
						throw new YAMLException(e);
					}					
				} else if (Object[].class.isAssignableFrom(expectedType)) {
					Object[] array = (Object[])Array.newInstance(expectedType.getComponentType(), ((Object[])value).length);
					Class collParameterType =  expectedType.getComponentType();
					int i = 0;
					for (Object subVal : (Object[])value) {
						Object deserialized = deserialize(collParameterType, (Map)subVal);
						array[i] = deserialized;
						i++;
					}
					value = array;
				} else if (Map.class.isAssignableFrom((expectedType))) {
					Map<String, Object> inputMap = (Map<String, Object>)value;
					
					Class actualExpectedType = expectedType;
					if (expectedType.isInterface()) {
						actualExpectedType = HashMap.class;
					}
					
					Class collParameterType =  yamlAnn.targetType();
					if (collParameterType == YAMLTargetTypeUnspecified.class) {
						throw new YAMLException("Target type must be specified for YAML property: " + key);
					}
					
					Map<Object, Object> outputMap;
					try {
						outputMap = (Map<Object, Object>)actualExpectedType.newInstance();
					} catch (InstantiationException e) {
						throw new YAMLException(e);
					} catch (IllegalAccessException e) {
						throw new YAMLException(e);
					}
					for (Map.Entry<String, Object> entry : inputMap.entrySet()) {
						Object deserialized = deserialize(collParameterType, (Map)entry.getValue());
						outputMap.put(entry.getKey(), deserialized);
					}
					value = outputMap;
				} else if (value instanceof Map) {
					value = deserialize(expectedType, (Map)value);
				}
				if (field != null) {
					try {
						if ((expectedType == float.class || expectedType == Float.class) && value instanceof Double) {
							field.set(obj, ((Double)value).floatValue());
						} else {
							field.set(obj, value);
						}
					} catch (IllegalArgumentException e) {
						throw new YAMLException(e);
					} catch (IllegalAccessException e) {
						throw new YAMLException(e);
					}
				} else if (writeMethod != null) {
					try {
						writeMethod.invoke(obj, value);
					} catch (IllegalAccessException e) {
						throw new YAMLException(e);
					} catch (IllegalArgumentException e) {
						throw new YAMLException(e);
					} catch (InvocationTargetException e) {
						throw new YAMLException(e);
					}
				}
			}
		}
	}
	
	private Class getExpectedType(Field field, Method writeMethod) {
		if (field != null) {
			return field.getType();
		} else if (writeMethod != null) {
			return writeMethod.getReturnType();
		} else {
			return null;
		}
	}
}
