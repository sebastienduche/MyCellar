package mycellar.core;

public interface IPanelModifyable {

  void setModified(boolean modified);

  void setPaneIndex(int index);

  boolean isModified();
}
