package mycellar;

import mycellar.core.LabelType;
import mycellar.core.uicomponents.MyCellarButton;
import mycellar.core.uicomponents.MyCellarLabel;
import net.miginfocom.swing.MigLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import static mycellar.Program.toCleanString;
import static mycellar.ProgramConstants.CHAR_O;
import static mycellar.ProgramConstants.FONT_DIALOG_SMALL;
import static mycellar.ProgramConstants.isVK_ENTER;
import static mycellar.ProgramConstants.isVK_O;


/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2003</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 1.9
 * @since 16/12/21
 */
@Deprecated
public class Options extends JDialog {
  static final long serialVersionUID = 190305;
  private static final int LARGEUR = 420;
  private static final int HAUTEUR = 230;
  private final MyCellarButton valider = new MyCellarButton(LabelType.INFO_OTHER, "Main.OK");
  private final MyCellarLabel textControl3 = new MyCellarLabel();
  private final JTextField value = new JTextField();
  private final String cle;
  private final boolean property;

  public Options(String title, String message, String propriete, String default_value, String key, String textError, boolean isAProperty) {
    super(new JFrame(), "", true);
    cle = key;
    property = isAProperty;
    textControl3.setText(textError);
    jbInit(title, message, propriete, default_value);
  }

  private void jbInit(String title, String message, String propriete, String default_value) {

    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    setTitle(title);
    MyCellarLabel titleLabel = new MyCellarLabel(title);
    titleLabel.setFont(FONT_DIALOG_SMALL);
    titleLabel.setForeground(Color.red);
    titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    MyCellarLabel definition = new MyCellarLabel(message);
    MyCellarLabel textControl2 = new MyCellarLabel(propriete);
    textControl3.setForeground(Color.red);
    textControl3.setHorizontalAlignment(SwingConstants.CENTER);
    valider.setMnemonic(CHAR_O);
    value.setText(default_value);
    valider.addActionListener(this::valider_actionPerformed);
    addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        keylistener_actionPerformed(e);
      }
    });

    setSize(LARGEUR, HAUTEUR);
    setLocationRelativeTo(Start.getInstance());
    setLayout(new MigLayout("", "grow", ""));
    getContentPane().add(titleLabel, "grow, wrap");
    getContentPane().add(definition, "gaptop 15px, grow, wrap");
    getContentPane().add(textControl3, "grow, wrap");
    getContentPane().add(textControl2, "split 2");
    getContentPane().add(value, "grow, wrap");
    getContentPane().add(valider, "center, gaptop 10px");
    setResizable(false);
  }

  private void valider_actionPerformed(ActionEvent e) {
    if (property) {
      Program.putCaveConfigString(cle, toCleanString(value.getText()));
    }
    dispose();
  }

  private void keylistener_actionPerformed(KeyEvent e) {
    if (isVK_O(e) || isVK_ENTER(e)) {
      valider_actionPerformed(null);
    }
  }

  public String getValue() {
    return toCleanString(value.getText());
  }

}
