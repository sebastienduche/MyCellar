package mycellar;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;


/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : Seb Informatique</p>
 *
 * @author Sébastien Duché
 * @version 0.8
 * @since 22/08/18
 */

class SplashScreen extends JPanel {
  private static final long serialVersionUID = -5527379907989703970L;
  private int resul = 0;
  private BufferedImage image = null;
  private int w, h;
  private Frame f;
  private Timer timer;

  /**
   * SplashScreen: Contructeur par défaut pour l'écran SplashScreen
   */
  public SplashScreen() {
    URL stream = getClass().getClassLoader().getResource("resources/SebInformatique.jpg");

    if (stream != null) {
      try {
        image = ImageIO.read(stream);
      } catch (IOException e) {
        Program.Debug("ERROR: Splashscreen: Unable to read image!");
        Program.Debug(e.getMessage());
      }

      if (image != null) {
        w = image.getWidth();
        h = image.getHeight();
        f = new Frame("SebInformatique");
        f.add(this);
        f.setUndecorated(true);
        f.setSize(getLargeur(), getHauteur());
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        f.setLocation((screenSize.width - getLargeur()) / 2, (screenSize.height - getHauteur()) / 2);
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
    } else {
      throw new RuntimeException("Missing resources file. Check resources dir in bin directory!");
    }
  }

  /**
   * paintComponent: Fonction d'affichage de l'image.
   *
   * @param g Graphics
   */
  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    g.drawImage(image, 0, 0, w, h, null);
  }

  /**
   * getHauteur: Hauteur de l'image.
   *
   * @return int
   */
  private int getHauteur() {
    return h;
  }

  /**
   * getLargeur: Largeur de l'image.
   *
   * @return int
   */
  private int getLargeur() {
    return w;
  }

  /**
   * quitter: Fonction pour fermer l'écran.
   */
  private void quitter() {
    f.dispose();
  }

  boolean isRunning() {
    return timer.isRunning();
  }
}
