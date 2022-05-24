package mycellar;

import mycellar.core.uicomponents.MyCellarButton;
import mycellar.core.uicomponents.MyCellarLabel;
import net.miginfocom.swing.MigLayout;

import javax.swing.ImageIcon;
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

import static mycellar.ProgramConstants.INTERNAL_VERSION;
import static mycellar.ProgramConstants.MAIN_VERSION;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;


/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 1998
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 2.0
 * @since 24/05/22
 */
final class APropos extends JDialog {
  static final long serialVersionUID = 150505;

  APropos() {
    super(new JFrame(), getLabel("Main.About"), true);
    IconPanel ip = new IconPanel(MyCellarImage.ICON);
    MyCellarButton ok = new MyCellarButton("Main.OK");
    ok.addActionListener((e) -> dispose());
    MyCellarLabel myCellarLabel1 = new MyCellarLabel("MyCellar");
    myCellarLabel1.setForeground(Color.red);
    myCellarLabel1.setHorizontalAlignment(SwingConstants.CENTER);
    MyCellarLabel myCellarLabel2 = new MyCellarLabel("Copyright: S.Duché");
    myCellarLabel2.setHorizontalAlignment(SwingConstants.LEFT);
    MyCellarLabel myCellarLabel3 = new MyCellarLabel("Release: " + INTERNAL_VERSION);
    myCellarLabel3.setHorizontalAlignment(SwingConstants.LEFT);
    MyCellarLabel myCellarLabel4 = new MyCellarLabel("Version: " + MAIN_VERSION);
    myCellarLabel4.setHorizontalAlignment(SwingConstants.LEFT);
    myCellarLabel1.setFont(new Font("Arial", Font.BOLD, 13));
    setLayout(new MigLayout("", "[][]", "[]"));
    add(myCellarLabel1, "center, span 2, wrap");
    add(myCellarLabel2, "gaptop 20px");
    add(ip, "spany 3, wmin 64, hmin 64, wrap");
    add(myCellarLabel3, "wrap");
    add(myCellarLabel4, "wrap");
    add(ok, "gaptop 20px, span 2, center");

    setResizable(false);
    pack();
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    setLocation((screenSize.width - getSize().width) / 2, (screenSize.height - getSize().height) / 2);
  }

  private static class IconPanel extends JPanel {
    static final long serialVersionUID = 1505051;
    private final ImageIcon img;

    private IconPanel(ImageIcon img) {
      this.img = img;
    }

    @Override
    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      final Image image = img.getImage();
      if (image != null) {
        g.drawImage(image, 0, 0, 64, 64, this);
      }
    }
  }
}
