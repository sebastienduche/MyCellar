package mycellar;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextField;

import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarLabel;
import net.miginfocom.swing.MigLayout;


/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2003</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 1.1
 * @since 04/08/17
 */
public class Options extends JDialog {
  private MyCellarLabel textControl1 = new MyCellarLabel();
  private MyCellarLabel textControl2 = new MyCellarLabel();
  private MyCellarLabel definition = new MyCellarLabel();
  private MyCellarButton valider = new MyCellarButton();
  private MyCellarLabel textControl3 = new MyCellarLabel();
  private int LARGEUR = 420;
  private int HAUTEUR = 230;
  private JTextField value = new JTextField();
  private String cle;
  private boolean property;
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
  private void jbInit(String title, String message, String propriete, String default_value) throws Exception {

    this.setDefaultCloseOperation(0);
    this.setTitle(title);
    textControl1.setFont(Program.font_dialog_small);
    textControl1.setForeground(Color.red);
    textControl1.setText(title);
    textControl1.setHorizontalAlignment(0);
    definition.setText(message);
    textControl2.setText(propriete);
    textControl3.setForeground(Color.red);
    textControl3.setHorizontalAlignment(0);
    valider.setText(Program.getLabel("Main.OK"));
    valider.setMnemonic('O');
    value.setText(default_value);
    valider.addActionListener((e) -> valider_actionPerformed(e));
    this.addKeyListener(new java.awt.event.KeyListener() {
      public void keyReleased(java.awt.event.KeyEvent e) {}

      public void keyPressed(java.awt.event.KeyEvent e) {
        keylistener_actionPerformed(e);
      }

      public void keyTyped(java.awt.event.KeyEvent e) {}
    });

    this.setSize(LARGEUR, HAUTEUR);
    this.setLocationRelativeTo(null);
    setLayout(new MigLayout("","grow",""));
    this.getContentPane().add(textControl1, "grow, wrap");
    this.getContentPane().add(definition, "gaptop 15px, grow, wrap");
    this.getContentPane().add(textControl3, "grow, wrap");
    this.getContentPane().add(textControl2, "split 2");
    this.getContentPane().add(value, "grow, wrap");
    this.getContentPane().add(valider, "center, gaptop 10px");
    this.setResizable(false);

  }

  /**
   * valider_actionPerformed: Valider la propriété et quitter.
   *
   * @param e ActionEvent
   */
  void valider_actionPerformed(ActionEvent e) {
    if (property) {
      Program.putCaveConfigString(cle, value.getText().trim());
    }
    this.dispose();
  }

  /**
   * keylistener_actionPerformed: Ecoute clavier
   *
   * @param e KeyEvent
   */
  void keylistener_actionPerformed(KeyEvent e) {
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
