package Cave;

import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;

import Cave.core.MyCellarButton;
import Cave.core.MyCellarLabel;
import Cave.countries.Countries;
import Cave.countries.Country;
import net.miginfocom.swing.MigLayout;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 1.6
 * @since 13/11/16
 */

public class ShowMoreHistory extends JDialog {
	private MyCellarLabel MyCellarLabel1 = new MyCellarLabel();
	private MyCellarLabel m_oBottleType = new MyCellarLabel();
	private MyCellarLabel MyCellarLabel = new MyCellarLabel();
	private MyCellarLabel m_oBottleName = new MyCellarLabel();
	private MyCellarLabel MyCellarLabel2 = new MyCellarLabel();
	private MyCellarLabel m_oBottleYear = new MyCellarLabel();
	private JPanel jPanel1 = new JPanel();
	private MyCellarLabel MyCellarLabel3 = new MyCellarLabel();
	private MyCellarLabel MyCellarLabel4 = new MyCellarLabel();
	private MyCellarLabel MyCellarLabel5 = new MyCellarLabel();
	private MyCellarLabel MyCellarLabel6 = new MyCellarLabel();
	private MyCellarLabel m_oBottlePlace = new MyCellarLabel();
	private MyCellarLabel m_oBottleNumPlace = new MyCellarLabel();
	private MyCellarLabel m_oBottleLine = new MyCellarLabel();
	private MyCellarLabel m_oBottleColumn = new MyCellarLabel();
	private MyCellarLabel MyCellarLabel7 = new MyCellarLabel();
	private MyCellarLabel m_oBottlePrice = new MyCellarLabel();
	private MyCellarLabel MyCellarLabel9 = new MyCellarLabel();
	private JTextArea m_oBottleComment = new JTextArea();
	private MyCellarLabel MyCellarLabelMaturity = new MyCellarLabel();
	private MyCellarLabel m_oBottleMaturity = new MyCellarLabel();
	private MyCellarLabel MyCellarLabelParker = new MyCellarLabel();
	private MyCellarLabel m_oBottleParker = new MyCellarLabel();
	private MyCellarLabel MyCellarLabelAppellation = new MyCellarLabel();
	private MyCellarLabel m_oBottleAppellation = new MyCellarLabel();
	private MyCellarLabel labelCountry = new MyCellarLabel();
	private MyCellarLabel labelBottleCountry = new MyCellarLabel();
	private MyCellarLabel labelVignoble = new MyCellarLabel();
	private MyCellarLabel labelBottleVignoble = new MyCellarLabel();
	private MyCellarLabel labelAppelationAOC = new MyCellarLabel();
	private MyCellarLabel labelBottleAOC = new MyCellarLabel();
	private MyCellarLabel labelAppelationIGP = new MyCellarLabel();
	private MyCellarLabel labelBottleIGP = new MyCellarLabel();
	private MyCellarButton m_oValidate = new MyCellarButton();
	private static final long serialVersionUID = 060107;

	public ShowMoreHistory( Bouteille _bottle ) {
		super(new JFrame(),true);
		Debug("Constructor");
		try {
			jbInit( _bottle );
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	private void jbInit(Bouteille _bottle) throws Exception {
		setLayout(new MigLayout("","",""));
		MyCellarLabel1.setText(Program.getLabel("Infos132") + ":");
		m_oBottleName.setText(_bottle.getNom());
		MyCellarLabel.setText(Program.getLabel("Infos134") + ":");
		m_oBottleType.setText(_bottle.getType());
		MyCellarLabel2.setText(Program.getLabel("Infos133")+":");
		m_oBottleYear.setText(_bottle.getAnnee());
		labelCountry.setText(Program.getLabel("Main.Country") +":");
		Vignoble vignoble = _bottle.getVignoble();
		Country country = null;
		if(vignoble != null)
			country = Countries.find(vignoble.getCountry());
		if(country != null)
			labelBottleCountry.setText(country.getLabel());
		labelVignoble.setText(Program.getLabel("Main.Vignoble")+":");
		labelAppelationAOC.setText(Program.getLabel("Main.AppelationAOC")+":");
		labelAppelationIGP.setText(Program.getLabel("Main.AppelationIGP")+":");
		if(vignoble != null) {
			labelBottleVignoble.setText(vignoble.getName());
			labelBottleAOC.setText(vignoble.getAOC());
			labelBottleIGP.setText(vignoble.getIGP());
		}
		jPanel1.setBorder(BorderFactory.createEtchedBorder());
		jPanel1.setLayout(new MigLayout("","[][][][]",""));
		jPanel1.setFont(Program.font_panel);
		jPanel1.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED),"",0,0,Program.font_panel), BorderFactory.createEmptyBorder()));
		MyCellarLabel3.setText(Program.getLabel("Infos105"));
		MyCellarLabel4.setText(Program.getLabel("Infos139"));
		MyCellarLabel5.setText(Program.getLabel("Infos028"));
		MyCellarLabel6.setText(Program.getLabel("Infos083"));
		m_oBottlePlace.setText(_bottle.getEmplacement());
		m_oBottleNumPlace.setText(Integer.toString(_bottle.getNumLieu()));
		m_oBottleLine.setText(Integer.toString(_bottle.getLigne()));
		m_oBottleColumn.setText(Integer.toString(_bottle.getColonne()));
		MyCellarLabel7.setText(Program.getLabel("Infos135")+":");
		m_oBottlePrice.setText(_bottle.getPrix() + " " + Program.getCaveConfigString("DEVISE", "€") );
		MyCellarLabel9.setText(Program.getLabel("Infos137")+":");
		m_oBottleComment.setText(_bottle.getComment());
		m_oBottleComment.setEditable(false);
		m_oBottleComment.setLineWrap(true);
		m_oBottleComment.setWrapStyleWord(true);
		MyCellarLabelMaturity.setText(Program.getLabel("Infos391")+":");
		m_oBottleMaturity.setText(_bottle.getMaturity());
		MyCellarLabelParker.setText(Program.getLabel("Infos392")+":");
		m_oBottleParker.setText(_bottle.getParker());
		MyCellarLabelAppellation.setText(Program.getLabel("Infos393")+":");
		m_oBottleAppellation.setText(_bottle.getAppellation());
		m_oValidate.setText(Program.getLabel("Infos019"));
		m_oValidate.addActionListener((e) -> cancel_actionPerformed(e));
		
		int nCave = Rangement.convertNom_Int(_bottle.getEmplacement());
		Rangement cave = Program.getCave(nCave);
		if (cave != null && cave.isCaisse())
		{
			MyCellarLabel5.setVisible(false);
			MyCellarLabel6.setVisible(false);
			m_oBottleLine.setVisible(false);
			m_oBottleColumn.setVisible(false);
		}

		this.setTitle(Program.getLabel("Infos361"));
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout("","[][]",""));
		panel.add(MyCellarLabel1);
		panel.add(m_oBottleName, "wrap");
		panel.add(MyCellarLabel2);
		panel.add(m_oBottleYear, "wrap");
		panel.add(MyCellarLabel);
		panel.add(m_oBottleType, "wrap");
		panel.add(MyCellarLabelMaturity);
		panel.add(m_oBottleMaturity);
		add(panel, "wrap");
		jPanel1.add(MyCellarLabel3, "gapright 10px");
		jPanel1.add(MyCellarLabel4, "gapright 10px");
		jPanel1.add(MyCellarLabel5, "gapright 10px");
		jPanel1.add(MyCellarLabel6, "wrap");
		jPanel1.add(m_oBottlePlace);
		jPanel1.add(m_oBottleNumPlace);
		jPanel1.add(m_oBottleLine);
		jPanel1.add(m_oBottleColumn);
		add(jPanel1, "wrap");
		JScrollPane scroll = new JScrollPane(m_oBottleComment);
		panel = new JPanel();
		panel.setLayout(new MigLayout("","[][]",""));
		panel.add(MyCellarLabel7);
		panel.add(m_oBottlePrice, "wrap");
		panel.add(MyCellarLabel9);
		panel.add(scroll, "w 200:200, wrap");
		panel.add(MyCellarLabelParker);
		panel.add(m_oBottleParker, "wrap");
		panel.add(MyCellarLabelAppellation);
		panel.add(m_oBottleAppellation, "wrap");
		panel.add(labelCountry);
		panel.add(labelBottleCountry, "wrap");
		panel.add(labelVignoble);
		panel.add(labelBottleVignoble, "wrap");
		panel.add(labelAppelationAOC);
		panel.add(labelBottleAOC, "wrap");
		panel.add(labelAppelationIGP);
		panel.add(labelBottleIGP);
		add(panel, "wrap");
		add(m_oValidate, "center");

		this.pack();
		setLocationRelativeTo(null);
		this.setVisible(true);
		this.toFront();
	}

	/**
	 * cancel_actionPerformed
	 *
	 * @param e ActionEvent
	 */
	void cancel_actionPerformed(ActionEvent e) {
		Debug("Closing");
		this.dispose();
		Program.Morehistory = null;
	}

	/**
	 * Debug
	 *
	 * @param sText String
	 */
	public static void Debug(String sText) {
		Program.Debug("ShowMoreHistory: " + sText);
	}


}
