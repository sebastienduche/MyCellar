package mycellar.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MyCellarVersion {

	public static final String version = "2.5.0.9";
	public static final String mainVersion = "4.8 CO";
	  
	public static String getLocalVersion() {
		InputStream stream = MyCellarVersion.class.getClassLoader().getResourceAsStream("MyCellarVersion.txt");
		if(stream == null)
			return "";
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		String version;
		try {
			version = reader.readLine();
			reader.close();
			return version;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	public static void setLocalVersion(String version) {
		File f = new File("MyCellarVersion.txt");
		try {
			FileWriter writer = new FileWriter(f);
			writer.write(version);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}