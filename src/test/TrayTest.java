package test;

import mycellar.MyCellarImage;

import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.util.Timer;
import java.util.TimerTask;

public class TrayTest {

	private boolean run = false;
	private final TrayIcon trayIcon;

	public TrayTest() {
		trayIcon = new TrayIcon(MyCellarImage.ADD.getImage());

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
						if(run) {
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

	public static void main(String[] args) throws AWTException {

		TrayTest test = new TrayTest();
		SystemTray.getSystemTray().add(test.getTrayIcon());
		test.displayMessage("", "Bienvenue");
	}

}
