package mycellar;

import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarLabel;
import mycellar.core.MyCellarVersion;
import net.miginfocom.swing.MigLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;


/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 1.3
 * @since 02/03/18
 */
class APropos extends JDialog {
  private MyCellarButton ok = new MyCellarButton();
  private final MyCellarLabel MyCellarLabel1 = new MyCellarLabel();
  private final MyCellarLabel MyCellarLabel2 = new MyCellarLabel();
  private final MyCellarLabel MyCellarLabel3 = new MyCellarLabel();
  private final MyCellarLabel MyCellarLabel4 = new MyCellarLabel();
  static final long serialVersionUID = 150505;

  /**
   * APropos: Constructeur pour la fenêtre d'A Propos.
   */
  APropos() {
    super(new JFrame(), Program.getLabel("Infos198"), true);
    jbInit();
    pack();
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    setLocation( (screenSize.width - getSize().width) / 2, (screenSize.height - getSize().height) / 2);
  }

  /**
   * jbInit: Fonction d'initialisation de la fenètre.
   *
   */
  private void jbInit() {
    ok.setText(Program.getLabel("Main.OK"));
    IconPanel ip = new IconPanel(MyCellarImage.ICON.getImage());
    ok.setFont(new Font("Arial", Font.PLAIN, 12));
    ok.addActionListener((e) -> dispose());
    MyCellarLabel1.setFont(new Font("Dialog", Font.BOLD, 13));
    MyCellarLabel1.setForeground(Color.red);
    MyCellarLabel1.setHorizontalAlignment(SwingConstants.CENTER);
    MyCellarLabel1.setText("MyCellar");
    MyCellarLabel2.setHorizontalAlignment(SwingConstants.LEFT);
    MyCellarLabel2.setText("Copyright: S.Duché");
    MyCellarLabel3.setHorizontalAlignment(SwingConstants.LEFT);
    MyCellarLabel3.setText("Release: " + MyCellarVersion.version);
    MyCellarLabel4.setHorizontalAlignment(SwingConstants.LEFT);
    MyCellarLabel4.setText("Version: " + MyCellarVersion.mainVersion);
    MyCellarLabel2.setFont(new Font("Dialog", Font.PLAIN, 11));
    MyCellarLabel3.setFont(new Font("Dialog", Font.PLAIN, 11));
    MyCellarLabel4.setFont(new Font("Dialog", Font.PLAIN, 11));
    setLayout(new MigLayout("","[][]","[]"));
    add(MyCellarLabel1,"center, span 2, wrap");
    add(MyCellarLabel2,"gaptop 20px");
    add(ip, "spany 3, wmin 64, hmin 64, wrap");
    add(MyCellarLabel3,"wrap");
    add(MyCellarLabel4,"wrap");
    add(ok,"gaptop 20px, span 2, center");
     
    setResizable(false);
  }

  /**
   * APropos: Constructeur de l'image.
   */
  class IconPanel extends JPanel {
    private Image img;
    static final long serialVersionUID = 1505051;

    private IconPanel(Image img) {
      this.img = img;
    }
    @Override
    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      g.drawImage(img, 0, 0, 64, 64, this);
    }
  }
}
