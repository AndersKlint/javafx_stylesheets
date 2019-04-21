package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class CSV {
	String path;

	public CSV(String path) {
		this.path = path;
	}

	public ArrayList<String[]> readFromFile() {
		Path pathToFile = Paths.get(path);

		ArrayList<String[]> strings = new ArrayList<String[]>();
		try (BufferedReader br = Files.newBufferedReader(pathToFile, StandardCharsets.UTF_8)) {
			String line = br.readLine();
			while (line != null) {
				String[] attributes = line.split(",");
				strings.add(attributes);
				line = br.readLine();
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return strings;
	}

	public void addToFile(String s) {
		s = s.replace("@", ",");
		try {
			PrintWriter pw = new PrintWriter(new FileOutputStream(new File("./" + path), true));
			pw.append("\n" + s);
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void editInFile(String s) {
		deleteFromFile(Integer.parseInt(s.split("@")[0]));
		addToFile(s);
	}

	public void deleteFromFile(int id) {
		PrintWriter pw;
		try {
			ArrayList<String[]> old = readFromFile();
			pw = new PrintWriter(new FileOutputStream(new File("./" + path), false));
			ArrayList<String[]> newA = new ArrayList<String[]>();
			for (String[] sa : old) {
				if (Integer.parseInt(sa[0]) != id) {
					newA.add(sa);
				}
			}
			for (String[] sa : newA) {
				for (String s : sa) {
					pw.write(s);
					if (!s.equals(sa[sa.length - 1])) {
						pw.write(",");
					}
				}
				if (!sa.equals(newA.get(newA.size() - 1))) {
					pw.write("\n");
				}
			}
			pw.close();
		} catch (

		FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
