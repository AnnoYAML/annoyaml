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
	
	@YAML(value="person::children", targetType=Person.class)
	private List<Person> children = new ArrayList<Person>();
	
	@YAML(value="person::relatives", targetType=Person.class)
	private Map<String, Person> relatives = new HashMap<String, Person>();
	
	@YAML(value="person::parent")
	private Person parent;
	
	@YAML(value="person::encryptedField", encrypt=true)
	private String encryptedField;
	
	@YAML(value="person::minime")
	private Person miniMe;
	
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
	public Person getMiniMe() {
		return miniMe;
	}
	public void setMiniMe(Person miniMe) {
		this.miniMe = miniMe;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((children == null) ? 0 : children.hashCode());
		result = prime * result
				+ ((encryptedField == null) ? 0 : encryptedField.hashCode());
		result = prime * result + ((miniMe == null) ? 0 : miniMe.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((relatives == null) ? 0 : relatives.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Person other = (Person) obj;
		if (children == null) {
			if (other.children != null)
				return false;
		} else if (!children.equals(other.children))
			return false;
		if (encryptedField == null) {
			if (other.encryptedField != null)
				return false;
		} else if (!encryptedField.equals(other.encryptedField))
			return false;
		if (miniMe == null) {
			if (other.miniMe != null)
				return false;
		} else if (!miniMe.equals(other.miniMe))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (relatives == null) {
			if (other.relatives != null)
				return false;
		} else if (!relatives.equals(other.relatives))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Person [name=" + name + ", children=" + children
				+ ", relatives=" + relatives + ", encryptedField="
				+ encryptedField + ", miniMe=" + miniMe + "]";
	}	
}
