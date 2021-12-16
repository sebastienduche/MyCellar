package mycellar;

import mycellar.core.LabelType;
import mycellar.core.MyLinkedHashMap;
import mycellar.core.uicomponents.MyCellarButton;
import mycellar.core.uicomponents.MyCellarCheckBox;
import mycellar.core.uicomponents.MyCellarLabel;
import mycellar.core.uicomponents.MyCellarRadioButton;
import mycellar.core.uicomponents.MyCellarSpinner;
import net.miginfocom.swing.MigLayout;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collections;
import java.util.List;

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
 * @version 2.6
 * @since 16/12/21
 */
public class MyOptions extends JDialog {
  @SuppressWarnings("deprecation")
  private final MyCellarLabel textControl3 = new MyCellarLabel();
  private final ButtonGroup buttonGroup = new ButtonGroup();
  private final List<String> cle;
  private final MyLinkedHashMap config;
  private final boolean cancel;
  private JComponent[] value;
  private JTextField[] labelEdit;
  private int taille_value = 0;
  private String[] resul;
  private boolean isLabelEdit = false;

  public MyOptions(String title, String message, String message2, List<String> propriete, List<String> default_value, List<String> cle2, List<String> type_objet,
                   MyLinkedHashMap config1, boolean cancel) {

    super(Start.getInstance(), "", true);
    config = config1;
    cle = cle2;
    this.cancel = cancel;
    jbInit(title, message, message2, propriete, default_value, type_objet);
  }

  MyOptions(String title, String message, List<String> type_objet, String erreur,
            MyLinkedHashMap config1, boolean cancel, boolean isLabelEdit) {

    super(Start.getInstance(), "", true);
    config = config1;
    cle = Collections.singletonList("");
    this.cancel = cancel;
    this.isLabelEdit = isLabelEdit;
    textControl3.setText(erreur);
    jbInit(title, message, "", Collections.singletonList(""), Collections.singletonList(""), type_objet);
  }

  private void jbInit(String title, String message, String message2, List<String> propriete, List<String> default_value, List<String> type_objet) {

    taille_value = propriete.size();
    MyCellarLabel[] label_value = new MyCellarLabel[taille_value];
    resul = new String[taille_value];
    value = new JComponent[taille_value];
    labelEdit = new JTextField[taille_value];
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setTitle(title);
    MyCellarLabel textControl1 = new MyCellarLabel(title);
    textControl1.setFont(FONT_DIALOG_SMALL);
    textControl1.setForeground(Color.red);
    textControl1.setText(title);
    textControl1.setHorizontalAlignment(SwingConstants.CENTER);
    MyCellarLabel definition = new MyCellarLabel(message);
    MyCellarLabel definition2 = new MyCellarLabel(message2);
    MyCellarButton valider = new MyCellarButton(LabelType.INFO_OTHER, "Main.OK");
    MyCellarButton annuler = new MyCellarButton(LabelType.INFO, "055");
    definition.setText(message);
    definition2.setText(message2);
    textControl3.setForeground(Color.red);
    textControl3.setHorizontalAlignment(SwingConstants.CENTER);
    valider.setMnemonic(CHAR_O);

    for (int i = 0; i < propriete.size(); i++) {
      value[i] = null;
      labelEdit[i] = new JTextField();
      if (type_objet.get(i).equals("JTextField")) {
        value[i] = new JTextField(default_value.get(i));
      }
      if (type_objet.get(i).equals("MyCellarSpinner")) {
        final MyCellarSpinner jspi = new MyCellarSpinner(0, 99999);
        jspi.setValue(Integer.parseInt(default_value.get(i)));
        value[i] = jspi;
      }
      if (type_objet.get(i).equals("MyCellarCheckBox")) {
        boolean bool = default_value.get(i).equals("true");
        value[i] = new MyCellarCheckBox("", bool);
      }
      if (type_objet.get(i).equals("MyCellarRadioButton")) {
        boolean bool = default_value.get(i).equals("true");
        value[i] = new MyCellarRadioButton("", bool);
        value[i].setEnabled(false);
        if (i > 0) {
          if (type_objet.get(i - 1).equals("MyCellarRadioButton") && cle.get(i - 1).equals(cle.get(i))) {
            buttonGroup.add((MyCellarRadioButton) value[i - 1]);
            buttonGroup.add((MyCellarRadioButton) value[i]);
            value[i - 1].setEnabled(true);
            value[i].setEnabled(true);
          }
        }
      }
      label_value[i] = new MyCellarLabel(propriete.get(i));
      if (type_objet.get(i).equals("MyCellarLabel")) {
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
    getContentPane().add(textControl1, "center, grow, span 2, wrap");
    getContentPane().add(definition, "span 2, wrap");
    getContentPane().add(definition2, "span 2, wrap");
    for (int i = 0; i < taille_value; i++) {
      if (type_objet.get(i).equals("MyCellarLabel")) {
        getContentPane().add(label_value[i], "wrap");
      } else {
        if (isLabelEdit)
          getContentPane().add(labelEdit[i], "grow");
        else
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
    setLocationRelativeTo(Start.getInstance());
    setResizable(false);
  }

  private void valider_actionPerformed(ActionEvent e) {
    String defaut = null;
    int nb_jradio = 0;
    for (int i = 0; i < taille_value; i++) {
      if (isLabelEdit) {
        JTextField jtf = labelEdit[i];
        cle.set(i, toCleanString(jtf.getText()));
      }
      if (value[i] instanceof JTextField) {
        JTextField jtex = (JTextField) value[i];
        resul[i] = toCleanString(jtex.getText());
        if (config != null && !cle.get(i).isEmpty()) {
          config.put(cle.get(i), resul[i]);
        }
        if (defaut == null) {
          defaut = toCleanString(jtex.getText());
        }
      }
      if (value[i] instanceof MyCellarSpinner) {
        MyCellarSpinner jspi = (MyCellarSpinner) value[i];
        resul[i] = jspi.getValue().toString();
        if (config != null && !cle.get(i).isEmpty()) {
          config.put(cle.get(i), resul[i]);
        }
      }
      if (value[i] instanceof MyCellarCheckBox) {
        MyCellarCheckBox jchk = (MyCellarCheckBox) value[i];
        if (jchk.isSelected()) {
          resul[i] = defaut;
          if (config != null && !cle.get(i).isEmpty()) {
            config.put(cle.get(i), defaut);
          }
        }
      }
      if (value[i] instanceof MyCellarRadioButton) {
        MyCellarRadioButton jrb = (MyCellarRadioButton) value[i];
        if (jrb.isSelected()) {
          resul[i] = Integer.toString(nb_jradio);
          if (config != null && !cle.get(i).isEmpty()) {
            config.put(cle.get(i), resul[i]);
          }
        }
        nb_jradio++;
      } else {
        nb_jradio = 0;
      }
    }
    dispose();
  }

  private void keylistener_actionPerformed(KeyEvent e) {
    if (isVK_O(e) || isVK_ENTER(e)) {
      valider_actionPerformed(null);
    }
  }

}
