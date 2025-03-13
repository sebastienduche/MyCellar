package mycellar;

import mycellar.core.uicomponents.MyCellarButton;
import mycellar.core.uicomponents.MyCellarSimpleLabel;
import mycellar.frame.MainFrame;
import net.miginfocom.swing.MigLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.Serial;

import static mycellar.MyCellarUtils.toCleanString;
import static mycellar.ProgramConstants.CHAR_O;
import static mycellar.ProgramConstants.FONT_DIALOG_BOLD;
import static mycellar.ProgramConstants.isVK_ENTER;
import static mycellar.ProgramConstants.isVK_O;
import static mycellar.general.ResourceKey.MAIN_OK;


/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2003
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 2.4
 * @since 13/03/25
 */
@Deprecated
public final class Options extends JDialog {
  @Serial
  private static final long serialVersionUID = 190305;
  private static final int LARGEUR = 420;
  private static final int HAUTEUR = 230;
  private final JTextField value = new JTextField();
  private final String cle;
  private final boolean property;

  public Options(String title, String message, String propriete, String key, String textError, boolean isAProperty) {
    super(new JFrame(), "", true);
    cle = key;
    property = isAProperty;
    MyCellarSimpleLabel textControl3 = new MyCellarSimpleLabel(textError);

    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    setTitle(title);
    MyCellarSimpleLabel titleLabel = new MyCellarSimpleLabel(title);
    titleLabel.setFont(FONT_DIALOG_BOLD);
    titleLabel.setForeground(Color.red);
    titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    MyCellarSimpleLabel definition = new MyCellarSimpleLabel(message);
    MyCellarSimpleLabel textControl2 = new MyCellarSimpleLabel(propriete);
    textControl3.setForeground(Color.red);
    textControl3.setHorizontalAlignment(SwingConstants.CENTER);
    MyCellarButton valider = new MyCellarButton(MAIN_OK);
    valider.setMnemonic(CHAR_O);
    valider.addActionListener(this::valider_actionPerformed);
    addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        keylistener_actionPerformed(e);
      }
    });

    setSize(LARGEUR, HAUTEUR);
    setLocationRelativeTo(MainFrame.getInstance());
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
