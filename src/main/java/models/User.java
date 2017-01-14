package models;

import java.io.Serializable;

public class User implements Serializable {

	private String firstNames;
	private String lastName;
	private String dob; //format: yyyy-MM-dd
	private String address;
	private String ccName;
	private String creditCardNumber;
	private String expiryDate; //format: yyyy-MM-dd
	private String email;
	private String password;
	
	/** 
	 * Create a new user given last name, first name, email, address, credit card number 
	 * and it's expiry date
	 * @param lastName of the user
	 * @param firstNames of the user
	 * @param email of the user
	 * @param address of the user
	 * @param creditCardNumber of the user
	 * @param expiryDate of the user
	 */
	public User(String lastName, String firstNames, String email, String address,
	          String creditCardNumber,String expiryDate) { 
	    this.lastName = lastName;
	    this.firstNames = firstNames;
	    this.email = email;
	    this.address = address;
	    this.creditCardNumber = creditCardNumber;
	    this.expiryDate = expiryDate;
	    
	}

	/**
	 * @return the first name of the user
	 */
	public String getFirstNames() {
		return firstNames;
	}

	public void setFirstNames(String firstNames) {
		this.firstNames = firstNames;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getDob() {
		return dob;
	}

	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCcName() {
		return ccName;
	}

	public void setCcName(String ccName) {
		this.ccName = ccName;
	}

	public String getCreditCardNumber() {
		return creditCardNumber;
	}

	public void setCreditCardNumber(String creditCardNumber) {
		this.creditCardNumber = creditCardNumber;
	}
	
	

	public String getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}
	
	public String toCsv(){
		return String.format("%s;%s;%s;%s;%s;%s", 
		        lastName, firstNames, email, address, creditCardNumber, expiryDate);	
	}

}