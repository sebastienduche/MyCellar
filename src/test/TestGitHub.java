package test;

import mycellar.launcher.Server;

import java.io.File;
import java.io.IOException;

public class TestGitHub {

	public static void main(String[] args) {
		
		File f = new File("Test.jar");
		System.out.println(f.getAbsolutePath());
		try {
			Server.getInstance().downloadFileFromGitHub("MyCellar.jar", f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
