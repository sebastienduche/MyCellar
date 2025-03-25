package test;

import mycellar.core.text.LanguageFileLoader;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

public class GenerateResourcesEnum {

  public static void main(String[] args) {
    try (FileWriter writer = new FileWriter("./src/mycellar/general/ResourceKey.java")) {
      writer.write("package mycellar.general;\n\n");
      writer.write("public enum ResourceKey implements IResource {\n");
      Map<String, String> mapKeyValue = mapResources(LanguageFileLoader.getInstance().getBundleTitle());
      writeResourceKeys(mapKeyValue, writer);

      Map<String, String> mapKeyValueWine = mapResources(LanguageFileLoader.getInstance().getBundleWine());
      writeResourceKeys(mapKeyValueWine, writer);

      Map<String, String> mapKeyValueKeyboard = mapResources(LanguageFileLoader.getInstance().getBundleKeyboard());
      writeResourceKeys(mapKeyValueKeyboard, writer);
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
      Map<String, String> mapKeyValueError = mapResources(LanguageFileLoader.getInstance().getBundleError());

      writeResourceKeys(mapKeyValueError, writer);

      Map<String, String> mapKeyValueErrorWine = mapResources(LanguageFileLoader.getInstance().getBundleWineError());

      writeResourceKeys(mapKeyValueErrorWine, writer);
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

  private static Map<String, String> mapResources(ResourceBundle resourceBundle) {
    return resourceBundle.keySet().stream().collect(toMap(s -> s.toUpperCase().replace('.', '_'), Function.identity()));
  }

  private static void writeResourceKeys(Map<String, String> mapKeyValue, FileWriter writer) {
    LinkedHashMap<String, String> sortedMapError = mapKeyValue.entrySet()
        .stream()
        .sorted(Map.Entry.comparingByKey())
        .collect(toMap(Map.Entry::getKey, Map.Entry::getValue,
            (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    sortedMapError.forEach(
        (key, value) -> {
          try {
            writer.write(key + "(\"" + value + "\"),\n");
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
    );
  }
}
