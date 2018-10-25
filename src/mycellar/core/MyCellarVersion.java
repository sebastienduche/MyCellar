package mycellar.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MyCellarVersion {

	public static final String VERSION = "3.1.4.2";
	public static final String MAIN_VERSION = "5.6 DB";
  
	public static String getLocalVersion() {
		// In directory bin
		File versionFile = new File("MyCellarVersion.txt");
		if (versionFile.exists()) {
    		try(BufferedReader reader = new BufferedReader(new FileReader(versionFile)))
    		{
    			return reader.readLine();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
		} else {
			setLocalVersion(VERSION);
			return VERSION;
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
