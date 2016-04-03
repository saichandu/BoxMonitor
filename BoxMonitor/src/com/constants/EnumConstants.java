package com.constants;

public enum EnumConstants {
	SUCCESS("Success"), FAILED("Failed");
	
	private String description;
	
	EnumConstants(String description) {
		this.description = description;
	}
	
	public String toString() {
		return this.description;
	}
}
