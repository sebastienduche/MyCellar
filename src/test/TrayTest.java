package test;

import mycellar.MyCellarImage;

import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.util.Timer;
import java.util.TimerTask;

public class TrayTest {

  private final TrayIcon trayIcon;
  private boolean run = false;

  public TrayTest() {
    trayIcon = new TrayIcon(MyCellarImage.ADD.getImage());

  }

  public static void main(String[] args) throws AWTException {

    TrayTest test = new TrayTest();
    SystemTray.getSystemTray().add(test.getTrayIcon());
    test.displayMessage("", "Bienvenue");
  }

  TrayIcon getTrayIcon() {
    return trayIcon;
  }

  public void displayMessage(String title, String message) {

    Timer tim = new Timer();

    tim.schedule(
        new TimerTask() {
          @Override
          public void run() {
            if (run) {
              tim.cancel();
              SystemTray.getSystemTray().remove(trayIcon);
              return;
            }
            trayIcon.displayMessage(
                title,
                message,
                MessageType.INFO
            );
            run = true;
          }
        },
        0,
        5_000
    );
  }

}
