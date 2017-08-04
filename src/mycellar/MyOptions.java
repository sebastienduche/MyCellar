package mycellar;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextField;

import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarCheckBox;
import mycellar.core.MyCellarLabel;
import mycellar.core.MyCellarRadioButton;
import mycellar.core.MyCellarSpinner;
import net.miginfocom.swing.MigLayout;


/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2003</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 1.6
 * @since 04/08/17
 */
public class MyOptions extends JDialog {
  private MyCellarLabel textControl1 = new MyCellarLabel();
  private MyCellarLabel label_value[];
  private MyCellarLabel definition = new MyCellarLabel();
  private MyCellarLabel definition2 = new MyCellarLabel();
  private MyCellarButton valider = new MyCellarButton();
  private MyCellarButton annuler = new MyCellarButton();
  private MyCellarLabel textControl3 = new MyCellarLabel();
  private ButtonGroup cbg = new ButtonGroup();
  private int LARGEUR = 420;
  private int HAUTEUR = 200;
  private JComponent[] value;
  private JTextField[] labelEdit;
  private String cle[];
  private int taille_value = 0;
  private MyLinkedHashMap config;
  private String resul[];
  private boolean bCancel = false;
  private boolean bIsLabelEdit = false;
  static final long serialVersionUID = 030107;

  /**
   * MyOptions: Constructeur pour la fenêtre d'option
   *
   * @param title String: Titre de la fenêtre.
   * @param message String: Message de la fenêtre.
   * @param message2 String: Message de la fenêtre.
   * @param propriete String[]: Propriété à renseigner.
   * @param default_value String[]: Valeur par défaut.
   * @param cle2 String[]: Clé de la propriété.
   * @param type_objet String[]: Type des objets à ajouter.
   * @param config1 MyLinkedHashMap
   */
  public MyOptions(String title, String message, String message2, String[] propriete, String[] default_value, String[] cle2, String[] type_objet,
                   MyLinkedHashMap config1, boolean cancel) {

    super(new JFrame(), "", true);
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
   * @param message2 String: Message de la fenêtre.
   * @param propriete String[]: Propriété à renseigner.
   * @param default_value String[]: Valeur par défaut.
   * @param cle2 String[]: Clé de la propiété.
   * @param type_objet String[]: Type des objets à ajouter.
   * @param erreur String: texte de l'erreur
   * @param config1 MyLinkedHashMap
   * @param cancel boolean
   * @param isLabelEdit boolean 
   */
  public MyOptions(String title, String message, String message2, String[] propriete, String[] default_value, String[] cle2, String[] type_objet, String erreur,
                   MyLinkedHashMap config1, boolean cancel, boolean isLabelEdit) {

    super(new JFrame(), "", true);
    config = config1;
    cle = cle2;
    bCancel = cancel;
    bIsLabelEdit = isLabelEdit;
    textControl3.setText(erreur);
    try {
      jbInit(title, message, message2, propriete, default_value, type_objet);
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
   * @param propriete String: Propriété à renseigner.
   * @param default_value String: Valeur par défaut.
   * @param type_objet String: Type des objets à ajouter.
   * @throws Exception
   */
  private void jbInit(String title, String message, String message2, String[] propriete, String[] default_value, String[] type_objet) throws Exception {

    taille_value = propriete.length;
    label_value = new MyCellarLabel[taille_value];
    resul = new String[taille_value];
    value = new JComponent[taille_value];
    labelEdit = new JTextField[taille_value];
    this.setDefaultCloseOperation(0);
    this.setTitle(title);
    textControl1.setFont(Program.font_dialog_small);
    textControl1.setForeground(Color.red);
    textControl1.setText(title);
    textControl1.setHorizontalAlignment(0);
    definition.setText(message);
    definition2.setText(message2);
    textControl3.setForeground(Color.red);
    textControl3.setHorizontalAlignment(0);
    valider.setText(Program.getLabel("Main.OK"));
    valider.setMnemonic('O');
    annuler.setText(Program.getLabel("Infos055"));
    
    for (int i = 0; i < taille_value; i++) {
      value[i] = null;
      labelEdit[i] = new JTextField();
      if (type_objet[i].equals("JTextField")) {
        value[i] = new JTextField(default_value[i]);
      }
      if (type_objet[i].equals("MyCellarSpinner")) {
        final MyCellarSpinner jspi = new MyCellarSpinner();
        jspi.setValue(new Integer(default_value[i]));
        jspi.addChangeListener(new javax.swing.event.ChangeListener() {
          public void stateChanged(javax.swing.event.ChangeEvent e) {
            if (Integer.parseInt(jspi.getValue().toString()) < 0) {
              jspi.setValue(new Integer(0));
            }
          }
        });
        value[i] = jspi;
      }
      if (type_objet[i].equals("MyCellarCheckBox")) {
        boolean bool = false;
        if (default_value[i].equals("true")) {
          bool = true;
        }
        value[i] = new MyCellarCheckBox("", bool);
      }
      if (type_objet[i].equals("MyCellarRadioButton")) {
        boolean bool = false;
        if (default_value[i].equals("true")) {
          bool = true;
        }
        value[i] = new MyCellarRadioButton("", bool);
        value[i].setEnabled(false);
        if (i > 0) {
          if (type_objet[i - 1].equals("MyCellarRadioButton") && cle[i - 1].equals(cle[i])) {
            cbg.add( (MyCellarRadioButton) value[i - 1]);
            cbg.add( (MyCellarRadioButton) value[i]);
            value[i - 1].setEnabled(true);
            value[i].setEnabled(true);
          }
        }
      }
      label_value[i] = new MyCellarLabel(propriete[i]);
      if (type_objet[i].equals("MyCellarLabel")) {
        label_value[i].setForeground(Color.red);
      }
    }
    valider.addActionListener((e) -> valider_actionPerformed(e));
    annuler.addActionListener((e) -> annuler_actionPerformed(e));

    this.addKeyListener(new java.awt.event.KeyListener() {
      public void keyReleased(java.awt.event.KeyEvent e) {}

      public void keyPressed(java.awt.event.KeyEvent e) {
        keylistener_actionPerformed(e);
      }

      public void keyTyped(java.awt.event.KeyEvent e) {}
    });

    HAUTEUR = 200 + (taille_value * 25);
    this.setSize(LARGEUR, HAUTEUR);
    java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
    this.setLocation( (screenSize.width - LARGEUR) / 2, (screenSize.height - HAUTEUR) / 2);
    this.getContentPane().setLayout(new MigLayout("","[grow][grow]","[]15px[][]15px[]"));
    this.getContentPane().add(textControl1, "center, grow, span 2, wrap");
    this.getContentPane().add(definition, "span 2, wrap");
    this.getContentPane().add(definition2, "span 2, wrap");
    for (int i = 0; i < taille_value; i++) {
      if (type_objet[i].equals("MyCellarLabel")) {
        this.getContentPane().add(label_value[i], "wrap");
      }
      else {
    	if( bIsLabelEdit )
    		this.getContentPane().add(labelEdit[i], "grow");
    	else
    		this.getContentPane().add(label_value[i], "grow");
        this.getContentPane().add(value[i], "grow, wrap, gapleft 15px");
      }
    }
    this.getContentPane().add(textControl3, "wrap");
    
    if ( bCancel )
    {
      this.getContentPane().add(valider, "span 2, split 2, center");
      this.getContentPane().add(annuler, "");
    }
    else
    {
      this.getContentPane().add(valider, "span 2, center");
    }
    this.setResizable(false);
  }

  /**
  * annuler_actionPerformed: Quitter.
  *
  * @param e ActionEvent
  */
 void annuler_actionPerformed(ActionEvent e) {
   this.dispose();
 }

  /**
   * valider_actionPerformed: Valider la propriété et quitter.
   *
   * @param e ActionEvent
   */
  void valider_actionPerformed(ActionEvent e) {
    try {
      String defaut = null;
      int nb_jradio = 0;
      for (int i = 0; i < taille_value; i++) {
    	  if( bIsLabelEdit )
    	  {
    		  JTextField jtf = (JTextField) labelEdit[i];
    		  cle[i] = jtf.getText().trim();
    	  }
        if (value[i] instanceof JTextField) {
          JTextField jtex = (JTextField) value[i];
          resul[i] = jtex.getText().trim();
          if (config != null && !cle[i].equals("")){
            config.put(cle[i], resul[i]);
          }
          if (defaut == null) {
            defaut = jtex.getText().trim();
          }
        }
        if (value[i] instanceof MyCellarSpinner) {
          MyCellarSpinner jspi = (MyCellarSpinner) value[i];
          resul[i] = jspi.getValue().toString();
          if (config != null && !cle[i].equals("")){
            config.put(cle[i], resul[i]);
          }
        }
        if (value[i] instanceof MyCellarCheckBox) {
          MyCellarCheckBox jchk = (MyCellarCheckBox) value[i];
          if (jchk.isSelected()) {
            resul[i] = defaut;
            if (config != null && !cle[i].equals("")){
              config.put(cle[i], defaut);
            }
          }
        }
        if (value[i] instanceof MyCellarRadioButton) {
          MyCellarRadioButton jrb = (MyCellarRadioButton) value[i];
          if (jrb.isSelected()) {
            resul[i] = Integer.toString(nb_jradio);
            if (config != null && !cle[i].equals("")){
              config.put(cle[i], resul[i]);
            }
          }
          nb_jradio++;
        }
        else {
          nb_jradio = 0;
        }
      }
      this.dispose();
    }
    catch (Exception exc) {
      Program.showException(exc);
    }
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
   * getResul
   *
   * @return String[]
   */
  public String[] getResul() {
    return resul;
  }

}
