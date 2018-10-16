package mycellar.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MyCellarVersion {

	public static final String VERSION = "3.1.1.6";
	public static final String MAIN_VERSION = "5.6 ZE";
  
	public static String getLocalVersion() {
		// In directory bin
		try(InputStream stream = MyCellarVersion.class.getClassLoader().getResourceAsStream("MyCellarVersion.txt");
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream)))
		{
			return reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static void setLocalVersion(String version) {
		File f = new File("MyCellarVersion.txt");
		try (FileWriter writer = new FileWriter(f)){
			writer.write(version);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
