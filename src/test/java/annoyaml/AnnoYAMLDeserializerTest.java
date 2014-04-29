package annoyaml;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.Test;

import annoyaml.AnnoYAMLSerializerTest.SampleForSerializationModifier;
import annoyaml.AnnoYAMLSerializerTest.SerializationModifier;
import annoyaml.annotation.YAML;
import annoyaml.annotation.YAMLSerializable;
import annoyaml.interceptor.IDeserializationInterceptor;
import annoyaml.interceptor.ISerializationInterceptor;
import annoyaml.test.model.Person;
import annoyaml.test.model.PrimitivesModel;

public class AnnoYAMLDeserializerTest {

	@Test
	public void testBasicDeserialize() {
		Person person = new Person("val1");
		
		AnnoYAMLSerializer serializer = new AnnoYAMLSerializer();
		String result = serializer.serialize(person);
		
		AnnoYAMLDeserializer deserializer = new AnnoYAMLDeserializer();
		Person deserialized = deserializer.deserialize(Person.class, result);
		assertEquals("val1", deserialized.getName());
	}
	
	@Test
	public void testList() throws Exception {
		Person parent = new Person("Parent", new Person("child1"), new Person("child2"));
		
		AnnoYAMLSerializer serializer = new AnnoYAMLSerializer();
		String result = serializer.serialize(parent);
		
		AnnoYAMLDeserializer deserializer = new AnnoYAMLDeserializer();
		Person deserialized = deserializer.deserialize(Person.class, result);
		assertEquals("Parent", deserialized.getName());
		
		assertNotNull(deserialized.getChildren());
		assertEquals("Children lengths should be equal", parent.getChildren().size(), deserialized.getChildren().size());
		
		assertEquals(parent.getChildren(), deserialized.getChildren());
	}
	
	@Test
	public void testMap() throws Exception {
		Person parent = new Person("Parent", new Person("child1"), new Person("child2"));
		
		parent.getRelatives().put("cousin1", new Person("cousin1 lastname"));
		parent.getRelatives().put("cousin2", new Person("cousin2", new Person("cousin2.child1"), new Person("cousin2.child2")));
		
		AnnoYAMLSerializer serializer = new AnnoYAMLSerializer();
		String result = serializer.serialize(parent);
		AnnoYAMLDeserializer deserializer = new AnnoYAMLDeserializer();
		Person deserialized = deserializer.deserialize(Person.class, result);
		assertEquals(parent.getRelatives(), deserialized.getRelatives());
	}

	@Test
	public void testSimpleObjectProperty() throws Exception {
		Person person1 = new Person("person1");
		Person miniPerson1 = new Person("miniPerson1");
		person1.setMiniMe(miniPerson1);
		
		AnnoYAMLSerializer serializer = new AnnoYAMLSerializer();
		String result = serializer.serialize(person1);
		AnnoYAMLDeserializer deserializer = new AnnoYAMLDeserializer();
		Person deserialized = deserializer.deserialize(Person.class, result);
		assertEquals(person1, deserialized);
		assertEquals(person1.getMiniMe(), deserialized.getMiniMe());
	}
	
	@Test
	public void testPrimitivesRoundTrip() throws Exception {
		PrimitivesModel m = new PrimitivesModel();
		m.setDouble1(2.3);
		m.setDoubleObject(2.3);
		m.setFloat1(3.3f);
		m.setFloatObject(3.3f);
		m.setInt1(1);
		m.setIntegerObject(2);
		m.setLong1(Long.MAX_VALUE);
		m.setLongObject(Long.MAX_VALUE);
		m.setString1("123443321");
		
		AnnoYAMLSerializer serializer = new AnnoYAMLSerializer();
		String result = serializer.serialize(m);
		AnnoYAMLDeserializer deserializer = new AnnoYAMLDeserializer();
		PrimitivesModel deserialized = deserializer.deserialize(PrimitivesModel.class, result);
		
		assertEquals(m, deserialized);
	}
	
	public static class DeserializationModifier implements IDeserializationInterceptor {
		public Object deserialize(YAML yamlAnnotation, Object containingObject,
				Method setterMethod, Field field, String yamlFieldName,
				Object yamlFieldValue) {
			if ("MODIFYME".equals(yamlFieldValue)) {
				return "MODIFIED";
			} else {
				return yamlFieldValue;
			}
		}
	}
	
	@YAMLSerializable
	public static class SampleForDeserializationModifier {
		@YAML(value="name", deserializationInterceptors=DeserializationModifier.class)
		private String name;
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}
	
	@Test
	public void testDeserializationModify() {
		AnnoYAMLSerializer serializer = new AnnoYAMLSerializer();
		AnnoYAMLDeserializer deserializer = new AnnoYAMLDeserializer();
		
		SampleForDeserializationModifier sample1 = new SampleForDeserializationModifier();
		sample1.setName("MODIFYME");
		
		String result1 = serializer.serialize(sample1);
		
		SampleForDeserializationModifier sampleResult1 = deserializer.deserialize(SampleForDeserializationModifier.class, result1);
		assertEquals("MODIFIED", sampleResult1.getName());
		
		SampleForDeserializationModifier sample2 = new SampleForDeserializationModifier();
		sample2.setName("SHOULD NOT BE MODIFIED");
		
		AnnoYAMLSerializer serializer2 = new AnnoYAMLSerializer();
		String result2 = serializer2.serialize(sample2);
		
		SampleForDeserializationModifier sampleResult2 = deserializer.deserialize(SampleForDeserializationModifier.class, result2);
		assertEquals("SHOULD NOT BE MODIFIED", sampleResult2.getName());
	}
}
