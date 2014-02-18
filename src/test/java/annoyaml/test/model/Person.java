package annoyaml.test.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import annoyaml.annotation.YAML;
import annoyaml.annotation.YAMLSerializable;

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
