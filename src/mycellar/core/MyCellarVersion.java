package mycellar.core;

import mycellar.Program;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MyCellarVersion {

	public static final String VERSION = "3.4.5.6";
	public static final String MAIN_VERSION = Program.VERSION + " BD";

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
