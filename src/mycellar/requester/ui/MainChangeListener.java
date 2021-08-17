package mycellar.requester.ui;

import javax.swing.event.ChangeListener;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2014</p>
 * <p>Société : Seb Informatique</p>
 *
 * @author Sébastien Duché
 * @version 0.1
 * @since 11/06/14
 */
public class MainChangeListener {

  private static final MainChangeListener INSTANCE = new MainChangeListener();
  private ChangeListener cl;

  private MainChangeListener() {
  }

  public static ChangeListener getChangeListener() {
    return INSTANCE.cl;
  }

  public static void setChangeListener(ChangeListener cl) {
    INSTANCE.cl = cl;
  }

  public MainChangeListener getInstance() {
    return INSTANCE;
  }
}
