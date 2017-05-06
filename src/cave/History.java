package Cave;

import java.util.Calendar;
import java.io.*;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.3
 * @since 04/03/15
 */

public class History implements Serializable {

  private Bouteille m_oBottle;
  private String m_sDate;
  private int m_nType;
  public static int ADD = 0;
  public static int MODIFY = 1;
  public static int DEL = 2;
  static final long serialVersionUID = 030107;

  /**
   * History: Contructeur avec une bouteille et un type d'action
   *
   * @param _oBottle Bouteille
   * @param _nType int
   */
  public History(Bouteille _oBottle, int _nType) {
    m_oBottle = _oBottle;
    m_nType = _nType;
    Calendar oCal = Calendar.getInstance();
    m_sDate = "";
    if (oCal.get(Calendar.DATE) < 10) {
      m_sDate = "0";
    }
    m_sDate += oCal.get(Calendar.DATE);
    m_sDate += "/";
    if (oCal.get(Calendar.MONTH) < 9) {
      m_sDate += "0";
    }
    m_sDate += (oCal.get(Calendar.MONTH) + 1);
    m_sDate += "/";
    m_sDate += oCal.get(Calendar.YEAR);
  }

  /**
   * GetType
   *
   * @return int
   */
  public int GetType() {
    return m_nType;
  }

  /**
   * GetBottle
   *
   * @return Bouteille
   */
  public Bouteille GetBottle() {
    return m_oBottle;
  }

  /**
   * GetDate
   *
   * @return String
   */
  public String GetDate() {
    return m_sDate;
  }
}
