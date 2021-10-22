package mycellar.core.datas.history;

import java.util.Arrays;

public enum HistoryState {
  ADD,
  MODIFY,
  DEL,
  VALIDATED,
  TOCHECK,
  ALL;

  public static HistoryState findState(int value) {
    return Arrays.stream(values()).filter(historyState -> historyState.ordinal() == value).findFirst().orElse(null);
  }
}
