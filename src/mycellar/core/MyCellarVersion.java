package mycellar.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MyCellarVersion {

	public static final String VERSION = "3.2.7.7";
	public static final String NUMERIC_VERSION = "5.8";
	public static final String MAIN_VERSION = NUMERIC_VERSION+" YC";

	public static String getLocalVersion() {
		// In directory bin
		File versionFile = new File("MyCellarVersion.txt");
		if (versionFile.exists()) {
    		try(var bufferReader = new BufferedReader(new FileReader(versionFile)))
    		{
    			return bufferReader.readLine();
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
		try (var writer = new FileWriter(f)){
			writer.write(version);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
