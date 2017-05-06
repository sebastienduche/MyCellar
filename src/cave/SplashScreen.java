package Cave;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;


/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.6
 * @since 13/11/16
 */

public class SplashScreen extends JPanel {
	private static final long serialVersionUID = -5527379907989703970L;
	private BufferedImage image;
	private int w; // largeur de l'image
	private int h; // hauteur de l'image
	private Frame f;
	private int resul = 0;
	private Timer timer;
	
	/**
	 * SplashScreen: Contructeur par défaut pour l'écran SplashScreen
	 */
	public SplashScreen() {
		URL stream = getClass().getClassLoader().getResource("resources/SebInformatique.jpg");
		try {
			image = ImageIO.read(stream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		w = image.getWidth();
		h = image.getHeight();
		f = new Frame("SebInformatique");
		f.add(this);
		f.setUndecorated(true);
		f.setSize(getLargeur(), getHauteur());
		java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		f.setLocation( (screenSize.width - getLargeur()) / 2, (screenSize.height - getHauteur()) / 2);
		f.setVisible(true);
		
		timer = new Timer(1000, (e) -> {
			resul++;
			if (resul == 2) {
				timer.stop();
				quitter();
			}
		});
		timer.start();
	}

	/**
	 * paintComponent: Fonction d'affichage de l'image.
	 *
	 * @param g Graphics
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(image, 0, 0, w, h, null);
	}

	/**
	 * getHauteur: Hauteur de l'image.
	 *
	 * @return int
	 */
	public int getHauteur() {
		return h;
	}

	/**
	 * getLargeur: Largeur de l'image.
	 *
	 * @return int
	 */
	public int getLargeur() {
		return w;
	}

	/**
	 * quitter: Fonction pour fermer l'écran.
	 */
	public void quitter() {
		f.dispose();
	}
	
	public boolean isRunning() {
		return timer.isRunning();
	}
}
