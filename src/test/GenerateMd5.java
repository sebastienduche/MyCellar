package test;

import mycellar.Program;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GenerateMd5 {

	public static void main(String[] args) {
		try (FileWriter writer = new FileWriter("./Build/MyCellarVersion.txt")) {
			System.out.println("Building Build/MyCellarVersion.txt");
			String checksum = getMD5Checksum("./Build/MyCellar.jar");
			writer.write(Program.INTERNAL_VERSION + "\n");
			writer.write(Program.VERSION + "\n");
			writer.write("MyCellar.jar@" + checksum + "\n");
			writer.write("Finish.html\n");
			getLibFiles()
					.forEach(s -> {
						try {
							writer.write(s + "\n");
						} catch (IOException e) {
							e.printStackTrace();
						}
					});
			writer.flush();
			System.out.println("Checksum");
			System.out.println(checksum);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static List<String> getLibFiles() {
		try {
			return Files.walk(Path.of("./Build/lib"), 1, FileVisitOption.FOLLOW_LINKS)
					.map(Path::toFile)
					.filter(File::isFile)
					.map(File::getName)
					.collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	private static byte[] createChecksum(String filename) throws Exception {
		try (InputStream fis = new FileInputStream(filename)) {

			byte[] buffer = new byte[1024];
			MessageDigest complete = MessageDigest.getInstance("MD5");
			int numRead;
			do {
				numRead = fis.read(buffer);
				if (numRead > 0) {
					complete.update(buffer, 0, numRead);
				}
			} while (numRead != -1);
			return complete.digest();
		}
	}

	// see this How-to for a faster way to convert
	// a byte array to a HEX string
	private static String getMD5Checksum(String filename) throws Exception {
		byte[] b = createChecksum(filename);
		StringBuilder result = new StringBuilder();
		for (byte value : b) {
			result.append(Integer.toString((value & 0xff) + 0x100, 16).substring(1));
		}
		return result.toString();
	}
}
