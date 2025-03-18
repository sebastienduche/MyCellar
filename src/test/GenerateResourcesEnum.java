package test;

import mycellar.core.text.LanguageFileLoader;

import java.io.FileWriter;
import java.io.IOException;

public class GenerateResourcesEnum {

  public static void main(String[] args) {
    try (FileWriter writer = new FileWriter("./src/mycellar/general/ResourceKey.java")) {
      writer.write("package mycellar.general;\n\n");
      writer.write("public enum ResourceKey implements IResource {\n");
      LanguageFileLoader.getInstance().getBundleTitle().keySet().forEach(
          s -> {
            try {
              writer.write(s.toUpperCase().replace('.', '_') + "(\"" + s + "\"),\n");
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
          }
      );
      LanguageFileLoader.getInstance().getBundleWine().keySet().forEach(
          s -> {
            try {
              writer.write(s.toUpperCase().replace('.', '_') + "(\"" + s + "\"),\n");
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
          }
      );
      writer.write("EMPTY(\"\")\n");
      writer.write(";\n");
      writer.write("private final String key;\n\n");
      writer.write("ResourceKey(String key) {\n");
      writer.write("this.key = key;\n");
      writer.write("}\n\n");
      writer.write("public String getKey() {\n");
      writer.write("return key;\n");
      writer.write("}\n");
      writer.write("\n}");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    try (FileWriter writer = new FileWriter("./src/mycellar/general/ResourceErrorKey.java")) {
      writer.write("package mycellar.general;\n\n");
      writer.write("public enum ResourceErrorKey implements IResource {\n");
      LanguageFileLoader.getInstance().getBundleError().keySet().forEach(
          s -> {
            try {
              writer.write(s.toUpperCase().replace('.', '_') + "(\"" + s + "\"),\n");
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
          }
      );
      LanguageFileLoader.getInstance().getBundleWineError().keySet().forEach(
          s -> {
            try {
              writer.write(s.toUpperCase().replace('.', '_') + "(\"" + s + "\"),\n");
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
          }
      );
      writer.write("EMPTY(\"\")\n");
      writer.write(";\n");
      writer.write("private final String key;\n\n");
      writer.write("ResourceErrorKey(String key) {\n");
      writer.write("this.key = key;\n");
      writer.write("}\n\n");
      writer.write("public String getKey() {\n");
      writer.write("return key;\n");
      writer.write("}\n");
      writer.write("\n}");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
