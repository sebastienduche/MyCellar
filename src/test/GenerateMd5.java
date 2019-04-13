package test;

import mycellar.core.MyCellarVersion;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.security.MessageDigest;

public class GenerateMd5 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			System.out.println("Building Build/MyCellarVersion.txt");
			String checksum = getMD5Checksum("./Build/MyCellar.jar");
			FileWriter writer = new FileWriter(new File("./Build/MyCellarVersion.txt"));
			writer.write(MyCellarVersion.VERSION+"\n");
			writer.write(MyCellarVersion.NUMERIC_VERSION+"\n");
			writer.write("MyCellar.jar@"+checksum+"\n");
			writer.write("Finish.html");
			writer.flush();
			writer.close();
			System.out.println("Checksum");
			System.out.println(checksum);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public static byte[] createChecksum(String filename) throws Exception {
		InputStream fis = new FileInputStream(filename);

		byte[] buffer = new byte[1024];
		MessageDigest complete = MessageDigest.getInstance("MD5");
		int numRead;
		do {
			numRead = fis.read(buffer);
			if (numRead > 0) {
				complete.update(buffer, 0, numRead);
			}
		} while (numRead != -1);
		fis.close();
		return complete.digest();
	}

	// see this How-to for a faster way to convert
	// a byte array to a HEX string
	private static String getMD5Checksum(String filename) throws Exception {
		byte[] b = createChecksum(filename);
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < b.length; i++) {
			result.append(Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1));
		}
		return result.toString();
	}
}
