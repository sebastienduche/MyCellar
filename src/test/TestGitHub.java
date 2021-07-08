package test;

import mycellar.launcher.MyCellarServer;

import java.io.File;
import java.io.IOException;

public class TestGitHub {

	public static void main(String[] args) {
		downloadFromGitHub();
	}

	private static void downloadFromGitHub() {
		File f = new File("Test.jar");
		System.out.println(f.getAbsolutePath());
		try {
			MyCellarServer.getInstance().downloadFileFromGitHub("MyCellar.jar", f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void testUpdateFromGitHub() {
//		Server.getInstance().testPopulateList();
		MyCellarServer.getInstance().downloadVersion();
	}

}
