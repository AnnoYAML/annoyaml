package annoyaml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import annoyaml.annotation.YAML;
import annoyaml.annotation.YAMLSerializable;
import annoyaml.interceptor.ISerializationInterceptor;
import annoyaml.test.model.BooleanTest;
import annoyaml.test.model.Person;
import annoyaml.test.model.SampleFlattenChildModel;
import annoyaml.test.model.SampleFlattenModel;
import annoyaml.test.model.SampleWithEnum;
import annoyaml.test.model.SampleWithEnum.eTestEnum;

public class AnnoYAMLSerializerTest {

	@Test
	public void testBasic() throws Exception {
		Person parent = new Person("Parent", new Person("child1"), new Person("child2"));
		
		parent.getRelatives().put("cousin1", new Person("cousin1 lastname"));
		parent.getRelatives().put("cousin2", new Person("cousin2", new Person("cousin2.child1"), new Person("cousin2.child2")));
		
		AnnoYAMLSerializer serializer = new AnnoYAMLSerializer();
		String result = serializer.serialize(parent);
		
		Yaml yaml = new Yaml();
		Map<Object, Object> loadedYAML = (Map<Object, Object>)yaml.load(result);
		assertEquals("Parent", loadedYAML.get("person::name"));
		List<Map<Object, Object>> children = (List<Map<Object, Object>>)loadedYAML.get("person::children");
		Map<Object, Object> child1Map = children.get(0);
		assertEquals("child1", child1Map.get("person::name"));
		Map<Object, Object> child2Map = children.get(1);
		assertEquals("child2", child2Map.get("person::name"));
		
		Map<String, Object> relativesMap = (Map<String, Object>)loadedYAML.get("person::relatives");
		Map<Object, Object> cousin1 = (Map<Object, Object>)relativesMap.get("cousin1");
		assertEquals("cousin1 lastname", cousin1.get("person::name"));
		assertNotNull(cousin1.get("person::children"));
		assertTrue(cousin1.get("person::children") instanceof List);
		List<Object> cousin1Children = (List<Object>)cousin1.get("person::children");
		assertTrue(cousin1Children.isEmpty());
		
		Map<Object, Object> cousin2 = (Map<Object, Object>)relativesMap.get("cousin2");
		assertEquals("cousin2", cousin2.get("person::name"));
		List<Map<Object, Object>> cousin2Children = (List<Map<Object, Object>>)cousin2.get("person::children");
		assertEquals(2, cousin2Children.size());
		Map<Object, Object> cousin2Child1 = cousin2Children.get(0);
		assertEquals("cousin2.child1", cousin2Child1.get("person::name"));
		Map<Object, Object> cousin2Child2 = cousin2Children.get(1);
		assertEquals("cousin2.child2", cousin2Child2.get("person::name"));
	}
	
	@Test
	public void testBoolean() throws Exception {
		BooleanTest t = new BooleanTest();
		t.setObjectBool(true);
		t.setPrimitiveBool(true);
		
		AnnoYAMLSerializer serializer = new AnnoYAMLSerializer();
		String result = serializer.serialize(t);
		assertTrue(result.contains("primitiveBool: true"));
		assertTrue(result.contains("objectBool: true"));
		
		t.setObjectBool(false);
		t.setPrimitiveBool(false);
		
		result = serializer.serialize(t);
		assertTrue(result.contains("primitiveBool: false"));
		assertTrue(result.contains("objectBool: false"));
	}
	
	@Test
	public void testEnum() throws Exception {
		SampleWithEnum m = new SampleWithEnum();
		m.setTestEnum(eTestEnum.valuea);

		AnnoYAMLSerializer serializer = new AnnoYAMLSerializer();
		String result = serializer.serialize(m);
		
		assertEquals("{'SampleWithEnum::testEnum': valuea}", result.trim());
	}
	
	@Test
	public void testCyclic() throws Exception {
		Person parent = new Person("Parent");
		Person child = new Person("Child");
		parent.getChildren().add(child);
		child.setParent(parent);
		
		AnnoYAMLSerializer serializer = new AnnoYAMLSerializer();
		String result = serializer.serialize(parent);
		
		Yaml yaml = new Yaml();
		Map<Object, Object> loadedYAML = (Map<Object, Object>)yaml.load(result);
		assertEquals("Parent", loadedYAML.get("person::name"));
		
		List<Map<Object, Object>> children = (List<Map<Object, Object>>)loadedYAML.get("person::children");
		Map<Object, Object> child1Map = children.get(0);
		assertEquals("Child", child1Map.get("person::name"));
		
	}

	@Test
	public void testEncryptedFieldNoEncryptorSet() throws Exception {
		Person person = new Person("val1");
		person.setEncryptedField("myencryptedfield");
		
		AnnoYAMLSerializer serializer = new AnnoYAMLSerializer();
		String result = serializer.serialize(person);
		
		Yaml yaml = new Yaml();
		Map<Object, Object> loadedYAML = (Map<Object, Object>)yaml.load(result);
		assertEquals("myencryptedfield", loadedYAML.get("person::encryptedField"));
	}

	@Test
	public void testEncryptedFieldFAKEEncryptorSet() throws Exception {
		Person person = new Person("val1");
		person.setEncryptedField("myencryptedfield");
		
		IEncryptor encryptor = new IEncryptor() {
			public String encrypt(String encrypt) {
				return "FAKEENCRYPTED";
			}
		};
		
		AnnoYAMLSerializer serializer = new AnnoYAMLSerializer();
		serializer.getYamlConfiguration().setEncryptor(encryptor);
		String result = serializer.serialize(person);
		
		Yaml yaml = new Yaml();
		Map<Object, Object> loadedYAML = (Map<Object, Object>)yaml.load(result);
		assertEquals("FAKEENCRYPTED", loadedYAML.get("person::encryptedField"));
	}
	
	@Test
	public void testFlattenedModel() throws Exception {
		SampleFlattenModel m = new SampleFlattenModel();
		m.setName("namevalue");
		SampleFlattenChildModel childModel = new SampleFlattenChildModel();
		childModel.setValue1("1");
		childModel.setValue2("2");
		childModel.setValue3(3);
		m.setChildModel(childModel);
		
		AnnoYAMLSerializer serializer = new AnnoYAMLSerializer();
		String result = serializer.serialize(m);
		
		Yaml yaml = new Yaml();
		Map<Object, Object> loadedYAML = (Map<Object, Object>)yaml.load(result);
		assertEquals("namevalue", loadedYAML.get("name"));
		assertEquals("1", loadedYAML.get("flattenedchild::value1"));
		assertEquals("2", loadedYAML.get("flattenedchild::value2"));
		assertEquals(3.0, loadedYAML.get("flattenedchild::value3"));
	}
	
	@Test
	public void testBasicMapSerialization() throws Exception {
		Person person = new Person();
		person.getBasicMap().put("a", 1.0);
		person.getBasicMap().put("b", 2.0);
		
		AnnoYAMLSerializer serializer = new AnnoYAMLSerializer();
		String result = serializer.serialize(person);
		assertTrue(result.contains("b: 2.0"));
		assertTrue(result.contains("a: 1.0"));
	}
	
	public static class SerializationModifier implements ISerializationInterceptor {
		public Object serialize(YAML yamlAnnotation, Object containingObject, java.lang.reflect.Method getterMethod, java.lang.reflect.Field field, String yamlFieldName, Object yamlFieldValue) {
			if ("REMOVEME".equals(yamlFieldValue)) {
				return null;
			} else {
				return "MODIFIED";
			}
		}
	}
	
	@YAMLSerializable
	public static class SampleForSerializationModifier {
		@YAML(value="name", serializationInterceptors=SerializationModifier.class)
		private String name;
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}
	
	@Test
	public void testSerializationInterceptorModify() throws Exception {
		
		SampleForSerializationModifier sample = new SampleForSerializationModifier();
		sample.setName("SampleName");
		
		AnnoYAMLSerializer serializer = new AnnoYAMLSerializer();
		String result = serializer.serialize(sample);
		
		AnnoYAMLDeserializer deserializer = new AnnoYAMLDeserializer();
		SampleForSerializationModifier sampleResult = deserializer.deserialize(SampleForSerializationModifier.class, result);
		assertEquals("MODIFIED", sampleResult.getName());
	}
	
	@Test
	public void testSerializationSkipProperty() throws Exception {
		SampleForSerializationModifier sample = new SampleForSerializationModifier();
		sample.setName("REMOVEME");
		
		AnnoYAMLSerializer serializer = new AnnoYAMLSerializer();
		String result = serializer.serialize(sample);
		
		AnnoYAMLDeserializer deserializer = new AnnoYAMLDeserializer();
		SampleForSerializationModifier sampleResult = deserializer.deserialize(SampleForSerializationModifier.class, result);
		assertNull(sampleResult.getName());
	}
}
