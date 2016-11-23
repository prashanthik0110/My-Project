package com.styfox.contactApp;

public class ContactBean {
	private String name;
	private String phoneNo;
	private String Email;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhoneNo() {
		return phoneNo;
	}
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
	
	public String getEmail() {
		if(Email==null){
			Email="";
		}
		return Email;
	}
	public void setEmail(String Email) {
		this.Email = Email;
	}
	
	
}
