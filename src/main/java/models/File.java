package models;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author Neeilan
 *
 */
public class File{
	private String path;

	/**
	 * @param path
	 */
	public File(String path) {
		this.path = path;
	}



	protected List<String> readLines() throws IOException{
		ArrayList<String> lines = new ArrayList<>();
		String[] linesArr = readFile().split("\\n");
		for (String s : linesArr) {
			lines.add(s);
		}
		return lines;
	}

	protected void writeLine(String str) throws IOException{
		FileWriter fw = new FileWriter(path, true);
		fw.write(String.format("%s\n",str));
		fw.close();
	}

	protected String readFile() throws IOException{
		Scanner scanner = new Scanner(path );
		return scanner.useDelimiter("\\A").next();

	}


	protected void writeFile(String str) throws IOException{
		FileWriter fw = new FileWriter(path, true);
		fw.write(String.format("%s\n",str));
		fw.close();
	}

	protected void removeLine(String line) throws IOException{
//		String newFile = readLines()
//				.stream()
//				.filter(currLine -> !currLine.equals(line))
//				.reduce("", (acc, curr) -> acc+curr);
//		this.writeFile(newFile);

	}

}