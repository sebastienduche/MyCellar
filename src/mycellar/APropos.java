package mycellar;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarLabel;
import net.miginfocom.swing.MigLayout;


/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 1.1
 * @since 13/11/16
 */
public class APropos extends JDialog {
  private MyCellarButton ok = new MyCellarButton();
  private MyCellarLabel MyCellarLabel1 = new MyCellarLabel();
  private MyCellarLabel MyCellarLabel2 = new MyCellarLabel();
  private MyCellarLabel MyCellarLabel3 = new MyCellarLabel();
  private MyCellarLabel MyCellarLabel4 = new MyCellarLabel();
  public static String sVersion = "2.5.0.4";
  public static String sMainVersion = "4.8 CO";
  static final long serialVersionUID = 150505;

  /**
   * APropos: Constructeur pour la fenêtre d'A Propos.
   *
   * @param frame Frame
   */
  public APropos() {
    super(new JFrame(), Program.getLabel("Infos198"), true);
    try {
      jbInit();
      pack();
      java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
      this.setLocation( (screenSize.width - this.getSize().width) / 2, (screenSize.height - this.getSize().height) / 2);
      
    }
    catch (Exception ex) {
      Program.showException(ex);
    }
  }

  /**
   * jbInit: Fonction d'initialisation de la fenètre.
   *
   * @throws Exception
   */
  private void jbInit() throws Exception {
    ok.setText("OK");
    IconPanel ip = new IconPanel(MyCellarImage.ICON.getImage());
    ok.setFont(new Font("Arial", 0, 12));
    ok.addActionListener((e) -> ok_actionPerformed(e));
    MyCellarLabel1.setFont(new java.awt.Font("Dialog", 1, 13));
    MyCellarLabel1.setForeground(Color.red);
    MyCellarLabel1.setHorizontalAlignment(SwingConstants.CENTER);
    MyCellarLabel1.setText("MyCellar");
    MyCellarLabel2.setHorizontalAlignment(SwingConstants.LEFT);
    MyCellarLabel2.setText("Copyright: S.Duché");
    MyCellarLabel3.setHorizontalAlignment(SwingConstants.LEFT);
    MyCellarLabel3.setText("Release: " + sVersion);
    MyCellarLabel4.setHorizontalAlignment(SwingConstants.LEFT);
    MyCellarLabel4.setText("Version: " + sMainVersion);
    MyCellarLabel2.setFont(new java.awt.Font("Dialog", 0, 11));
    MyCellarLabel3.setFont(new java.awt.Font("Dialog", 0, 11));
    MyCellarLabel4.setFont(new java.awt.Font("Dialog", 0, 11));
    this.setLayout(new MigLayout("","[][]","[]"));
    this.add(MyCellarLabel1,"center, span 2, wrap");
    this.add(MyCellarLabel2,"gaptop 20px");
    this.add(ip, "spany 3, wmin 64, hmin 64, wrap");
    this.add(MyCellarLabel3,"wrap");
    this.add(MyCellarLabel4,"wrap");
    this.add(ok,"gaptop 20px, span 2, center");
     
    this.setResizable(false);
  }

  /**
   * ok_actionPerformed: Fermeture de la fenètre.
   *
   * @param e ActionEvent
   */
  void ok_actionPerformed(ActionEvent e) {
    this.dispose();
  }

  /**
   * APropos: Constructeur de l'image.
   *
   * @param fileImg File
   */
  class IconPanel extends JPanel {
    Image img;
    static final long serialVersionUID = 1505051;

    public IconPanel(Image img) {
      this.img = img;
    }

    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      g.drawImage(img, 0, 0, 64, 64, this);
    }
  }
}
