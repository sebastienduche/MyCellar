package mycellar.myoptions;

import mycellar.Program;
import mycellar.core.IMyCellarComponent;
import mycellar.core.MyLinkedHashMap;
import mycellar.core.uicomponents.MyCellarButton;
import mycellar.core.uicomponents.MyCellarCheckBox;
import mycellar.core.uicomponents.MyCellarLabel;
import mycellar.core.uicomponents.MyCellarRadioButton;
import mycellar.core.uicomponents.MyCellarSimpleLabel;
import mycellar.core.uicomponents.MyCellarSpinner;
import mycellar.frame.MainFrame;
import net.miginfocom.swing.MigLayout;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import static javax.swing.SwingConstants.CENTER;
import static mycellar.MyCellarUtils.toCleanString;
import static mycellar.ProgramConstants.CHAR_O;
import static mycellar.ProgramConstants.FONT_DIALOG_BOLD;
import static mycellar.ProgramConstants.isVK_ENTER;
import static mycellar.ProgramConstants.isVK_O;
import static mycellar.general.ResourceKey.MAIN_CANCEL;
import static mycellar.general.ResourceKey.MAIN_OK;
import static mycellar.myoptions.MyOptionObjectType.MY_CELLAR_LABEL;


/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2003
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 3.4
 * @since 13/03/25
 */
public final class MyOptions extends JDialog {

  private final List<String> keyList;
  private final JComponent[] value;
  private final String title;
  private final String information;
  private final boolean withCancelButton;
  private final List<MyOptionObjectType> objectType;
  private final List<IMyCellarComponent> labelValues = new ArrayList<>();

  private final MyCellarButton valider = new MyCellarButton(MAIN_OK);
  private final MyCellarButton annuler = new MyCellarButton(MAIN_CANCEL);
  private MyCellarSimpleLabel titleLabel;
  private MyCellarSimpleLabel informationLabel;
  MyCellarSimpleLabel textControl3 = new MyCellarSimpleLabel();

  public MyOptions(String title, String information, List<MyOptionKey> resourceList) {
    super(MainFrame.getInstance(), "", true);
    keyList = resourceList.stream().map(MyOptionKey::propertyKey).toList();
    value = new JComponent[resourceList.size()];
    this.title = title;
    this.information = information;
    this.withCancelButton = false;
    objectType = resourceList.stream().map(MyOptionKey::objectType).toList();

    buildDefaulComponent();

    for (int i = 0; i < resourceList.size(); i++) {
      MyOptionKey myOptionKey = resourceList.get(i);
      if (MyOptionObjectType.JTEXT_FIELD.equals(myOptionKey.objectType())) {
        value[i] = new JTextField(myOptionKey.defaultValue());
        labelValues.add(new MyCellarSimpleLabel());
      } else if (MyOptionObjectType.MY_CELLAR_SPINNER.equals(myOptionKey.objectType())) {
        final MyCellarSpinner jspi = new MyCellarSpinner(0, 99999);
        jspi.setValue(Integer.parseInt(myOptionKey.defaultValue()));
        value[i] = jspi;
      } else if (MyOptionObjectType.MY_CELLAR_CHECK_BOX.equals(myOptionKey.objectType())) {
        boolean bool = "true".equals(myOptionKey.defaultValue());
        value[i] = new MyCellarCheckBox(myOptionKey.resource(), bool);
        labelValues.add(new MyCellarSimpleLabel());
      } else if (MyOptionObjectType.MY_CELLAR_RADIO_BUTTON.equals(myOptionKey.objectType())) {
        boolean bool = "true".equals(myOptionKey.defaultValue());
        if (myOptionKey.resource() == null) {
          value[i] = new MyCellarRadioButton(myOptionKey.labelKey(), bool);
        } else {
          value[i] = new MyCellarRadioButton(myOptionKey.resource(), bool);
        }
        value[i].setEnabled(false);
        labelValues.add(new MyCellarSimpleLabel());
        if (i > 0) {
          if (MyOptionObjectType.MY_CELLAR_RADIO_BUTTON.equals(resourceList.get(i - 1).objectType()) && resourceList.get(i - 1).propertyKey().equals(resourceList.get(i).propertyKey())) {
            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add((MyCellarRadioButton) value[i - 1]);
            buttonGroup.add((MyCellarRadioButton) value[i]);
            value[i - 1].setEnabled(true);
            value[i].setEnabled(true);
          }
        }
      } else if (MY_CELLAR_LABEL.equals(myOptionKey.objectType())) {
        MyCellarLabel simpleLabel = new MyCellarLabel(myOptionKey.resource());
        simpleLabel.setForeground(Color.red);
        labelValues.add(simpleLabel);
      }
    }
    buildFrame();
  }

  private void buildFrame() {
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
    for (int i = 0; i < objectType.size(); i++) {
      if (MY_CELLAR_LABEL.equals(objectType.get(i))) {
        getContentPane().add((Component) labelValues.get(i), "wrap");
      } else {
        getContentPane().add((Component) labelValues.get(i), "grow");
        getContentPane().add(value[i], "grow, wrap, gapleft 15px");
      }
    }
    getContentPane().add(textControl3, "wrap");

    if (withCancelButton) {
      getContentPane().add(valider, "span 2, split 2, center");
      getContentPane().add(annuler, "");
    } else {
      getContentPane().add(valider, "span 2, center");
    }
    pack();
    setLocationRelativeTo(MainFrame.getInstance());
    setResizable(false);
  }

  private void buildDefaulComponent() {
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setTitle(title);
    titleLabel = new MyCellarSimpleLabel(title);
    informationLabel = new MyCellarSimpleLabel(information);
    titleLabel.setFont(FONT_DIALOG_BOLD);
    titleLabel.setForeground(Color.red);
    titleLabel.setText(title);
    titleLabel.setHorizontalAlignment(CENTER);
    informationLabel.setText(information);
    textControl3.setForeground(Color.red);
    textControl3.setHorizontalAlignment(CENTER);
    valider.setMnemonic(CHAR_O);
  }

  private void valider_actionPerformed(ActionEvent e) {
    if (!Program.hasOpenedFile()) {
      throw new RuntimeException("Unable to save a property because no file is opened");
    }
    String defaut = null;
    int nb_jradio = 0;
    for (int i = 0; i < value.length; i++) {
      switch (value[i]) {
        case JTextField jTextField -> {
          saveInConfig(i, toCleanString(jTextField.getText()));
          if (defaut == null) {
            defaut = toCleanString(jTextField.getText());
          }
        }
        case MyCellarSpinner jspi -> saveInConfig(i, jspi.getValue().toString());
        case MyCellarCheckBox jchk when jchk.isSelected() -> saveInConfig(i, defaut);
        case MyCellarRadioButton jrb -> {
          if (jrb.isSelected()) {
            saveInConfig(i, Integer.toString(nb_jradio));
          }
          nb_jradio++;
        }
        case null, default -> nb_jradio = 0;
      }
    }
    dispose();
  }

  private void saveInConfig(int i, String resul) {
    final MyLinkedHashMap config = Program.getOpenedFile().getCaveConfig();
    if (config != null && !keyList.get(i).isEmpty()) {
      config.put(keyList.get(i), resul);
    }
  }

  private void keylistener_actionPerformed(KeyEvent e) {
    if (isVK_O(e) || isVK_ENTER(e)) {
      valider_actionPerformed(null);
    }
  }

}
