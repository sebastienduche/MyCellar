package mycellar.core.datas.history;

import java.util.Arrays;

public enum HistoryState {
  ADD(0),
  MODIFY(1),
  DEL(2),
  VALIDATED(3),
  TOCHECK(4),
  ALL(5);

  private final int index;

  HistoryState(int index) {
    this.index = index;
  }

  public int getIndex() {
    return index;
  }

  public static HistoryState findState(int value) {
    return Arrays.stream(values()).filter(historyState -> historyState.getIndex() == value).findFirst().orElse(null);
  }
}
