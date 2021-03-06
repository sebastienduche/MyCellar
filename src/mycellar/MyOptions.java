package mycellar;

import mycellar.core.LabelType;
import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarCheckBox;
import mycellar.core.MyCellarLabel;
import mycellar.core.MyCellarRadioButton;
import mycellar.core.MyCellarSpinner;
import mycellar.core.MyLinkedHashMap;
import net.miginfocom.swing.MigLayout;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collections;
import java.util.List;

import static mycellar.Program.toCleanString;


/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2003</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 2.5
 * @since 30/12/20
 */
class MyOptions extends JDialog {
  @SuppressWarnings("deprecation")
  private final MyCellarLabel textControl3 = new MyCellarLabel();
  private final ButtonGroup cbg = new ButtonGroup();
  private static final int LARGEUR = 420;
  private JComponent[] value;
  private JTextField[] labelEdit;
  private final List<String> cle;
  private int taille_value = 0;
  private final MyLinkedHashMap config;
  private String[] resul;
  private final boolean bCancel;
  private boolean bIsLabelEdit = false;

  /**
   * MyOptions: Constructeur pour la fenêtre d'option
   *
   * @param title String: Titre de la fenêtre.
   * @param message String: Message de la fenêtre.
   * @param message2 String: Message de la fenêtre.
   * @param propriete : Propriété à renseigner.
   * @param default_value : Valeur par défaut.
   * @param cle2 : Clé de la propriété.
   * @param type_objet : Type des objets à ajouter.
   * @param config1 MyLinkedHashMap
   */
  MyOptions(String title, String message, String message2, List<String> propriete, List<String> default_value, List<String> cle2, List<String> type_objet,
            MyLinkedHashMap config1, boolean cancel) {

    super(Start.getInstance(), "", true);
    config = config1;
    cle = cle2;
    bCancel = cancel;
    try {
      jbInit(title, message, message2, propriete, default_value, type_objet);
    }
    catch (Exception e) {
      Program.showException(e);
    }
  }

  /**
   * MyOptions: Constructeur pour la fenêtre d'option
   *
   * @param title String: Titre de la fenêtre.
   * @param message String: Message de la fenêtre.
   * @param type_objet : Type des objets à ajouter.
   * @param erreur String: texte de l'erreur
   * @param config1 MyLinkedHashMap
   * @param cancel boolean
   * @param isLabelEdit boolean 
   */
  MyOptions(String title, String message, List<String> type_objet, String erreur,
            MyLinkedHashMap config1, boolean cancel, boolean isLabelEdit) {

    super(Start.getInstance(), "", true);
    config = config1;
    cle = Collections.singletonList("");
    bCancel = cancel;
    bIsLabelEdit = isLabelEdit;
    textControl3.setText(erreur);
    try {
      jbInit(title, message, "", Collections.singletonList(""), Collections.singletonList(""), type_objet);
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
   * @param message2 String: Message de la fenêtre.
   * @param propriete : Propriété à renseigner.
   * @param default_value : Valeur par défaut.
   * @param type_objet : Type des objets à ajouter.
   * @throws Exception
   */
  private void jbInit(String title, String message, String message2, List<String> propriete, List<String> default_value, List<String> type_objet) {

    taille_value = propriete.size();
    MyCellarLabel[] label_value = new MyCellarLabel[taille_value];
    resul = new String[taille_value];
    value = new JComponent[taille_value];
    labelEdit = new JTextField[taille_value];
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setTitle(title);
    MyCellarLabel textControl1 = new MyCellarLabel(title);
    textControl1.setFont(Program.FONT_DIALOG_SMALL);
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
    valider.setMnemonic('O');

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
        boolean bool = false;
        if (default_value.get(i).equals("true")) {
          bool = true;
        }
        value[i] = new MyCellarCheckBox("", bool);
      }
      if (type_objet.get(i).equals("MyCellarRadioButton")) {
        boolean bool = false;
        if (default_value.get(i).equals("true")) {
          bool = true;
        }
        value[i] = new MyCellarRadioButton("", bool);
        value[i].setEnabled(false);
        if (i > 0) {
          if (type_objet.get(i - 1).equals("MyCellarRadioButton") && cle.get(i - 1).equals(cle.get(i))) {
            cbg.add( (MyCellarRadioButton) value[i - 1]);
            cbg.add( (MyCellarRadioButton) value[i]);
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
      public void keyReleased(KeyEvent e) {}
      @Override
      public void keyPressed(KeyEvent e) {
        keylistener_actionPerformed(e);
      }
      @Override
      public void keyTyped(KeyEvent e) {}
    });

    int hauteur = 200 + (taille_value * 25);
    setSize(LARGEUR, hauteur);
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    setLocation( (screenSize.width - LARGEUR) / 2, (screenSize.height - hauteur) / 2);
    getContentPane().setLayout(new MigLayout("","[grow][grow]","[]15px[][]15px[]"));
    getContentPane().add(textControl1, "center, grow, span 2, wrap");
    getContentPane().add(definition, "span 2, wrap");
    getContentPane().add(definition2, "span 2, wrap");
    for (int i = 0; i < taille_value; i++) {
      if (type_objet.get(i).equals("MyCellarLabel")) {
        getContentPane().add(label_value[i], "wrap");
      }
      else {
        if(bIsLabelEdit)
          getContentPane().add(labelEdit[i], "grow");
        else
          getContentPane().add(label_value[i], "grow");
        getContentPane().add(value[i], "grow, wrap, gapleft 15px");
      }
    }
    getContentPane().add(textControl3, "wrap");

    if (bCancel) {
      getContentPane().add(valider, "span 2, split 2, center");
      getContentPane().add(annuler, "");
    } else {
      getContentPane().add(valider, "span 2, center");
    }
    setResizable(false);
  }

  /**
   * valider_actionPerformed: Valider la propriété et quitter.
   *
   * @param e ActionEvent
   */
  private void valider_actionPerformed(ActionEvent e) {
    try {
      String defaut = null;
      int nb_jradio = 0;
      for (int i = 0; i < taille_value; i++) {
        if(bIsLabelEdit) {
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
    } catch (Exception ex) {
      Program.showException(ex);
    }
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

}
