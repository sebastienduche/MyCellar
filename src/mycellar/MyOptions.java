package mycellar;

import mycellar.core.MyLinkedHashMap;
import mycellar.core.uicomponents.MyCellarButton;
import mycellar.core.uicomponents.MyCellarCheckBox;
import mycellar.core.uicomponents.MyCellarRadioButton;
import mycellar.core.uicomponents.MyCellarSimpleLabel;
import mycellar.core.uicomponents.MyCellarSpinner;
import mycellar.frame.MainFrame;
import mycellar.general.ResourceKey;
import net.miginfocom.swing.MigLayout;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import static javax.swing.SwingConstants.CENTER;
import static mycellar.MyCellarUtils.toCleanString;
import static mycellar.ProgramConstants.CHAR_O;
import static mycellar.ProgramConstants.FONT_DIALOG_BOLD;
import static mycellar.ProgramConstants.isVK_ENTER;
import static mycellar.ProgramConstants.isVK_O;


/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2003
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 3.3
 * @since 12/03/25
 */
public final class MyOptions extends JDialog {

  public static final String JTEXT_FIELD = "JTextField";
  public static final String MY_CELLAR_SPINNER = "MyCellarSpinner";
  public static final String MY_CELLAR_CHECK_BOX = "MyCellarCheckBox";
  public static final String MY_CELLAR_RADIO_BUTTON = "MyCellarRadioButton";
  public static final String MY_CELLAR_LABEL = "MyCellarLabel";
  private final List<String> cle;
  private final JComponent[] value;
  private final int taille_value;
  private final String[] resul;

  public MyOptions(String title, String information, List<String> resourceList, List<String> defaultValueList, List<String> cle2, List<String> type_objet,
                   boolean cancel) {

    super(MainFrame.getInstance(), "", true);
    cle = cle2;
    taille_value = resourceList.size();
    resul = new String[taille_value];
    value = new JComponent[taille_value];
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setTitle(title);
    MyCellarSimpleLabel titleLabel = new MyCellarSimpleLabel(title);
    titleLabel.setFont(FONT_DIALOG_BOLD);
    titleLabel.setForeground(Color.red);
    titleLabel.setText(title);
    titleLabel.setHorizontalAlignment(CENTER);
    MyCellarSimpleLabel informationLabel = new MyCellarSimpleLabel(information);
    MyCellarButton valider = new MyCellarButton(ResourceKey.MAIN_OK);
    MyCellarButton annuler = new MyCellarButton(ResourceKey.MAIN_CANCEL);
    informationLabel.setText(information);
    MyCellarSimpleLabel textControl3 = new MyCellarSimpleLabel();
    textControl3.setForeground(Color.red);
    textControl3.setHorizontalAlignment(CENTER);
    valider.setMnemonic(CHAR_O);

    MyCellarSimpleLabel[] label_value = new MyCellarSimpleLabel[taille_value];
    for (int i = 0; i < resourceList.size(); i++) {
      value[i] = null;
      label_value[i] = new MyCellarSimpleLabel(resourceList.get(i));
      if (type_objet.get(i).equals(JTEXT_FIELD)) {
        value[i] = new JTextField(defaultValueList.get(i));
      }
      if (type_objet.get(i).equals(MY_CELLAR_SPINNER)) {
        final MyCellarSpinner jspi = new MyCellarSpinner(0, 99999);
        jspi.setValue(Integer.parseInt(defaultValueList.get(i)));
        value[i] = jspi;
      }
      if (type_objet.get(i).equals(MY_CELLAR_CHECK_BOX)) {
        boolean bool = defaultValueList.get(i).equals("true");
        value[i] = new MyCellarCheckBox(resourceList.get(i), bool);
        label_value[i] = new MyCellarSimpleLabel();
      }
      if (type_objet.get(i).equals(MY_CELLAR_RADIO_BUTTON)) {
        boolean bool = defaultValueList.get(i).equals("true");
        value[i] = new MyCellarRadioButton(resourceList.get(i), bool);
        value[i].setEnabled(false);
        label_value[i] = new MyCellarSimpleLabel();
        if (i > 0) {
          if (type_objet.get(i - 1).equals(MY_CELLAR_RADIO_BUTTON) && cle.get(i - 1).equals(cle.get(i))) {
            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add((MyCellarRadioButton) value[i - 1]);
            buttonGroup.add((MyCellarRadioButton) value[i]);
            value[i - 1].setEnabled(true);
            value[i].setEnabled(true);
          }
        }
      }

      if (type_objet.get(i).equals(MY_CELLAR_LABEL)) {
        label_value[i].setForeground(Color.red);
      }
    }
    valider.addActionListener(this::valider_actionPerformed);
    annuler.addActionListener((e) -> dispose());

    addKeyListener(new KeyListener() {
      @Override
      public void keyReleased(KeyEvent e) {
      }

      @Override
      public void keyPressed(KeyEvent e) {
        keylistener_actionPerformed(e);
      }

      @Override
      public void keyTyped(KeyEvent e) {
      }
    });

    getContentPane().setLayout(new MigLayout("", "[grow][grow]", "[]15px[][]15px[]"));
    getContentPane().add(titleLabel, "center, grow, span 2, wrap");
    getContentPane().add(new JLabel(), "span 2, wrap");
    getContentPane().add(informationLabel, "span 2, wrap");
    for (int i = 0; i < taille_value; i++) {
      if (type_objet.get(i).equals(MY_CELLAR_LABEL)) {
        getContentPane().add(label_value[i], "wrap");
      } else {
        getContentPane().add(label_value[i], "grow");
        getContentPane().add(value[i], "grow, wrap, gapleft 15px");
      }
    }
    getContentPane().add(textControl3, "wrap");

    if (cancel) {
      getContentPane().add(valider, "span 2, split 2, center");
      getContentPane().add(annuler, "");
    } else {
      getContentPane().add(valider, "span 2, center");
    }
    pack();
    setLocationRelativeTo(MainFrame.getInstance());
    setResizable(false);
  }

  private void valider_actionPerformed(ActionEvent e) {
    if (!Program.hasOpenedFile()) {
      throw new RuntimeException("Unable to save a property because no file is opened");
    }
    String defaut = null;
    int nb_jradio = 0;
    for (int i = 0; i < taille_value; i++) {
      if (value[i] instanceof JTextField jtex) {
        resul[i] = toCleanString(jtex.getText());
        saveInConfig(i, resul[i]);
        if (defaut == null) {
          defaut = toCleanString(jtex.getText());
        }
      } else if (value[i] instanceof MyCellarSpinner jspi) {
        resul[i] = jspi.getValue().toString();
        saveInConfig(i, resul[i]);
      } else if (value[i] instanceof MyCellarCheckBox jchk && jchk.isSelected()) {
        resul[i] = defaut;
        saveInConfig(i, defaut);
      } else if (value[i] instanceof MyCellarRadioButton jrb) {
        if (jrb.isSelected()) {
          resul[i] = Integer.toString(nb_jradio);
          saveInConfig(i, resul[i]);
        }
        nb_jradio++;
      } else {
        nb_jradio = 0;
      }
    }
    dispose();
  }

  private void saveInConfig(int i, String resul) {
    final MyLinkedHashMap config = Program.getOpenedFile().getCaveConfig();
    if (config != null && !cle.get(i).isEmpty()) {
      config.put(cle.get(i), resul);
    }
  }

  private void keylistener_actionPerformed(KeyEvent e) {
    if (isVK_O(e) || isVK_ENTER(e)) {
      valider_actionPerformed(null);
    }
  }

}
