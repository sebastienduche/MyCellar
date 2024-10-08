package test;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.SplashScreen;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public final class SplashDemo extends Frame implements ActionListener {
  private static final WindowListener CLOSE_WINDOW = new WindowAdapter() {
    @Override
    public void windowClosing(WindowEvent e) {
      e.getWindow().dispose();
    }
  };

  public SplashDemo() {
    super("SplashScreen demo");
    setSize(300, 200);
    setLayout(new BorderLayout());
    Menu m1 = new Menu("File");
    MenuItem mi1 = new MenuItem("Exit");
    m1.add(mi1);
    mi1.addActionListener(this);
    addWindowListener(CLOSE_WINDOW);

    MenuBar mb = new MenuBar();
    setMenuBar(mb);
    mb.add(m1);
    final SplashScreen splash = SplashScreen.getSplashScreen();
    if (splash == null) {
      System.out.println("SplashScreen.getSplashScreen() returned null");
      return;
    }
    Graphics2D g = splash.createGraphics();
    if (g == null) {
      System.out.println("g is null");
      return;
    }
    for (int i = 0; i < 100; i++) {
      renderSplashFrame(g, i);
      splash.update();
      try {
        Thread.sleep(90);
      } catch (InterruptedException ignored) {
      }
    }
    splash.close();
    setVisible(true);
    toFront();
  }

  static void renderSplashFrame(Graphics2D g, int frame) {
    g.setComposite(AlphaComposite.Clear);
    g.fillRect(120, 140, 200, 40);
    g.setPaintMode();
    g.setColor(Color.BLACK);
    final String[] comps = {"foo", "bar", "baz"};
    g.drawString("Loading " + comps[(frame / 5) % 3] + "...", 120, 150);
  }

  public static void main(String[] args) {
    new SplashDemo();
  }

  @Override
  public void actionPerformed(ActionEvent ae) {
    System.exit(0);
  }
}
