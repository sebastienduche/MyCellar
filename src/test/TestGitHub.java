package test;

import mycellar.launcher.Server;
import org.kohsuke.github.GHGistBuilder;
import org.kohsuke.github.GitHub;

import java.io.File;
import java.io.IOException;

public class TestGitHub {

	public static void main(String[] args) {

		try {
			testCreateGist();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void downloadFromGitHub() {
		File f = new File("Test.jar");
		System.out.println(f.getAbsolutePath());
		try {
			Server.getInstance().downloadFileFromGitHub("MyCellar.jar", f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void testCreateGist() throws IOException {
		final GitHub gitHub = GitHub.connect("sebastienduche", "b879ec5a49a9bfbb20a89735270b1b96c94aa19d");
		final GHGistBuilder gist = gitHub.createGist();
		gist.description("Error")
				.file("Error.log", "HELLO World!!")
				.create();
	}
}
