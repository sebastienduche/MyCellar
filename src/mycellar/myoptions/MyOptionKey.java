package mycellar.myoptions;

import mycellar.core.text.LabelKey;
import mycellar.general.IResource;

public record MyOptionKey(IResource resource, String defaultValue, String propertyKey, MyOptionObjectType objectType,
                          LabelKey labelKey) {
  public MyOptionKey(IResource resource, String defaultValue, String propertyKey, MyOptionObjectType objectType) {
    this(resource, defaultValue, propertyKey, objectType, null);
  }

  public MyOptionKey(LabelKey labelKey, MyOptionObjectType objectType, String propertyKey, String defaultValue) {
    this(null, defaultValue, propertyKey, objectType, labelKey);
  }
}
