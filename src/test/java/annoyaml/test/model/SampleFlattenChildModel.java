package annoyaml.test.model;

import annoyaml.annotation.YAML;
import annoyaml.annotation.YAMLSerializable;

@YAMLSerializable
public class SampleFlattenChildModel {

	@YAML("flattenedchild::value1")
	private String value1;
	@YAML("flattenedchild::value2")
	private String value2;
	@YAML("flattenedchild::value3")
	private double value3;
	public String getValue1() {
		return value1;
	}
	public void setValue1(String value1) {
		this.value1 = value1;
	}
	public String getValue2() {
		return value2;
	}
	public void setValue2(String value2) {
		this.value2 = value2;
	}
	public double getValue3() {
		return value3;
	}
	public void setValue3(double value3) {
		this.value3 = value3;
	}
	@Override
	public String toString() {
		return "SampleFlattenChildModel [value1=" + value1 + ", value2="
				+ value2 + ", value3=" + value3 + "]";
	}
}
