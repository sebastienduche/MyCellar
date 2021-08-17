package test;

import mycellar.core.common.music.DurationConverter;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DurationConverterTest {

  @Test
  void getFormattedDisplay() {
    assertEquals("5:33", DurationConverter.getFormattedDisplay("333000"));
    assertEquals("0:03", DurationConverter.getFormattedDisplay("3000"));
    assertEquals("59:59", DurationConverter.getFormattedDisplay("3599000"));
    assertEquals("1:01:00", DurationConverter.getFormattedDisplay("3660000"));
  }

  @Test
  void getValueFromDisplay() {
    assertEquals("333000", DurationConverter.getValueFromDisplay("5:33"));
    assertEquals("3000", DurationConverter.getValueFromDisplay("0:03"));
    assertEquals("3599000", DurationConverter.getValueFromDisplay("59:59"));
    assertEquals("3660000", DurationConverter.getValueFromDisplay("1:01:00"));
  }

  @Test
  void getTimeFromDisplay() {
    assertEquals(LocalDateTime.of(2000, 1, 1, 0, 5, 33), DurationConverter.getTimeFromDisplay("5:33"));
    assertEquals(LocalDateTime.of(2000, 1, 1, 0, 0, 3), DurationConverter.getTimeFromDisplay("0:03"));
    assertEquals(LocalDateTime.of(2000, 1, 1, 0, 59, 59), DurationConverter.getTimeFromDisplay("59:59"));
    assertEquals(LocalDateTime.of(2000, 1, 1, 1, 1, 0), DurationConverter.getTimeFromDisplay("1:01:00"));
  }

  @Test
  void getValueFromTime() {
    assertEquals("333000", DurationConverter.getValueFromTime(LocalDateTime.of(2000, 1, 1, 0, 5, 33)));
    assertEquals("3000", DurationConverter.getValueFromTime(LocalDateTime.of(2000, 1, 1, 0, 0, 3)));
    assertEquals("3599000", DurationConverter.getValueFromTime(LocalDateTime.of(2000, 1, 1, 0, 59, 59)));
    assertEquals("3660000", DurationConverter.getValueFromTime(LocalDateTime.of(2000, 1, 1, 1, 1, 0)));
  }
}
