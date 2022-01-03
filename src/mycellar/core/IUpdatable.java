package mycellar.core;

public interface IUpdatable {
  void setUpdateView(UpdateViewType updateViewType);

  void updateView();
}
