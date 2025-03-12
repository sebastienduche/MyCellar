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

  @Deprecated
  public static final String JTEXT_FIELD = "JTextField";
  @Deprecated
  public static final String MY_CELLAR_SPINNER = "MyCellarSpinner";
  @Deprecated
  public static final String MY_CELLAR_CHECK_BOX = "MyCellarCheckBox";
  @Deprecated
  public static final String MY_CELLAR_RADIO_BUTTON = "MyCellarRadioButton";
  @Deprecated
  public static final String MY_CELLAR_LABEL = "MyCellarLabel";
  private final List<String> keyList;
  private final JComponent[] value;
  private final String title;
  private final String information;
  private final boolean withCancelButton;
  private final List<String> objectType;
  private final List<IMyCellarComponent> labelValues = new ArrayList<>();

  private final MyCellarButton valider = new MyCellarButton(MAIN_OK);
  private final MyCellarButton annuler = new MyCellarButton(MAIN_CANCEL);
  private MyCellarSimpleLabel titleLabel;
  private MyCellarSimpleLabel informationLabel;
  MyCellarSimpleLabel textControl3 = new MyCellarSimpleLabel();

  @Deprecated(since = "version90")
  public MyOptions(String title, String information, List<String> resourceList, List<String> defaultValueList, List<String> cle2, List<String> type_objet,
                   boolean cancel) {
    super(MainFrame.getInstance(), "", true);
    keyList = cle2;
    value = new JComponent[resourceList.size()];
    this.title = title;
    this.information = information;
    this.withCancelButton = cancel;
    objectType = type_objet;

    buildDefaulComponent();

    for (int i = 0; i < resourceList.size(); i++) {
      value[i] = null;
      MyCellarSimpleLabel simpleLabel = new MyCellarSimpleLabel(resourceList.get(i));
//      labelValues.add(simpleLabel);
      if (type_objet.get(i).equals(JTEXT_FIELD)) {
        value[i] = new JTextField(defaultValueList.get(i));
      } else if (type_objet.get(i).equals(MY_CELLAR_SPINNER)) {
        final MyCellarSpinner jspi = new MyCellarSpinner(0, 99999);
        jspi.setValue(Integer.parseInt(defaultValueList.get(i)));
        value[i] = jspi;
      } else if (type_objet.get(i).equals(MY_CELLAR_CHECK_BOX)) {
        boolean bool = "true".equals(defaultValueList.get(i));
        value[i] = new MyCellarCheckBox(resourceList.get(i), bool);
        labelValues.add(new MyCellarSimpleLabel());
      } else if (type_objet.get(i).equals(MY_CELLAR_RADIO_BUTTON)) {
        boolean bool = "true".equals(defaultValueList.get(i));
        value[i] = new MyCellarRadioButton(resourceList.get(i), bool);
        value[i].setEnabled(false);
        labelValues.add(new MyCellarSimpleLabel());
        if (i > 0) {
          if (type_objet.get(i - 1).equals(MY_CELLAR_RADIO_BUTTON) && keyList.get(i - 1).equals(keyList.get(i))) {
            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add((MyCellarRadioButton) value[i - 1]);
            buttonGroup.add((MyCellarRadioButton) value[i]);
            value[i - 1].setEnabled(true);
            value[i].setEnabled(true);
          }
        }
      } else if (type_objet.get(i).equals(MY_CELLAR_LABEL)) {
        simpleLabel.setForeground(Color.red);
        labelValues.add(simpleLabel);
      }
    }
    buildFrame();
  }

  //  public MyOptions(String title, String information, List<IResource> resourceList, List<String> defaultValueList, List<String> cle2, List<String> type_objet) {
  public MyOptions(String title, String information, List<MyOptionKey> resourceList) {
    super(MainFrame.getInstance(), "", true);
    keyList = resourceList.stream().map(MyOptionKey::propertyKey).toList();
    value = new JComponent[resourceList.size()];
    this.title = title;
    this.information = information;
    this.withCancelButton = false;
    objectType = buildObjectTypes(resourceList);

    buildDefaulComponent();

    for (int i = 0; i < resourceList.size(); i++) {
      MyOptionKey myOptionKey = resourceList.get(i);
      value[i] = null;
      MyCellarLabel simpleLabel = new MyCellarLabel(myOptionKey.resource());
//      labelValues.add(simpleLabel);
      if (MyOptionObjectType.JTEXT_FIELD.equals(myOptionKey.objectType())) {
        value[i] = new JTextField(myOptionKey.defaultValue());
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
        value[i] = new MyCellarRadioButton(myOptionKey.resource(), bool);
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
      } else if (MyOptionObjectType.MY_CELLAR_LABEL.equals(myOptionKey.objectType())) {
        simpleLabel.setForeground(Color.red);
        labelValues.add(simpleLabel);
      }
    }
    buildFrame();
  }

  private List<String> buildObjectTypes(List<MyOptionKey> resourceList) {
    List<String> objects = new ArrayList<>();
    for (MyOptionKey myOptionKey : resourceList) {
      String value = switch (myOptionKey.objectType()) {
        case JTEXT_FIELD -> JTEXT_FIELD;
        case MY_CELLAR_SPINNER -> MY_CELLAR_SPINNER;
        case MY_CELLAR_CHECK_BOX -> MY_CELLAR_CHECK_BOX;
        case MY_CELLAR_RADIO_BUTTON -> MY_CELLAR_RADIO_BUTTON;
        case MY_CELLAR_LABEL -> MY_CELLAR_LABEL;
      };
      objects.add(value);
    }
    return objects;
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
      if (objectType.get(i).equals(MY_CELLAR_LABEL)) {
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
        case MyCellarSpinner jspi -> {
          saveInConfig(i, jspi.getValue().toString());
        }
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
