package annoyaml.test.model;

import annoyaml.annotation.YAML;
import annoyaml.annotation.YAMLFlatten;
import annoyaml.annotation.YAMLSerializable;

@YAMLSerializable
public class SampleFlattenModel {

	@YAML("name")
	private String name;
	@YAML("childmodel")
	@YAMLFlatten
	private SampleFlattenChildModel childModel;
	
	public SampleFlattenChildModel getChildModel() {
		return childModel;
	}
	public void setChildModel(SampleFlattenChildModel childModel) {
		this.childModel = childModel;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "SampleFlattenModel [name=" + name + ", childModel="
				+ childModel + "]";
	}
}
