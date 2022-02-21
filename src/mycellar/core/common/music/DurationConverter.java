package mycellar.core.common.music;

import mycellar.MyCellarUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

import static mycellar.ProgramConstants.DOUBLE_DOT;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2021</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.1
 * @since 15/05/21
 */
public class DurationConverter {

  private static final int SECOND_IN_HOUR = 3600;
  private static final int SECOND_IN_MINUTE = 60;
  private static final int MULTIPLICATOR = 1000;

  public static String getFormattedDisplay(String duration) {
    if (duration == null) {
      return "";
    }
    int value = MyCellarUtils.safeParseInt(duration, 0);
    value /= MULTIPLICATOR;
    int hour = value / SECOND_IN_HOUR;
    int newValue = value - (hour * SECOND_IN_HOUR);
    int minute = newValue / SECOND_IN_MINUTE;
    int second = newValue - (minute * SECOND_IN_MINUTE);
    if (hour > 0) {
      return hour +
          DOUBLE_DOT +
          StringUtils.leftPad(Integer.toString(minute), 2, "0") +
          DOUBLE_DOT +
          StringUtils.leftPad(Integer.toString(second), 2, "0");
    } else {
      return minute +
          DOUBLE_DOT +
          StringUtils.leftPad(Integer.toString(second), 2, "0");
    }
  }

  public static String getValueFromDisplay(String duration) {
    if (duration == null) {
      return "";
    }
    String[] values = duration.split(DOUBLE_DOT);
    int value = 0;
    int i = 0;
    if (values.length == 3) {
      value = MyCellarUtils.safeParseInt(values[i++], 0) * SECOND_IN_HOUR;
    }
    value += MyCellarUtils.safeParseInt(values[i++], 0) * SECOND_IN_MINUTE;
    value += MyCellarUtils.safeParseInt(values[i++], 0);
    value *= MULTIPLICATOR;
    return Integer.toString(value);
  }

  public static LocalDateTime getTimeFromDisplay(String duration) {
    if (duration == null) {
      return null;
    }
    String[] values = duration.split(DOUBLE_DOT);
    int i = 0;
    int hour = 0;
    if (values.length == 3) {
      hour = MyCellarUtils.safeParseInt(values[i++], 0);
    }
    int minute = MyCellarUtils.safeParseInt(values[i++], 0);
    int second = MyCellarUtils.safeParseInt(values[i++], 0);
    return LocalDateTime.of(2000, 1, 1, hour, minute, second);
  }

  public static String getValueFromTime(LocalDateTime duration) {
    if (duration == null) {
      return "";
    }
    int value = duration.getHour() * SECOND_IN_HOUR;
    value += duration.getMinute() * SECOND_IN_MINUTE;
    value += duration.getSecond();
    value *= MULTIPLICATOR;
    return Integer.toString(value);
  }

}
