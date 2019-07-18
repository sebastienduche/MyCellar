package test;

import mycellar.TextFieldPopup;
import net.miginfocom.swing.MigLayout;

import javax.swing.JFrame;
import javax.swing.JTextField;
import java.util.LinkedList;

public class Test extends JFrame {

	LinkedList<String> list = new LinkedList<String>();
	TextFieldPopup text;
	public Test() {
		list.add("a");
		list.add("aaaa");
		list.add("aa");
		list.add("aaazzz");
		text = new TextFieldPopup(list, 200) {
      @Override
      public void doAfterValidate() {

      }
    };
		setSize(400,400);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new MigLayout("","grow",""));
		getContentPane().add(text, "growx, wrap");
		getContentPane().add(new JTextField(), "grow");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}

	public static void main(String[] args) {
		new Test();
	}

}
