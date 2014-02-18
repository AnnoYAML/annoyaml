annoyaml
========

Annotation based YAML library

Define a YAML serialization of Java POJOs via simple Java Annotations. Supported types include:
 
 - Java Primitives, and Strings
 - Java Collections and Maps
 - Lists and Arrays

This is implemented using [SnakeYAML](https://code.google.com/p/snakeyaml/)

Example Java code:



```
@YAMLSerializable
public class Person {

	@YAML(value="person::name")
	private String name;
	
	@YAML(value="person::children")
	private List<Person> children = new ArrayList<Person>();
	
	@YAML(value="person::relatives")
	private Map<String, Person> relatives = new HashMap<String, Person>();
	
	@YAML(value="person::parent")
	private Person parent;
	
	@YAML(value="person::encryptedField", encrypt=true)
	private String encryptedField;
	
	public Person() {
	}
	
	public Person(String name) {
		super();
		this.name = name;
	}

	public Person(String name, Person... children) {
		this.name = name;
		this.children.addAll(Arrays.asList(children));
	}
	public Person getParent() {
		return parent;
	}
	public void setParent(Person parent) {
		this.parent = parent;
	}
	public Map<String, Person> getRelatives() {
		return relatives;
	}
	public List<Person> getChildren() {
		return children;
	}
	public void setChildren(List<Person> children) {
		this.children = children;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEncryptedField() {
		return encryptedField;
	}
	public void setEncryptedField(String encryptedField) {
		this.encryptedField = encryptedField;
	}
}
```

To serialize this to a String, simply run this:

```
Person parent = new Person("Parent", new Person("child1"), new Person("child2"));
AnnoYAMLSerializer serializer = new AnnoYAMLSerializer();
String result = serializer.serialize(parent);
```
