package test;

import org.apache.commons.net.util.Base64;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class Encode {

  public static void main(String[] args) {
    String buf = args[0];
    byte[] to = Base64.encodeBase64(buf.getBytes());
    try {
      FileOutputStream writer = new FileOutputStream("MyCellar.dat");
      writer.write(to);
      writer.flush();
      writer.close();

      BufferedReader reader = new BufferedReader(new FileReader("MyCellar.dat"));
      String sBuf = reader.readLine();
      System.out.println(new String(Base64.decodeBase64(sBuf.getBytes())));
      reader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
