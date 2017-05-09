package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.net.util.Base64;

public class Encode {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String buf = "sebastienduche@gmail.com/mycellarinfo@gmail.com/mycellar2014";
		byte[] to = Base64.encodeBase64(buf.getBytes());
		try {
			FileOutputStream writer = new FileOutputStream(new File("MyCellar.dat"));
			writer.write(to);
			writer.flush();
			writer.close();
			
			BufferedReader reader = new BufferedReader(new FileReader(new File("MyCellar.dat")));
			String sBuf = reader.readLine();
			System.out.println(new String(Base64.decodeBase64(sBuf.getBytes())));
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
