package annoyaml.test.model;

import annoyaml.annotation.YAML;
import annoyaml.annotation.YAMLSerializable;

@YAMLSerializable
public class BooleanTest {

	@YAML("primitiveBool")
	private boolean primitiveBool;
	@YAML("objectBool")
	private Boolean objectBool;
	
	public boolean isPrimitiveBool() {
		return primitiveBool;
	}
	public Boolean getObjectBool() {
		return objectBool;
	}
	public void setPrimitiveBool(boolean primitiveBool) {
		this.primitiveBool = primitiveBool;
	}
	public void setObjectBool(Boolean objectBool) {
		this.objectBool = objectBool;
	}
}
