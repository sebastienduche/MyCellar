package mycellar;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JProgressBar;

import mycellar.core.MyCellarLabel;


/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2003</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 1.0
 * @since 12/01/14
 */
public class Loading extends JDialog {
  private MyCellarLabel MyCellarLabel1;
  private JProgressBar jProgressBar1 = new JProgressBar();
  private GridBagLayout gridBagLayout1 = new GridBagLayout();
  static final long serialVersionUID = 130805;

  /**
   * Loading: Constructeur avec texte de la fenêtre.
   *
   * @param txt String
   */
  public Loading(String txt) {
    super(new JFrame(), "", false);
    try {
      jbInit(txt);
    }
    catch (Exception e) {
      Program.showException(e);
    }
  }

  /**
   * jbInit: Fonction d'initialisation avec texte de la fenêtre.
   *
   * @param txt String
   * @throws Exception
   */
  private void jbInit(String txt) throws Exception {

    this.setSize(270, 75);
    this.setDefaultCloseOperation(0);
    java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
    this.setTitle(Program.getLabel("Infos070"));
    this.setLocation( (screenSize.width - 270) / 2, (screenSize.height - 75) / 2);
    MyCellarLabel1 = new MyCellarLabel(txt, MyCellarLabel.CENTER);
    jProgressBar1.setMinimum(0);
    jProgressBar1.setMaximum(100);
    this.getContentPane().setLayout(gridBagLayout1);
    this.getContentPane().add(MyCellarLabel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 20));
    this.getContentPane().add(jProgressBar1, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 0, 1, 0), 270, 4));
    this.setResizable(false);
  }

  /**
   * setText: Fonction pour remplacer le texte de la fenêtre.
   *
   * @param txt String
   * @param titre String
   */
  public void setText(String txt, String titre) {

    MyCellarLabel1.setText(txt);
    if (titre != null){
      this.setTitle(titre);
    }
    this.repaint();
  }

  /**
   * setValue: Fonction pour affecter une valeur à la barre de progression
   * (0-100).
   *
   * @param i int
   */
  public void setValue(int i) {

    jProgressBar1.setValue(i);
    this.repaint();
  }

  /**
   * cache: Fonction pour cacher la fenêtre.
   */
  public void cache() {

    this.dispose();
  }

  /**
   * view: Fonction pour montrer la fenêtre.
   */
  public void view() {

    this.repaint();
    this.setVisible(true);

  }

}
