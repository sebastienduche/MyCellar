package mycellar;

import mycellar.core.MyCellarObject;
import org.apache.commons.text.StringEscapeUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.util.List;

import static mycellar.Filtre.EXTENSION_SINFO;
import static mycellar.ProgramConstants.ONE_DOT;
import static mycellar.ProgramConstants.SLASH;
import static mycellar.core.text.MyCellarLabelManagement.getError;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2021
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.6
 * @since 13/01/24
 */
public final class MyCellarUtils {

  public static boolean isNullOrEmpty(String value) {
    return value == null || value.isBlank();
  }

  public static boolean isDefined(String value) {
    return !isNullOrEmpty(value);
  }

  public static String nonNullValueOrDefault(String value, String defaultValue) {
    return value == null ? defaultValue : value;
  }

  public static void assertObjectType(MyCellarObject myCellarObject, Class<?> aClass) {
    if (!aClass.isInstance(myCellarObject)) {
      throw new ClassCastException("Invalid class cast: " + aClass);
    }
  }

  public static int safeParseInt(String value, int defaultValue) {
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException ignored) {
      return defaultValue;
    }
  }

  public static Integer parseIntOrError(String value) {
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException e) {
      Erreur.showSimpleErreur(getError("Error.enterNumericValue"));
      return null;
    }
  }

  public static BigDecimal safeStringToBigDecimal(final String value, BigDecimal defaultValue) {
    try {
      return stringToBigDecimal(value);
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }

  public static BigDecimal stringToBigDecimal(final String value) throws NumberFormatException {
    StringBuilder buf = new StringBuilder();
    for (int i = 0; i < value.length(); i++) {
      char c = value.charAt(i);
      if (c == ' ') {
        continue;
      }
      if (c == ',' || c == '.') {
        buf.append('.');
      }
      if (Character.isDigit(c)) {
        buf.append(c);
      }
    }
    return new BigDecimal(buf.toString()).setScale(2, RoundingMode.HALF_UP);
  }

  public static String getShortFilename(String sFilename) {
    String tmp = sFilename.replaceAll("\\\\", SLASH);
    int ind1 = tmp.lastIndexOf(SLASH);
    int ind2 = tmp.indexOf(ONE_DOT + EXTENSION_SINFO);
    if (ind1 != -1 && ind2 != -1) {
      tmp = tmp.substring(ind1 + 1, ind2);
    }
    return tmp;
  }

  public static String convertToHTMLString(String s) {
    return StringEscapeUtils.escapeHtml4(s);
  }

  public static String convertStringFromHTMLString(String s) {
    return StringEscapeUtils.unescapeHtml4(s);
  }

  public static String removeAccents(String s) {
    s = Normalizer.normalize(s, Normalizer.Form.NFD);
    s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
    return s;
  }

  public static String toCleanString(final Object o) {
    if (o == null) {
      return "";
    }
    String value = o.toString();
    return value == null ? "" : value.strip();
  }

  public static String removeQuotes(final String value) {
    if (value == null || value.isEmpty()) {
      return value;
    }
    if (value.charAt(0) == '"' && value.charAt(value.length() - 1) == '"') {
      return value.substring(1, value.length() - 1);
    }
    return value;
  }

  public static boolean isAnyOf(Object value, List<Object> list) {
    return list.stream().anyMatch(value::equals);
  }
}
