package annoyaml;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import annoyaml.test.model.Person;
import annoyaml.test.model.SampleFlattenChildModel;
import annoyaml.test.model.SampleFlattenModel;

public class AnnoYAMLSerializerTest {

	@Test
	public void testBasic() throws Exception {
		Person parent = new Person("Parent", new Person("child1"), new Person("child2"));
		
		parent.getRelatives().put("cousin1", new Person("cousin1 lastname"));
		parent.getRelatives().put("cousin2", new Person("cousin2", new Person("cousin2.child1"), new Person("cousin2.child2")));
		
		AnnoYAMLSerializer serializer = new AnnoYAMLSerializer();
		String result = serializer.serialize(parent);
		assertEquals(readClasspathFile("testBasicExpected.txt"), result);
	}
	
	@Test
	public void testCyclic() throws Exception {
		Person parent = new Person("Parent");
		Person child = new Person("Child");
		parent.getChildren().add(child);
		child.setParent(parent);
		
		AnnoYAMLSerializer serializer = new AnnoYAMLSerializer();
		String result = serializer.serialize(parent);
		assertEquals(readClasspathFile("testCyclic.txt"), result);
	}

	@Test
	public void testEncryptedFieldNoEncryptorSet() throws Exception {
		Person person = new Person("val1");
		person.setEncryptedField("myencryptedfield");
		
		AnnoYAMLSerializer serializer = new AnnoYAMLSerializer();
		String result = serializer.serialize(person);
		assertEquals(readClasspathFile("testEncryptedNoEncryptorSetExpected.txt"), result);
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
		assertEquals(readClasspathFile("testEncryptedFakeEncryptorSetExpected.txt"), result);
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
		assertEquals(readClasspathFile("flattenTestExpected.txt"), result);
	}
	
	private String readClasspathFile(String file) throws IOException {
		InputStream is = getClass().getResourceAsStream(file);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buffer = new byte[32768];
		int bytesRead;
		while ( (bytesRead = is.read(buffer)) != -1) {
			bos.write(buffer, 0, bytesRead);
		}
		return new String(bos.toByteArray());
	}
	
	
}
