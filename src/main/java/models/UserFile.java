package models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserFile extends File implements DataStore<User> {

	public UserFile(String path) {
		super(path);
	}

	@Override
	public void add(User item) throws IOException {
		super.writeLine(item.toCsv());
	}
	

	@Override
	public List<User> findAll() {
//		try {
//			return super.readLines()
//					.stream()
//					.map(this::createUserFromCsv)
//					.collect(Collectors.toList());
//		} catch (IOException e) {
//			e.printStackTrace();
//			return new ArrayList<User>();
//		}
		return null;
	}
	
	private User createUserFromCsv(String csvStr){
		String[] attributes = csvStr.split(";");
		return new User(attributes[0], attributes[1], attributes[2], attributes[3], attributes[4], attributes[5]);
	}

	@Override
	public void remove(User user) throws IOException {
		super.removeLine(user.toCsv());
	}

}
