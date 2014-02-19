package annoyaml.test.model;

import annoyaml.annotation.YAML;
import annoyaml.annotation.YAMLSerializable;

@YAMLSerializable
public class PrimitivesModel {
	@YAML("int1")
	private int int1;
	@YAML("long1")
	private long long1;
	@YAML("float1")
	private float float1;
	@YAML("double1")
	private double double1;
	@YAML("string1")
	private String string1;
	
	@YAML("integerObject")
	private Integer integerObject;
	@YAML("floatObject")
	private Float floatObject;
	@YAML("longObject")
	private Long longObject;
	@YAML("doubleObject")
	private Double doubleObject;
	
	public int getInt1() {
		return int1;
	}
	public void setInt1(int int1) {
		this.int1 = int1;
	}
	public long getLong1() {
		return long1;
	}
	public void setLong1(long long1) {
		this.long1 = long1;
	}
	public float getFloat1() {
		return float1;
	}
	public void setFloat1(float float1) {
		this.float1 = float1;
	}
	public double getDouble1() {
		return double1;
	}
	public void setDouble1(double double1) {
		this.double1 = double1;
	}
	public String getString1() {
		return string1;
	}
	public void setString1(String string1) {
		this.string1 = string1;
	}
	public Integer getIntegerObject() {
		return integerObject;
	}
	public void setIntegerObject(Integer integerObject) {
		this.integerObject = integerObject;
	}
	public Float getFloatObject() {
		return floatObject;
	}
	public void setFloatObject(Float floatObject) {
		this.floatObject = floatObject;
	}
	public Long getLongObject() {
		return longObject;
	}
	public void setLongObject(Long longObject) {
		this.longObject = longObject;
	}
	public Double getDoubleObject() {
		return doubleObject;
	}
	public void setDoubleObject(Double doubleObject) {
		this.doubleObject = doubleObject;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(double1);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((doubleObject == null) ? 0 : doubleObject.hashCode());
		result = prime * result + Float.floatToIntBits(float1);
		result = prime * result
				+ ((floatObject == null) ? 0 : floatObject.hashCode());
		result = prime * result + int1;
		result = prime * result
				+ ((integerObject == null) ? 0 : integerObject.hashCode());
		result = prime * result + (int) (long1 ^ (long1 >>> 32));
		result = prime * result
				+ ((longObject == null) ? 0 : longObject.hashCode());
		result = prime * result + ((string1 == null) ? 0 : string1.hashCode());
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
		PrimitivesModel other = (PrimitivesModel) obj;
		if (Double.doubleToLongBits(double1) != Double
				.doubleToLongBits(other.double1))
			return false;
		if (doubleObject == null) {
			if (other.doubleObject != null)
				return false;
		} else if (!doubleObject.equals(other.doubleObject))
			return false;
		if (Float.floatToIntBits(float1) != Float.floatToIntBits(other.float1))
			return false;
		if (floatObject == null) {
			if (other.floatObject != null)
				return false;
		} else if (!floatObject.equals(other.floatObject))
			return false;
		if (int1 != other.int1)
			return false;
		if (integerObject == null) {
			if (other.integerObject != null)
				return false;
		} else if (!integerObject.equals(other.integerObject))
			return false;
		if (long1 != other.long1)
			return false;
		if (longObject == null) {
			if (other.longObject != null)
				return false;
		} else if (!longObject.equals(other.longObject))
			return false;
		if (string1 == null) {
			if (other.string1 != null)
				return false;
		} else if (!string1.equals(other.string1))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "PrimitivesModel [int1=" + int1 + ", long1=" + long1
				+ ", float1=" + float1 + ", double1=" + double1 + ", string1="
				+ string1 + ", integerObject=" + integerObject
				+ ", floatObject=" + floatObject + ", longObject=" + longObject
				+ ", doubleObject=" + doubleObject + "]";
	}
}
