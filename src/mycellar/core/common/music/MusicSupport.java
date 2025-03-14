package mycellar.core.common.music;

import mycellar.general.ResourceKey;

import static mycellar.MyCellarUtils.isNullOrEmpty;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;
import static mycellar.general.ResourceKey.EMPTY;
import static mycellar.general.ResourceKey.MUSICSUPPORT_CD;
import static mycellar.general.ResourceKey.MUSICSUPPORT_DIGITAL;
import static mycellar.general.ResourceKey.MUSICSUPPORT_K7;
import static mycellar.general.ResourceKey.MUSICSUPPORT_VINYL;

public enum MusicSupport {
  NONE(EMPTY),
  CD(MUSICSUPPORT_CD),
  VINYL(MUSICSUPPORT_VINYL),
  K7(MUSICSUPPORT_K7),
  DIGITAL(MUSICSUPPORT_DIGITAL);

  private final ResourceKey resourceKey;

  MusicSupport(ResourceKey resourceKey) {
    this.resourceKey = resourceKey;
  }

  public static MusicSupport getSupport(String value) {
    if (isNullOrEmpty(value)) {
      return NONE;
    }
    try {
      return valueOf(value);
    } catch (IllegalArgumentException e) {
      if (value.equals(CD.resourceKey.getKey())) {
        return CD;
      } else if (value.equals(VINYL.resourceKey.getKey())) {
        return VINYL;
      } else if (value.equals(K7.resourceKey.getKey())) {
        return K7;
      } else if (value.equals(DIGITAL.resourceKey.getKey())) {
        return DIGITAL;
      }
    }
    return NONE;
  }

  @Override
  public String toString() {
    if (isNullOrEmpty(resourceKey.getKey())) {
      return "";
    }
    return getLabel(resourceKey);
  }

}
