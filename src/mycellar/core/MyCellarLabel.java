package mycellar.core;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.awt.Font;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Titre : Cave à vin
 * Description : Votre description
 * Copyright : Copyright (c) 2011
 * Société : Seb Informatique
 *
 * @author Sébastien Duché
 * @version 0.7
 * @since 20/05/21
 */

public class MyCellarLabel extends JLabel implements IMyCellarComponent {

	private static final long serialVersionUID = 4972622436840497820L;
	private static final Font FONT = new Font("Arial", Font.PLAIN, 12);

	private LabelType type;
	private String code;
	private String value;
	private LabelProperty labelProperty;

	@Deprecated
	public MyCellarLabel() {
		setFont(FONT);
	}

	public MyCellarLabel(String text) {
		super(text);
		setFont(FONT);
	}

	public MyCellarLabel(LabelType type, String code) {
		this.type = type;
		this.code = code;
		updateText();
		MyCellarLabelManagement.add(this);
		setFont(FONT);
	}

	public MyCellarLabel(LabelType type, String code, LabelProperty labelProperty) {
		this.type = type;
		this.code = code;
		this.labelProperty = labelProperty;
		updateText();
		MyCellarLabelManagement.add(this);
		setFont(FONT);
	}

	public MyCellarLabel(LabelType type, String code, String value) {
		this.type = type;
		this.code = code;
		this.value = value;
		updateText();
		MyCellarLabelManagement.add(this);
		setFont(FONT);
	}

	public MyCellarLabel(Icon image) {
		super(image);
		setFont(FONT);
	}

	@Deprecated
	public MyCellarLabel(String text, int horizontalAlignment) {
		super(text, horizontalAlignment);
		setFont(FONT);
	}

	public MyCellarLabel(Icon image, int horizontalAlignment) {
		super(image, horizontalAlignment);
		setFont(FONT);
	}

	@Deprecated
	public MyCellarLabel(String text, Icon icon, int horizontalAlignment) {
		super(text, icon, horizontalAlignment);
		setFont(FONT);
	}

	@Override
	public void updateText() {
		MyCellarLabelManagement.updateText(this, type, code, value, labelProperty);
	}

	public void setText(String text, boolean autoHide) {
		super.setText(text);
		if (autoHide) {
			new Timer().schedule(
					new TimerTask() {
						@Override
						public void run() {
							SwingUtilities.invokeLater(() -> setText(""));
						}
					},
					5000
			);
		}
	}
}
