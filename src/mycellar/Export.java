package mycellar;

import mycellar.core.ICutCopyPastable;
import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarFields;
import mycellar.core.MyCellarLabel;
import mycellar.core.MyCellarRadioButton;
import mycellar.core.PopupListener;
import mycellar.pdf.PDFPageProperties;
import mycellar.pdf.PDFProperties;
import mycellar.pdf.PDFTools;
import mycellar.showfile.ManageColumnModel;
import net.miginfocom.swing.MigLayout;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2004</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 7.5
 * @since 29/05/18
 */
public class Export extends JPanel implements ITabListener, Runnable, ICutCopyPastable {

	private final MyCellarLabel textControl1 = new MyCellarLabel();
	private final MyCellarButton valider = new MyCellarButton();
	private final JTextField file = new JTextField();
	private final MyCellarButton browse = new MyCellarButton();
	private final MyCellarButton parameters = new MyCellarButton();
	private final MyCellarRadioButton MyCellarRadioButtonXML = new MyCellarRadioButton(Program.getLabel("Infos210"), true);
	private final MyCellarRadioButton MyCellarRadioButtonHTML = new MyCellarRadioButton(Program.getLabel("Infos211"), false);
	private final MyCellarLabel MyCellarLabel1 = new MyCellarLabel();
	private final MyCellarRadioButton MyCellarRadioButtonCSV = new MyCellarRadioButton(Program.getLabel("Infos212"), false);
	private final MyCellarRadioButton MyCellarRadioButtonXLS = new MyCellarRadioButton(Program.getLabel("Infos233"), false);
	private final MyCellarRadioButton MyCellarRadioButtonPDF = new MyCellarRadioButton(Program.getLabel("Infos248"), false);
	private final ButtonGroup cbg = new ButtonGroup();
	private final MyCellarLabel end = new MyCellarLabel();
	private final MyCellarButton openit = new MyCellarButton();
	private final MyCellarButton options = new MyCellarButton(Program.getLabel("Infos193") + "...");
	private char OUVRIR = Program.getLabel("OUVRIR").charAt(0);
	private char EXPORT = Program.getLabel("EXPORT").charAt(0);
	private final JMenuItem param = new JMenuItem(Program.getLabel("Infos156"));
	private boolean isJFile = false;
	private List<Bouteille> bottles = null;
	static final long serialVersionUID = 240706;

	/**
	 * Export: Constructeur pour l'export.
	 */
	public Export() {
		try {
			initialize();
		}
		catch (Exception e) {
			Program.showException(e);
		}
	}

	/**
	 * Export: Constructeur pour l'export.
	 *
	 * @param bottles LinkedList<Bouteille>: Contenu de la cave.
	 */
	public Export(List<Bouteille> bottles) {
		this.bottles = bottles;
		try {
			initialize();
		}
		catch (Exception e) {
			Program.showException(e);
		}
	}

	/**
	 * initialize: Fonction d'initialisation.
	 *
	 * @throws Exception
	 */
	private void initialize() {

		textControl1.setText(Program.getLabel("Infos149")); //Nom du fichier:
		browse.setText("...");
		parameters.setText(Program.getLabel("Main.Parameters"));
		MyCellarLabel1.setText(Program.getLabel("Infos151")); //Format de l\'Export:
		end.setFont(Program.font_dialog_small);
		openit.setMnemonic(OUVRIR);
		openit.setText(Program.getLabel("Infos152")); //Ouvrir le fichier
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

		cbg.add(MyCellarRadioButtonXML);
		cbg.add(MyCellarRadioButtonHTML);
		cbg.add(MyCellarRadioButtonCSV);
		cbg.add(MyCellarRadioButtonXLS);
		cbg.add(MyCellarRadioButtonPDF);

		valider.setText(Program.getLabel("Infos153")); //Exporter
		valider.setMnemonic(EXPORT);


		valider.addActionListener((e) -> valider_actionPerformed());
		options.addActionListener((e) -> options_actionPerformed());
		browse.addActionListener((e) -> browse_actionPerformed());

		addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(KeyEvent e) {}
			@Override
			public void keyPressed(KeyEvent e) {
				keylistener_actionPerformed(e);
			}
			@Override
			public void keyTyped(KeyEvent e) {}
		});

		setLayout(new MigLayout("","grow",""));
		JPanel panelTitle = new JPanel();
		panelTitle.setLayout(new MigLayout("", "grow",""));
		panelTitle.add(textControl1, "split 4");
		panelTitle.add(file, "grow");
		panelTitle.add(browse);
		panelTitle.add(parameters);
		add(panelTitle,"grow, wrap");
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
		JPanel panelEnd = new JPanel();
		panelEnd.setLayout(new MigLayout("", "grow",""));
		panelEnd.add(end, "grow, center, hidemode 3, wrap");
		panelEnd.add(valider, "center, split 2");
		panelEnd.add(openit);
		add(panelEnd, "grow");
		openit.setEnabled(false);
		options.setEnabled(false);

		int val = Program.getCaveConfigInt("EXPORT_DEFAULT", 0);

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
	 * browse_actionPerformed: Fonction pour parcourir les répertoires.
	 */
	private void browse_actionPerformed() {

		end.setText("");
		JFileChooser boiteFichier = new JFileChooser(Program.getCaveConfigString("DIR",""));
		boiteFichier.removeChoosableFileFilter(boiteFichier.getFileFilter());
		if (MyCellarRadioButtonPDF.isSelected()) {
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_PDF);
		} else if (MyCellarRadioButtonXLS.isSelected()) {
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XLS);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_ODS);
		} else if (MyCellarRadioButtonCSV.isSelected()) {
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_CSV);
		} else if (MyCellarRadioButtonHTML.isSelected()) {
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_HTM);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_HTML);
		} else if (MyCellarRadioButtonXML.isSelected()) {
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XML);
		}

		if (boiteFichier.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			File nomFichier = boiteFichier.getSelectedFile();
			Program.putCaveConfigString("DIR", boiteFichier.getCurrentDirectory().toString());
			String nom = nomFichier.getName();
			//Erreur utilisation de caractères interdits
			if (nom.contains("\"") || nom.contains(";") || nom.contains("<") || nom.contains(">") || nom.contains("?") || nom.contains("\\") || nom.contains("/") ||
					nom.contains("|") || nom.contains("*") ) {
				Erreur.showSimpleErreur(Program.getError("Error126"));
			}
			else {
				String fic = nomFichier.getAbsolutePath();
				int index = fic.indexOf(".");
				if (index == -1) {
					if (MyCellarRadioButtonXML.isSelected()) {
						fic = fic.concat(".xml");
					}	else if (MyCellarRadioButtonHTML.isSelected()) {
						fic = fic.concat(".htm");
					}	else if (MyCellarRadioButtonCSV.isSelected()) {
						fic = fic.concat(".csv");
					}	else if (MyCellarRadioButtonXLS.isSelected()) {
						Filtre filtre = (Filtre) boiteFichier.getFileFilter();
						if (filtre.toString().equals("xls"))
							fic = fic.concat(".xls");
						if (filtre.toString().equals("ods"))
							fic = fic.concat(".ods");
					}	else if (MyCellarRadioButtonPDF.isSelected()) {
						fic = fic.concat(".pdf");
					}
				}
				file.setText(fic);
				isJFile = true;
			}
		}
	}

	/**
	 * openit_actionPerformed: Ouvrir le fichier issu de l'export.
	 */
	private void openit_actionPerformed() {

		String nom = file.getText().trim();
		if (!nom.isEmpty()) {
			File f = new File(nom);
			if(!f.exists() || f.isDirectory()) {
				end.setText("");
				//Fichier non trouvé
				//Vérifier le chemin
				Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error020"), nom), Program.getError("Error022"));
				return;
			}
			Program.open(f);
		}
	}

	/**
	 * options_actionPerformed: Appel de la fenêtre d'options.
	 */
	private void options_actionPerformed() {
		end.setText("");
		if (MyCellarRadioButtonPDF.isSelected()) {
			PDFOptions ef = new PDFOptions();
			ef.setAlwaysOnTop(true);
			ef.setVisible(true);
			options.setSelected(false);
		}
		else if (MyCellarRadioButtonXLS.isSelected()) {
			XLSOptions xf = new XLSOptions();
			xf.setAlwaysOnTop(true);
			xf.setVisible(true);
			options.setSelected(false);
		}
		else if (MyCellarRadioButtonCSV.isSelected()) {
			CSVOptions cf = new CSVOptions();
			cf.setAlwaysOnTop(true);
			cf.setVisible(true);
			options.setSelected(false);
		}
		else if(MyCellarRadioButtonHTML.isSelected()) {
			ArrayList<MyCellarFields> list = MyCellarFields.getFieldsList();
			ArrayList<MyCellarFields> cols = Program.getHTMLColumns();
			ManageColumnModel modelColumn = new ManageColumnModel(list, cols);
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
			cols = new ArrayList<>();
			Program.setModified();
			List<Integer> properties = modelColumn.getSelectedColumns();
			for(MyCellarFields c : list) {
				if(properties.contains(c.ordinal()))
					cols.add(c);
			}
			Program.saveHTMLColumns(cols);
		}
	}

	/**
	 * keylistener_actionPerformed: Ecouteur du clavier.
	 *
	 * @param e KeyEvent
	 */
	void keylistener_actionPerformed(KeyEvent e) {
		if (e.getKeyCode() == OUVRIR && openit.isEnabled()) {
			openit_actionPerformed();
		}
		else if (e.getKeyCode() == EXPORT) {
			valider_actionPerformed();
		}
		else if (e.getKeyCode() == KeyEvent.VK_F1) {
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
	 * run: Exécution des tâches.
	 */
	@Override
	public void run() {
		try {
			valider.setEnabled(false);
			openit.setEnabled(false);
			String nom = file.getText().trim();
			end.setText(Program.getLabel("Infos250"));

			if (nom.isEmpty()) {
				end.setText("");
				Erreur.showSimpleErreur(Program.getError("Error106")); //Veuillez saisir un nom de fichier.
				valider.setEnabled(true);
				return;
			}

			File aFile = new File(nom);
			if(aFile.exists()) {
				// Existing file. replace?
				if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(null, MessageFormat.format(Program.getError("Export.replaceFileQuestion"), aFile.getAbsolutePath()), Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
					end.setText("");
					valider.setEnabled(true);
					return;
				}
			}

			String extension = nom;
			if (nom.length() >= 4) {
				extension = nom.substring(nom.length() - 4);
			}
			if (MyCellarRadioButtonXML.isSelected()) {
				if (!extension.equalsIgnoreCase(".xml")) {
					end.setText("");
					Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error087"), extension), true); //L'extension du fichier n'est pas XML
					valider.setEnabled(true);
					return;
				}
				if (!isJFile) {
					if (nom.contains("\"") || nom.contains(";") || nom.contains("<") || nom.contains(">") || nom.contains("?") || nom.contains("|") || nom.contains("*")) {
						end.setText("");
						Erreur.showSimpleErreur(Program.getError("Error126"));
						valider.setEnabled(true);
						return;
					}
				}

				boolean ok;
				if(bottles == null) {
					ok = ListeBouteille.writeXML(aFile);
				}
				else {
					ListeBouteille liste = new ListeBouteille();
					for(Bouteille b: bottles) {
						liste.getBouteille().add(b);
					}
					ok = ListeBouteille.writeXML(liste, aFile);
				}
				if (ok) {
					end.setText(Program.getLabel("Infos154")); //"Export terminé."
					openit.setEnabled(true);
				}
				else {
					end.setText(Program.getError("Error129")); //"Erreur lors de l'export"
				}
			}
			else if (MyCellarRadioButtonHTML.isSelected()) {
					if (extension.compareToIgnoreCase(".htm") != 0 && extension.compareToIgnoreCase("html") != 0) {
						end.setText("");
						Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error107"), extension), true); //L'extension du fichier n'est pas HTML
						valider.setEnabled(true);
						return;
					}
					if( null == bottles) {
						bottles = Program.getStorage().getAllList();
					}
					if (RangementUtils.write_HTML(nom, bottles, Program.getHTMLColumns())) {
						end.setText(Program.getLabel("Infos154")); //"Export terminé."
						Erreur.showSimpleErreur(MessageFormat.format(Program.getLabel("Main.savedFile"), file.getText().trim()), true);
						openit.setEnabled(true);
					}
					else {
						end.setText(Program.getError("Error129")); //"Erreur lors de l'export"
					}
				}
				else if (MyCellarRadioButtonCSV.isSelected()) {
					if (extension.compareToIgnoreCase(".csv") != 0) {
						end.setText("");
						Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error108"), extension), true); //L'extension du fichier n'est pas CSV
						valider.setEnabled(true);
						return;
					}
					if( bottles == null) {
						bottles = Program.getStorage().getAllList();
					}
					if (RangementUtils.write_CSV(nom, bottles)) {
						end.setText(Program.getLabel("Infos154")); //"Export terminé."
						Erreur.showSimpleErreur(MessageFormat.format(Program.getLabel("Main.savedFile"), file.getText().trim()), true);
						openit.setEnabled(true);
					}
				}
			else if (MyCellarRadioButtonXLS.isSelected()) {
				if (extension.compareToIgnoreCase(".xls") != 0 && extension.compareToIgnoreCase(".ods") != 0) {
					end.setText("");
					Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error034"), extension), true); //L'extension du fichier n'est pas CSV
					valider.setEnabled(true);
					return;
				}
				if(bottles == null) {
					bottles = Program.getStorage().getAllList();
				}
				if (RangementUtils.write_XLS(nom, bottles, false)) {
					end.setText(Program.getLabel("Infos154")); //"Export terminé."
					Erreur.showSimpleErreur(MessageFormat.format(Program.getLabel("Main.savedFile"), file.getText().trim()), true);
					openit.setEnabled(true);
				}
				else {
					end.setText(Program.getError("Error129")); //"Erreur lors de l'export"
					Erreur.showSimpleErreur(Program.getError("Error160"), Program.getError("Error161"));
				}
			}
			else if (MyCellarRadioButtonPDF.isSelected()) {
				if (extension.compareToIgnoreCase(".pdf") != 0) {
					end.setText("");
					Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error157"), extension), true); //L'extension du fichier n'est pas PDF
					valider.setEnabled(true);
					return;
				}
				if( bottles == null) {
					bottles = Program.getStorage().getAllList();
				}
				if (exportToPDF(bottles, aFile)) {
					end.setText(Program.getLabel("Infos154")); //"Export terminé."
					openit.setEnabled(true);
				}
			}
			valider.setEnabled(true);
		}
		catch (Exception e1) {
			Program.showException(e1);
		}
	}

	/**
	 * @param bottles
	 * @param nomFichier
	 * @return
	 */
public static boolean exportToPDF(List<Bouteille> bottles, File nomFichier) {
		try {
			PDFTools pdf = PDFTools.createPDFFile();
			PDFProperties properties = Program.getPDFProperties();
			pdf.addTitle(properties.getTitle(), 20, properties.isBoldTitle() ? PDType1Font.HELVETICA_BOLD : PDType1Font.HELVETICA, properties.getTitleSize());
			PDFPageProperties pageProperties = new PDFPageProperties(30, 20, 20, 20, PDType1Font.HELVETICA, properties.getFontSize());
			pageProperties.setStartTop(50);
			pdf.drawTable(pageProperties, properties, Program.getPDFRows(bottles, properties), Program.getPDFHeader(properties));
			pdf.save(nomFichier);
			Erreur.showSimpleErreur(MessageFormat.format(Program.getLabel("Main.savedFile"), nomFichier.getAbsolutePath()), true);
		}
		catch (Exception ex) {
			Program.showException(ex, false);
			Erreur.showSimpleErreur(Program.getError("Error160"), Program.getError("Error161"));
			return false;
		}
		return true;
	}

	/**
	 * param_actionPerformed: Appelle la fenêtre de paramètres.
	 */
	private void param_actionPerformed() {
		String titre = Program.getLabel("Infos310");
		String message2 = Program.getLabel("Infos309");
		String titre_properties[] = new String[5];
		titre_properties[0] = Program.getLabel("Infos210");
		titre_properties[1] = Program.getLabel("Infos211");
		titre_properties[2] = Program.getLabel("Infos212");
		titre_properties[3] = Program.getLabel("Infos233");
		titre_properties[4] = Program.getLabel("Infos248");
		String default_value[] = new String[5];
		String key_properties[] = new String[5];
		key_properties[0] = "EXPORT_DEFAULT";
		key_properties[1] = "EXPORT_DEFAULT";
		key_properties[2] = "EXPORT_DEFAULT";
		key_properties[3] = "EXPORT_DEFAULT";
		key_properties[4] = "EXPORT_DEFAULT";
		String val = Program.getCaveConfigString(key_properties[0], "0");

		default_value[0] = "false";
		default_value[1] = "false";
		default_value[2] = "false";
		default_value[3] = "false";
		default_value[4] = "false";
		if (val.equals("0")) {
			default_value[0] = "true";
		}
		if (val.equals("1")) {
			default_value[1] = "true";
		}
		if (val.equals("2")) {
			default_value[2] = "true";
		}
		if (val.equals("3")) {
			default_value[3] = "true";
		}
		if (val.equals("4")) {
			default_value[4] = "true";
		}
		String type_objet[] = {"MyCellarRadioButton", "MyCellarRadioButton", "MyCellarRadioButton", "MyCellarRadioButton", "MyCellarRadioButton"};
		MyOptions myoptions = new MyOptions(titre, "", message2, titre_properties, default_value, key_properties, type_objet, Program.getCaveConfig(), false);
		myoptions.setVisible(true);
	}

  @Override
  public void cut() {
    String text = file.getSelectedText();
    String fullText = file.getText();
    if(text != null) {
      file.setText(fullText.substring(0, file.getSelectionStart()) + fullText.substring(file.getSelectionEnd()));
      Program.clipboard.copier(text);
    }
  }

  @Override
  public void copy() {
    String text = file.getSelectedText();
    if(text != null) {
      Program.clipboard.copier(text);
    }
  }

  @Override
  public void paste() {
    String fullText = file.getText();
    file.setText(fullText.substring(0,  file.getSelectionStart()) + Program.clipboard.coller() + fullText.substring(file.getSelectionEnd()));
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
