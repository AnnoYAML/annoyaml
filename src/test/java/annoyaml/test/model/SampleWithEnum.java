package annoyaml.test.model;

import annoyaml.annotation.YAML;
import annoyaml.annotation.YAMLSerializable;

@YAMLSerializable
public class SampleWithEnum {
	public enum eTestEnum {
		valuea,
		valueb
	}
	
	private eTestEnum testEnum;
	
	@YAML("SampleWithEnum::testEnum")
	public eTestEnum getTestEnum() {
		return testEnum;
	}
	public void setTestEnum(eTestEnum testEnum) {
		this.testEnum = testEnum;
	}
	
}
