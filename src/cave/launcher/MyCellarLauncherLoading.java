package Cave.launcher;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;


/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2003</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.1
 * @since 02/03/14
 */
public class MyCellarLauncherLoading extends JDialog {

	private static final long serialVersionUID = -2314335792718752038L;
	private JLabel label;
	private JProgressBar jProgressBar1 = new JProgressBar();
	private GridBagLayout gridBagLayout1 = new GridBagLayout();

	/**
	 * Loading: Constructeur avec texte de la fenêtre.
	 *
	 * @param txt String
	 * @throws Exception 
	 */
	public MyCellarLauncherLoading(String txt) throws Exception {
		super(new JFrame(), "", false);
		jbInit(txt);
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
		this.setTitle("Loading...");
		this.setLocation( (screenSize.width - 270) / 2, (screenSize.height - 75) / 2);
		label = new JLabel(txt, JLabel.CENTER);
		jProgressBar1.setMinimum(0);
		jProgressBar1.setMaximum(100);
		this.getContentPane().setLayout(gridBagLayout1);
		this.getContentPane().add(label, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 20));
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

		label.setText(txt);
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
