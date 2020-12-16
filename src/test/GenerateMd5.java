package test;

import mycellar.Program;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.List;

public class GenerateMd5 {

	private static final List<String> FILE_TO_ADD = List.of("commons-io-2.4.jar",
			"commons-lang3-3.9.jar",
			"commons-text-1.9.jar",
			"github-api-1.117.jar",
			"jackson-annotations-2.10.2.jar",
			"jackson-core-2.10.2.jar",
			"jackson-databind-2.10.2.jar");

	private static final List<String> FILE_TO_DELETE = List.of("commons-io-2.1.jar",
			"commons-lang-2.1.jar",
			"javax.mail.jar",
			"mailapi.jar",
			"mailapi-1.4.4.jar",
			"smtp.jar");

	public static void main(String[] args) {
		try (FileWriter writer = new FileWriter("./Build/MyCellarVersion.txt")) {
			System.out.println("Building Build/MyCellarVersion.txt");
			String checksum = getMD5Checksum("./Build/MyCellar.jar");
			writer.write(Program.INTERNAL_VERSION+"\n");
			writer.write(Program.VERSION+"\n");
			writer.write("MyCellar.jar@"+checksum+"\n");
			writer.write("Finish.html\n");
			for (String s : FILE_TO_ADD) {
				writer.write(s + "\n");
			}
			for (String s : FILE_TO_DELETE) {
				writer.write("-" + s + "\n");
			}
			writer.flush();
			System.out.println("Checksum");
			System.out.println(checksum);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
