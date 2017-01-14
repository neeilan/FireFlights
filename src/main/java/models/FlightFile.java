package models;

import java.io.IOException;
import java.util.List;

/**
 * @author Neeilan
 *
 */
public class FlightFile extends File implements DataStore<Flight> {

	public FlightFile(String path) {
		super(path);
	}

	public void add(Flight item) {
		try {
			super.writeLine(item.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<Flight> findAll() {
		List<String> lines;
//		try {
//			lines = super.readLines();
//			return lines.stream()
//					.map(this::createFlightFromCsv)
//					.collect(Collectors.toList());
//		} catch (IOException e) {
//			return new ArrayList<Flight>();
//		}
		return null;
	}
	
	private Flight createFlightFromCsv(String csvStr){
		String[] attributes = csvStr.split(";");
		
		return new Flight(attributes[0], attributes[1], attributes[2], attributes[3], attributes[4], attributes[5],
				Double.parseDouble(attributes[6]));
	}

	@Override
	public void remove(Flight item) throws IOException {
//		List<Flight> newFileContents
//		= this.findAll()
//			.stream()
//			.filter(other -> !other.equals(item.toCsv()))
//			.collect(Collectors.toList());
//		super.writeFile("");
//		newFileContents.forEach(this::add);
	}

}
