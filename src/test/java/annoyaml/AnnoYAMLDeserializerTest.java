package annoyaml;

import static org.junit.Assert.*;

import org.junit.Test;

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
		System.out.println(result);
		AnnoYAMLDeserializer deserializer = new AnnoYAMLDeserializer();
		PrimitivesModel deserialized = deserializer.deserialize(PrimitivesModel.class, result);
		
		assertEquals(m, deserialized);
	}
}
