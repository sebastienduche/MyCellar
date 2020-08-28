package mycellar;

import mycellar.core.LabelType;
import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarLabel;
import net.miginfocom.swing.MigLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2003</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 1.5
 * @since 28/08/20
 */
public class Options extends JDialog {
  private final MyCellarLabel textControl1 = new MyCellarLabel();
  private final MyCellarLabel textControl2 = new MyCellarLabel();
  private final MyCellarLabel definition = new MyCellarLabel();
  private final MyCellarButton valider = new MyCellarButton(LabelType.INFO_OTHER, "Main.OK");
  private final MyCellarLabel textControl3 = new MyCellarLabel();
  private final int LARGEUR = 420;
  private final int HAUTEUR = 230;
  private final JTextField value = new JTextField();
  private final String cle;
  private final boolean property;
  static final long serialVersionUID = 190305;

  /**
   * Options: Constructeur pour la fenêtre d'option
   *
   * @param title String: Titre de la fenêtre.
   * @param message String: Message de la fenêtre.
   * @param propriete String: Propriété à renseigner.
   * @param default_value String: Valeur par défaut.
   * @param key String: Clé de la propiété.
   * @param textError String: Texte de l'erreur.
   * @param isAProperty boolean: true si c'est une propiété.
   */
  public Options(String title, String message, String propriete, String default_value, String key, String textError, boolean isAProperty) {

    super(new JFrame(), "", true);
    cle = key;
    property = isAProperty;
    textControl3.setText(textError);
    try {
      jbInit(title, message, propriete, default_value);
    }
    catch (Exception e) {
      Program.showException(e);
    }
  }

  /**
   * jbInit: Fonction d'initialisation.
   *
   * @param title String: Titre de la fenêtre.
   * @param message String: Message de la fenêtre.
   * @param propriete String: Propriété à renseigner.
   * @param default_value String: Valeur par défaut.
   * @throws Exception
   */
  private void jbInit(String title, String message, String propriete, String default_value) {

    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    setTitle(title);
    textControl1.setFont(Program.FONT_DIALOG_SMALL);
    textControl1.setForeground(Color.red);
    textControl1.setText(title);
    textControl1.setHorizontalAlignment(SwingConstants.CENTER);
    definition.setText(message);
    textControl2.setText(propriete);
    textControl3.setForeground(Color.red);
    textControl3.setHorizontalAlignment(SwingConstants.CENTER);
    valider.setMnemonic('O');
    value.setText(default_value);
    valider.addActionListener(this::valider_actionPerformed);
    addKeyListener(new KeyListener() {
      @Override
      public void keyReleased(KeyEvent e) {}
      @Override
      public void keyPressed(KeyEvent e) {
        keylistener_actionPerformed(e);
      }
      @Override
      public void keyTyped(KeyEvent e) {}
    });

    setSize(LARGEUR, HAUTEUR);
    setLocationRelativeTo(Start.getInstance());
    setLayout(new MigLayout("","grow",""));
    getContentPane().add(textControl1, "grow, wrap");
    getContentPane().add(definition, "gaptop 15px, grow, wrap");
    getContentPane().add(textControl3, "grow, wrap");
    getContentPane().add(textControl2, "split 2");
    getContentPane().add(value, "grow, wrap");
    getContentPane().add(valider, "center, gaptop 10px");
    setResizable(false);
  }

  /**
   * valider_actionPerformed: Valider la propriété et quitter.
   *
   * @param e ActionEvent
   */
  private void valider_actionPerformed(ActionEvent e) {
    if (property) {
      Program.putCaveConfigString(cle, value.getText().trim());
    }
    dispose();
  }

  /**
   * keylistener_actionPerformed: Ecoute clavier
   *
   * @param e KeyEvent
   */
  private void keylistener_actionPerformed(KeyEvent e) {
    if (e.getKeyCode() == 'o' || e.getKeyCode() == 'O' || e.getKeyCode() == KeyEvent.VK_ENTER) {
      valider_actionPerformed(null);
    }
  }

  /**
   * getValue: Retourne la valeur définie pour la propriété.
   *
   * @return String
   */
  public String getValue() {
    return value.getText().trim();
  }

}
