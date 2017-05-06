package Test;

import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.util.Timer;
import java.util.TimerTask;

import Cave.MyCellarImage;

public class TrayTest {
	
	private boolean run = false;
	private TrayIcon icon;
	
	public TrayTest() {
		icon = new TrayIcon(MyCellarImage.ADD.getImage());
		
	}
	
	TrayIcon getTrayIcon() {
		return icon;
	}
	
	public void displayMessage(String title, String message) {
		
		Timer tim = new Timer();
		
		tim.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        	if(run) {
                        		tim.cancel();
                        		SystemTray.getSystemTray().remove(icon);
                        		return;
                        	}
                            icon.displayMessage(
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
