package mycellar;

import mycellar.core.ICutCopyPastable;
import mycellar.core.IMyCellar;
import mycellar.core.LabelProperty;
import mycellar.core.LabelType;
import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarFields;
import mycellar.core.MyCellarLabel;
import mycellar.core.MyCellarMenuItem;
import mycellar.core.MyCellarRadioButton;
import mycellar.core.MyCellarSettings;
import mycellar.core.PopupListener;
import mycellar.pdf.PDFPageProperties;
import mycellar.pdf.PDFTools;
import mycellar.placesmanagement.RangementUtils;
import mycellar.showfile.ManageColumnModel;
import mycellar.xls.XLSOptions;
import net.miginfocom.swing.MigLayout;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2004</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 * @author S&eacute;bastien Duch&eacute;
 * @version 9.5
 * @since 24/12/20
 */
public class Export extends JPanel implements ITabListener, Runnable, ICutCopyPastable, IMyCellar {

	private final MyCellarButton valider = new MyCellarButton(LabelType.INFO, "153");
	private final JTextField file = new JTextField();
	private final MyCellarButton browse = new MyCellarButton("...");
	private final MyCellarButton parameters = new MyCellarButton(LabelType.INFO_OTHER, "Main.Parameters");
	private final JProgressBar progressBar = new JProgressBar();
	private final MyCellarRadioButton MyCellarRadioButtonXML = new MyCellarRadioButton(LabelType.INFO, "210", true);
	private final MyCellarRadioButton MyCellarRadioButtonHTML = new MyCellarRadioButton(LabelType.INFO, "211", false);
	private final MyCellarRadioButton MyCellarRadioButtonCSV = new MyCellarRadioButton(LabelType.INFO, "212", false);
	private final MyCellarRadioButton MyCellarRadioButtonXLS = new MyCellarRadioButton(LabelType.INFO, "233", false);
	private final MyCellarRadioButton MyCellarRadioButtonPDF = new MyCellarRadioButton(LabelType.INFO, "248", false);
	private final MyCellarLabel end = new MyCellarLabel();
	private final MyCellarButton openit = new MyCellarButton(LabelType.INFO, "152");
	private final MyCellarButton options = new MyCellarButton(LabelType.INFO, "156", LabelProperty.SINGLE.withThreeDashes());
	private static final char OUVRIR = Program.getLabel("OUVRIR").charAt(0);
	private static final char EXPORT = Program.getLabel("EXPORT").charAt(0);
	private final JMenuItem param = new MyCellarMenuItem(LabelType.INFO, "156", LabelProperty.SINGLE.withThreeDashes());
	private final List<Bouteille> bottles;
	static final long serialVersionUID = 240706;

	/**
	 * Export: Constructeur pour l'export.
	 */
	public Export() {
			bottles = Program.getStorage().getAllList();
		try {
			initialize();
		}
		catch (RuntimeException e) {
			Program.showException(e);
		}
	}

	/**
	 * Export: Constructeur pour l'export.
	 *
	 * @param bottles LinkedList<Bouteille>: Bottles to export
	 */
	public Export(final List<Bouteille> bottles) {
		this.bottles = bottles;
		try {
			initialize();
		}
		catch (RuntimeException e) {
			Program.showException(e);
		}
	}

	/**
	 * initialize: Fonction d'initialisation.
	 */
	private void initialize() {

		MyCellarLabel nameLabel = new MyCellarLabel(LabelType.INFO, "149"); //Nom du fichier:
		end.setFont(Program.FONT_DIALOG_SMALL);
		openit.setMnemonic(OUVRIR);
		openit.addActionListener((e) -> openit_actionPerformed());
		parameters.addActionListener((e) -> param_actionPerformed());
		MyCellarRadioButtonXML.addActionListener((e) -> jradio_actionPerformed());
		MyCellarRadioButtonHTML.addActionListener((e) -> jradio_actionPerformed());
		MyCellarRadioButtonCSV.addActionListener((e) -> jradio_actionPerformed());
		end.setHorizontalAlignment(SwingConstants.CENTER);
		end.setForeground(Color.red);
		MyCellarRadioButtonXLS.addActionListener((e) -> jradio_actionPerformed());
		MyCellarRadioButtonPDF.addActionListener((e) -> jradio_actionPerformed());

		param.addActionListener((e) -> param_actionPerformed());
		file.addMouseListener(new PopupListener());

    final ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(MyCellarRadioButtonXML);
		buttonGroup.add(MyCellarRadioButtonHTML);
		buttonGroup.add(MyCellarRadioButtonCSV);
		buttonGroup.add(MyCellarRadioButtonXLS);
		buttonGroup.add(MyCellarRadioButtonPDF);

		valider.setMnemonic(EXPORT);


		valider.addActionListener((e) -> valider_actionPerformed());
		options.addActionListener((e) -> options_actionPerformed());
		browse.addActionListener((e) -> browse_actionPerformed());

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				keylistener_actionPerformed(e);
			}
		});

		setLayout(new MigLayout("","grow","[][][]push[]"));
		JPanel panelFormat = new JPanel();
		panelFormat.setLayout(new MigLayout("", "grow",""));
		panelFormat.add(MyCellarRadioButtonXML, "split 6");
		panelFormat.add(MyCellarRadioButtonHTML);
		panelFormat.add(MyCellarRadioButtonCSV);
		panelFormat.add(MyCellarRadioButtonXLS);
		panelFormat.add(MyCellarRadioButtonPDF);
		panelFormat.add(options,"w 100:100:100, push");
		panelFormat.setBorder(BorderFactory.createTitledBorder(Program.getLabel("Infos151")));
		add(panelFormat,"grow, wrap");
		JPanel panelTitle = new JPanel();
		panelTitle.setLayout(new MigLayout("", "grow",""));
		panelTitle.add(nameLabel, "split 4");
		panelTitle.add(file, "grow");
		panelTitle.add(browse);
		panelTitle.add(parameters);
		add(panelTitle,"grow, wrap");
		JPanel panelEnd = new JPanel();
		panelEnd.setLayout(new MigLayout("", "grow",""));

		panelEnd.add(end, "grow, center, hidemode 3, wrap");
		panelEnd.add(valider, "center, split 2");
		panelEnd.add(openit);
		add(panelEnd, "grow, wrap");
		add(progressBar, "grow, center, hidemode 3");
		openit.setEnabled(false);
		options.setEnabled(false);
		progressBar.setVisible(false);

		int val = Program.getCaveConfigInt(MyCellarSettings.EXPORT_DEFAULT, 0);

		MyCellarRadioButtonXML.setSelected(val == 0);
		MyCellarRadioButtonHTML.setSelected(val == 1);
		MyCellarRadioButtonCSV.setSelected(val == 2);
		MyCellarRadioButtonXLS.setSelected(val == 3);
		MyCellarRadioButtonPDF.setSelected(val == 4);
		options.setEnabled(val != 0);

		setVisible(true);
	}

	/**
	 * valider_actionPerformed: Fonction d'export.
	 */
	private void valider_actionPerformed() {
		new Thread(this).start();
	}

	/**
	 * browse_actionPerformed: Fonction pour parcourir les repertoires.
	 */
	private void browse_actionPerformed() {

		end.setText("");
		JFileChooser boiteFichier = new JFileChooser(Program.getCaveConfigString(MyCellarSettings.DIR,""));
		boiteFichier.removeChoosableFileFilter(boiteFichier.getFileFilter());
		if (MyCellarRadioButtonPDF.isSelected()) {
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_PDF);
		} else if (MyCellarRadioButtonXLS.isSelected()) {
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XLSX);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_ODS);
		} else if (MyCellarRadioButtonCSV.isSelected()) {
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_CSV);
		} else if (MyCellarRadioButtonHTML.isSelected()) {
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_HTML);
		} else if (MyCellarRadioButtonXML.isSelected()) {
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XML);
		}

		if (boiteFichier.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			File nomFichier = boiteFichier.getSelectedFile();
			Program.putCaveConfigString(MyCellarSettings.DIR, boiteFichier.getCurrentDirectory().toString());
			//Erreur utilisation de caracteres interdits
			if (MyCellarControl.controlPath(nomFichier)) {
				Filtre filtre = (Filtre) boiteFichier.getFileFilter();
				String nom = nomFichier.getAbsolutePath();
				nom = MyCellarControl.controlAndUpdateExtension(nom, filtre);
				file.setText(nom);
			}
		}
	}

	/**
	 * openit_actionPerformed: Ouvrir le fichier issu de l'export.
	 */
	private void openit_actionPerformed() {
		String nom = file.getText().strip();
		if (!nom.isEmpty()) {
			File f = new File(nom);
			if(!f.exists() || f.isDirectory()) {
				end.setText("");
				//Fichier non trouve Verifier le chemin
				Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error020"), nom), Program.getError("Error022"));
				return;
			}
			Program.open(f);
		}
	}

	/**
	 * options_actionPerformed: Appel de la fenetre d'options.
	 */
	private void options_actionPerformed() {
		end.setText("");
		if (MyCellarRadioButtonPDF.isSelected()) {
			PDFOptions ef = new PDFOptions();
			ef.setAlwaysOnTop(true);
			ef.setVisible(true);
			options.setSelected(false);
		} else if (MyCellarRadioButtonXLS.isSelected()) {
			XLSOptions xf = new XLSOptions();
			xf.setAlwaysOnTop(true);
			xf.setVisible(true);
			options.setSelected(false);
		}	else if (MyCellarRadioButtonCSV.isSelected()) {
			CSVOptions cf = new CSVOptions();
			cf.setAlwaysOnTop(true);
			cf.setVisible(true);
			options.setSelected(false);
		}	else if(MyCellarRadioButtonHTML.isSelected()) {
			List<MyCellarFields> fieldsList = MyCellarFields.getFieldsList();
			ManageColumnModel modelColumn = new ManageColumnModel(fieldsList, Program.getHTMLColumns());
			JTable table = new JTable(modelColumn);
			TableColumnModel tcm = table.getColumnModel();
			TableColumn tc = tcm.getColumn(0);
			tc.setCellRenderer(new StateRenderer());
			tc.setCellEditor(new StateEditor());
			tc.setMinWidth(25);
			tc.setMaxWidth(25);
			JPanel panel = new JPanel();
			panel.add(new JScrollPane(table));
			JOptionPane.showMessageDialog(this, panel, Program.getLabel("Main.Columns"), JOptionPane.PLAIN_MESSAGE);
			Program.setModified();
			List<Integer> properties = modelColumn.getSelectedColumns();
			List<MyCellarFields> cols = new ArrayList<>();
			for (MyCellarFields c : fieldsList) {
				if (properties.contains(c.ordinal())) {
          cols.add(c);
        }
			}
			Program.saveHTMLColumns(cols);
		}
	}

	/**
	 * keylistener_actionPerformed: Ecouteur du clavier.
	 *
	 * @param e KeyEvent
	 */
	private void keylistener_actionPerformed(KeyEvent e) {
		if (e.getKeyCode() == OUVRIR && openit.isEnabled()) {
			openit_actionPerformed();
		}	else if (e.getKeyCode() == EXPORT) {
			valider_actionPerformed();
		}	else if (e.getKeyCode() == KeyEvent.VK_F1) {
			aide_actionPerformed();
		}
	}

	/**
	 * jradio_actionPerformed: Bouton radio.
	 */
	private void jradio_actionPerformed() {
		end.setText("");
		options.setEnabled(!MyCellarRadioButtonXML.isSelected());
	}


	/**
	 * aide_actionPerformed: Aide
	 */
	private void aide_actionPerformed() {
		Program.getAide();
	}


	/**
	 * run: Execution des taches.
	 */
	@Override
	public void run() {
		try {
			valider.setEnabled(false);
			openit.setEnabled(false);
			String nom = file.getText().strip();
			end.setText(Program.getLabel("Infos250"));

			if (!MyCellarControl.controlPath(nom)) {
				end.setText("");
				valider.setEnabled(true);
				return;
			}

			File aFile = new File(nom);
			if (aFile.exists()) {
				// Existing file. replace?
				if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(Start.getInstance(), MessageFormat.format(Program.getError("Export.replaceFileQuestion"), aFile.getAbsolutePath()), Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
					end.setText("");
					valider.setEnabled(true);
					return;
				}
			}

			if (MyCellarRadioButtonXML.isSelected()) {
				if (MyCellarControl.hasInvalidExtension(nom, Collections.singletonList(Filtre.FILTRE_XML.toString()))) {
					//"Le fichier saisie ne possede pas une extension XML: " + str_tmp3);
					end.setText("");
					Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error087"), nom));
					valider.setEnabled(true);
					return;
				}

				ListeBouteille liste = new ListeBouteille();
				bottles.forEach(bouteille -> liste.getBouteille().add(bouteille));
				boolean ok = ListeBouteille.writeXML(liste, aFile);
				if (ok) {
					end.setText(Program.getLabel("Infos154")); //"Export termine."
					openit.setEnabled(true);
				}	else {
					end.setText(Program.getError("Error129")); //"Erreur lors de l'export"
				}
			}	else if (MyCellarRadioButtonHTML.isSelected()) {
				if (MyCellarControl.hasInvalidExtension(nom, Collections.singletonList(Filtre.FILTRE_HTML.toString()))) {
					//"Le fichier saisie ne possede pas une extension HTML: " + str_tmp3);
					end.setText("");
					Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error107"), nom));
					valider.setEnabled(true);
					return;
				}

				if (RangementUtils.write_HTML(aFile, bottles, Program.getHTMLColumns())) {
					end.setText(Program.getLabel("Infos154")); //"Export termine."
					Erreur.showSimpleErreur(MessageFormat.format(Program.getLabel("Main.savedFile"), aFile.getAbsolutePath()), true);
					openit.setEnabled(true);
				}	else {
					end.setText(Program.getError("Error129")); //"Erreur lors de l'export"
				}
			} else if (MyCellarRadioButtonCSV.isSelected()) {
				if (MyCellarControl.hasInvalidExtension(nom, Arrays.asList(Filtre.FILTRE_CSV.toString()))) {
					//"Le fichier saisie ne possede pas une extension CSV: " + str_tmp3);
					end.setText("");
					Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error108"), nom));
					valider.setEnabled(true);
					return;
				}

				progressBar.setVisible(true);
				if (RangementUtils.write_CSV(aFile, bottles, progressBar)) {
					end.setText(Program.getLabel("Infos154")); //"Export termine."
					Erreur.showSimpleErreur(MessageFormat.format(Program.getLabel("Main.savedFile"), aFile.getAbsolutePath()),
							Program.getLabel("Export.CSVInfo"), true);
					openit.setEnabled(true);
				}
				progressBar.setVisible(false);
			}	else if (MyCellarRadioButtonXLS.isSelected()) {
				if (MyCellarControl.hasInvalidExtension(nom, Arrays.asList(Filtre.FILTRE_XLSX.toString(), Filtre.FILTRE_XLS.toString(), Filtre.FILTRE_ODS.toString()))) {
					//"Le fichier saisie ne possede pas une extension XLS: " + str_tmp3);
					end.setText("");
					Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error034"), nom));
					valider.setEnabled(true);
					return;
				}

				progressBar.setVisible(true);
				if (RangementUtils.write_XLS(aFile, bottles, false, progressBar)) {
					end.setText(Program.getLabel("Infos154")); //"Export termine."
					Erreur.showSimpleErreur(MessageFormat.format(Program.getLabel("Main.savedFile"), aFile.getAbsolutePath()), true);
					openit.setEnabled(true);
				}	else {
					end.setText(Program.getError("Error129")); //"Erreur lors de l'export"
					Erreur.showSimpleErreur(Program.getError("Error160"), Program.getError("Error161"));
				}
				progressBar.setVisible(false);
			}	else if (MyCellarRadioButtonPDF.isSelected()) {
				if (MyCellarControl.hasInvalidExtension(nom, Arrays.asList(Filtre.FILTRE_PDF.toString()))) {
					//"Le fichier saisie ne possede pas une extension PDF: " + str_tmp3);
					end.setText("");
					Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error157"), nom));
					valider.setEnabled(true);
					return;
				}

				if (exportToPDF(bottles, aFile)) {
					end.setText(Program.getLabel("Infos154")); //"Export termine."
					openit.setEnabled(true);
				} else {
					end.setText("");
				}
			}
			valider.setEnabled(true);
		}	catch (Exception e1) {
			Program.showException(e1);
		}
	}

	/**
	 * @param bottles
	 * @param nomFichier
	 * @return
	 */
public static boolean exportToPDF(final List<Bouteille> bottles, File nomFichier) {
		try {
			final PDFTools pdf = new PDFTools();
			pdf.addTitle(20);
			PDFPageProperties pageProperties = new PDFPageProperties(30, 20, 20, 20, PDType1Font.HELVETICA, pdf.getProperties().getFontSize());
			pageProperties.setStartTop(50);
			pdf.drawTable(pageProperties, Program.getPDFRows(bottles, pdf.getProperties()), Program.getPDFHeader(pdf.getProperties()));
			pdf.save(nomFichier);
			Erreur.showSimpleErreur(MessageFormat.format(Program.getLabel("Main.savedFile"), nomFichier.getAbsolutePath()), true);
		} catch (IOException | RuntimeException ex) {
			Program.showException(ex, false);
			Erreur.showSimpleErreur(Program.getError("Error160"), Program.getError("Error161"));
			return false;
		}
		return true;
	}

	/**
	 * param_actionPerformed: Appelle la fenetre de parametres.
	 */
	private void param_actionPerformed() {
		String titre = Program.getLabel("Infos310");
		String message2 = Program.getLabel("Infos309");
		String[] titre_properties = {Program.getLabel("Infos210"),
			Program.getLabel("Infos211"),
			Program.getLabel("Infos212"),
			Program.getLabel("Infos233"),
			Program.getLabel("Infos248")};
		String[] default_value = {"false", "false", "false", "false", "false"};
		String[] key_properties = {MyCellarSettings.EXPORT_DEFAULT, MyCellarSettings.EXPORT_DEFAULT,
			MyCellarSettings.EXPORT_DEFAULT, MyCellarSettings.EXPORT_DEFAULT, MyCellarSettings.EXPORT_DEFAULT};
		default_value[Program.getCaveConfigInt(key_properties[0], 0)] = "true";

		String[] type_objet = {"MyCellarRadioButton", "MyCellarRadioButton", "MyCellarRadioButton", "MyCellarRadioButton", "MyCellarRadioButton"};
		MyOptions myoptions = new MyOptions(titre, "", message2, titre_properties, default_value, key_properties, type_objet, Program.getCaveConfig(), false);
		myoptions.setVisible(true);
	}

  @Override
  public void cut() {
    String text = file.getSelectedText();
    String fullText = file.getText();
    if (text != null) {
      file.setText(fullText.substring(0, file.getSelectionStart()) + fullText.substring(file.getSelectionEnd()));
      Program.CLIPBOARD.copier(text);
    }
  }

  @Override
  public void copy() {
    String text = file.getSelectedText();
    if (text != null) {
      Program.CLIPBOARD.copier(text);
    }
  }

  @Override
  public void paste() {
    String fullText = file.getText();
    file.setText(fullText.substring(0,  file.getSelectionStart()) + Program.CLIPBOARD.coller() + fullText.substring(file.getSelectionEnd()));
  }

	@Override
	public boolean tabWillClose(TabEvent event) {
		return true;
	}

	@Override
	public void tabClosed() {
		Start.getInstance().updateMainPanel();
	}

}
