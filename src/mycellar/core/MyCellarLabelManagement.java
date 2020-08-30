package mycellar.core;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import mycellar.Program;

public class MyCellarLabelManagement {

  private static final List<IMyCellarComponent> LABEL_LIST = new ArrayList<>();
  
  static void add(IMyCellarComponent component) {
    LABEL_LIST.add(component);
  }
  
  static void updateText(IMyCellarComponent component, LabelType type, String code, String value) {
    if (value == null) {
      switch (type) {
        case INFO:
          component.setText(Program.getLabel("Infos" + code));
          break;
        case ERROR:
          component.setText(Program.getError("Errors" + code));
          break;
        case INFO_OTHER:
          component.setText(Program.getLabel(code));
          break;
        case ERROR_OTHER:
          component.setText(Program.getError(code));
          break;
      }
    } else {
      switch (type) {
        case INFO:
          component.setText(MessageFormat.format(Program.getLabel("Infos" + code), value).strip());
          break;
        case ERROR:
          component.setText(MessageFormat.format(Program.getError("Errors" + code), value).strip());
          break;
        case INFO_OTHER:
          component.setText(MessageFormat.format(Program.getLabel(code), value).strip());
          break;
        case ERROR_OTHER:
          component.setText(MessageFormat.format(Program.getError(code), value).strip());
          break;
      }
    }
  }
  
  public static void updateLabels() {
    LABEL_LIST.forEach(IMyCellarComponent::updateText);
  }
}
