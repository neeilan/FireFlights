package controllers;

import java.util.ArrayList;
import java.util.List;

import exceptions.UserNotFoundException;
import models.DataStore;
import models.User;
import models.UserFile;

public class UserController {
	private DataStore<User> userData;

	
	public UserController() {
		super();
	}
	
	public void setDataSource(UserFile userData) {
		this.userData = userData;
	}

	public String findByEmail(String email) throws UserNotFoundException {
		// This was going to originally go in userfile but since phase 2 requirements
		// are so closely tied to csv I bumped it up a level to controller (also no need to cast userData)
		List<User> users = userData.findAll();
		List<User> emailMatches = new ArrayList<>();

		for (User user : users){
			if (user.getEmail().equals(email)){
				emailMatches.add(user);
			}
		}

		if (emailMatches.size() > 0)
			return emailMatches.get(0).toCsv();
		else{
			throw new UserNotFoundException();
		}
	}
}
