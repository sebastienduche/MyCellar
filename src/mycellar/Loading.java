package mycellar;

import mycellar.core.MyCellarLabel;

import javax.swing.JDialog;
import javax.swing.JProgressBar;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;


/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2003</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 1.2
 * @since 12/10/18
 */
class Loading extends JDialog {
  private final JProgressBar jProgressBar1 = new JProgressBar();
  static final long serialVersionUID = 130805;
  private final MyCellarLabel MyCellarLabel1;

  /**
   * Loading: Constructeur avec texte de la fenêtre.
   *
   * @param txt String
   */
  public Loading(String txt) {
    super(Start.getInstance());
    setSize(270, 75);
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    setTitle(Program.getLabel("Infos070"));
    setLocation( (screenSize.width - 270) / 2, (screenSize.height - 75) / 2);
    MyCellarLabel1 = new MyCellarLabel(txt, MyCellarLabel.CENTER);
    jProgressBar1.setMinimum(0);
    jProgressBar1.setMaximum(100);
    getContentPane().setLayout(new GridBagLayout());
    getContentPane().add(MyCellarLabel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 20));
    getContentPane().add(jProgressBar1, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 0, 1, 0), 270, 4));
    setResizable(false);
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
      setTitle(titre);
    }
    repaint();
  }

  /**
   * setValue: Fonction pour affecter une valeur à la barre de progression
   * (0-100).
   *
   * @param i int
   */
  public void setValue(int i) {
    jProgressBar1.setValue(i);
    repaint();
  }
}
