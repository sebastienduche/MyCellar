package mycellar;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.MessageFormat;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarCheckBox;
import mycellar.core.MyCellarFields;
import mycellar.core.MyCellarLabel;
import mycellar.core.MyCellarRadioButton;
import mycellar.pdf.PDFPageProperties;
import mycellar.pdf.PDFProperties;
import mycellar.pdf.PDFTools;
import mycellar.showfile.ManageColumnModel;
import net.miginfocom.swing.MigLayout;

import org.apache.pdfbox.pdmodel.font.PDType1Font;



/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2004</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 5.9
 * @since 13/11/16
 */
public class Export extends JPanel implements ITabListener, Runnable {

	private MyCellarLabel textControl1 = new MyCellarLabel();
	private MyCellarButton valider = new MyCellarButton();
	private JTextField file = new JTextField();
	private MyCellarButton browse = new MyCellarButton();
	private MyCellarButton parameters = new MyCellarButton();
	private MyCellarRadioButton MyCellarRadioButtonXML = new MyCellarRadioButton(Program.getLabel("Infos210"), true);
	private MyCellarRadioButton MyCellarRadioButtonHTML = new MyCellarRadioButton(Program.getLabel("Infos211"), false);
	private MyCellarLabel MyCellarLabel1 = new MyCellarLabel();
	private MyCellarRadioButton MyCellarRadioButtonCSV = new MyCellarRadioButton(Program.getLabel("Infos212"), false);
	private MyCellarRadioButton MyCellarRadioButtonXLS = new MyCellarRadioButton(Program.getLabel("Infos233"), false);
	private MyCellarRadioButton MyCellarRadioButtonPDF = new MyCellarRadioButton(Program.getLabel("Infos248"), false);
	private ButtonGroup cbg = new ButtonGroup();
	private MyCellarLabel end = new MyCellarLabel();
	private MyCellarButton openit = new MyCellarButton();
	private MyCellarCheckBox options = new MyCellarCheckBox(Program.getLabel("Infos193") + "...");
	private char OUVRIR = Program.getLabel("OUVRIR").charAt(0);
	private char EXPORT = Program.getLabel("EXPORT").charAt(0);
	private JPopupMenu popup = new JPopupMenu();
	private JMenuItem couper = new JMenuItem(Program.getLabel("Infos241"), new ImageIcon("./resources/Cut16.gif"));
	private JMenuItem copier = new JMenuItem(Program.getLabel("Infos242"), new ImageIcon("./resources/Copy16.gif"));
	private JMenuItem coller = new JMenuItem(Program.getLabel("Infos243"), new ImageIcon("./resources/Paste16.gif"));
	private JMenuItem cut = new JMenuItem(Program.getLabel("Infos241"), new ImageIcon("./resources/Cut16.gif"));
	private JMenuItem copy = new JMenuItem(Program.getLabel("Infos242"), new ImageIcon("./resources/Copy16.gif"));
	private JMenuItem paste = new JMenuItem(Program.getLabel("Infos243"), new ImageIcon("./resources/Paste16.gif"));
	private MyClipBoard clipboard = new MyClipBoard();
	private JMenuItem param = new JMenuItem(Program.getLabel("Infos156"));
	private Component objet1 = null;
	private PDFOptions ef = null;
	private XLSOptions xf = null;
	private CSVOptions cf = null;
	private boolean isJFile = false;
	private LinkedList<Bouteille> bottles = null;
	static final long serialVersionUID = 240706;

	/**
	 * Export: Constructeur pour l'export.
	 */
	public Export() {
		try {
			jbInit();
		}
		catch (Exception e) {
			Program.showException(e);
		}
	}

	/**
	 * Export: Constructeur pour l'export.
	 *
	 * @param cave LinkedList<Bouteille>: Contenu de la cave.
	 */
	public Export(LinkedList<Bouteille> cave) {
		this.bottles = cave;
		try {
			jbInit();
		}
		catch (Exception e) {
			Program.showException(e);
		}
	}

	/**
	 * jbInit: Fonction d'initialisation.
	 *
	 * @param cave Rangement: contenu de la cave.
	 * @throws Exception
	 */
	private void jbInit() throws Exception {

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
		end.setHorizontalAlignment(0);
		end.setForeground(Color.red);
		MyCellarRadioButtonXLS.addActionListener((e) -> jradio_actionPerformed());
		MyCellarRadioButtonPDF.addActionListener((e) -> jradio_actionPerformed());

		couper.addActionListener((e) -> couper_actionPerformed());
		cut.addActionListener((e) -> couper_actionPerformed());
		copier.addActionListener((e) -> copier_actionPerformed());
		copy.addActionListener((e) -> copier_actionPerformed());
		coller.addActionListener((e) -> coller_actionPerformed());
		paste.addActionListener((e) -> coller_actionPerformed());
		param.addActionListener((e) -> param_actionPerformed());
		couper.setEnabled(false);
		copier.setEnabled(false);
		popup.add(couper);
		popup.add(copier);
		popup.add(coller);
		MouseListener popup_l = new PopupListener();
		file.addMouseListener(popup_l);
		cut.setEnabled(false);
		copy.setEnabled(false);
		cut.setAccelerator(KeyStroke.getKeyStroke('X', ActionEvent.CTRL_MASK));
		copy.setAccelerator(KeyStroke.getKeyStroke('C', ActionEvent.CTRL_MASK));
		paste.setAccelerator(KeyStroke.getKeyStroke('V', ActionEvent.CTRL_MASK));

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

		this.addKeyListener(new java.awt.event.KeyListener() {
			public void keyReleased(java.awt.event.KeyEvent e) {}

			public void keyPressed(java.awt.event.KeyEvent e) {
				keylistener_actionPerformed(e);
			}

			public void keyTyped(java.awt.event.KeyEvent e) {}
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
		panelFormat.add(options,"grow, push");
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

		if (val == 0) {
			MyCellarRadioButtonXML.setSelected(true);
			options.setEnabled(false);
		}
		else {
			if (val == 1) {
				MyCellarRadioButtonHTML.setSelected(true);
				options.setEnabled(true);
			}
			else {
				if (val == 2) {
					MyCellarRadioButtonCSV.setSelected(true);
					options.setEnabled(true);
				}
				else {
					if (val == 3) {
						MyCellarRadioButtonXLS.setSelected(true);
						options.setEnabled(true);
					}
					else {
						MyCellarRadioButtonPDF.setSelected(true);
						options.setEnabled(true);
					}
				}
			}
		}

		this.setVisible(true);
	}

	//Exporter
	/**
	 * valider_actionPerformed: Fonction d'export.
	 */
	void valider_actionPerformed() {
		new Thread(this).start();
	}

	/**
	 * browse_actionPerformed: Fonction pour parcourir les répertoires.
	 */
	void browse_actionPerformed() {

		end.setText("");
		JFileChooser boiteFichier = new JFileChooser(Program.getCaveConfigString("DIR",""));
		boiteFichier.removeChoosableFileFilter(boiteFichier.getFileFilter());
		if (MyCellarRadioButtonPDF.isSelected()) {
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_PDF);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XML);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_CSV);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_HTM);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_HTML);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XLS);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_ODS);
		}
		if (MyCellarRadioButtonXLS.isSelected()) {
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XLS);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XML);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_CSV);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_HTM);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_HTML);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_PDF);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_ODS);
		}
		if (MyCellarRadioButtonCSV.isSelected()) {
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_CSV);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XML);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_PDF);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_HTM);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_HTML);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XLS);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_ODS);
		}
		if (MyCellarRadioButtonHTML.isSelected()) {
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_HTML);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XML);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_PDF);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XLS);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_ODS);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_CSV);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_HTM);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_HTML);
		}
		if (MyCellarRadioButtonXML.isSelected()) {
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XML);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_PDF);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XLS);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_ODS);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_CSV);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_HTM);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_HTML);
		}
		int retour_jfc = boiteFichier.showSaveDialog(this);
		File nomFichier = new File("");
		String fic = "";
		if (retour_jfc == JFileChooser.APPROVE_OPTION) {
			nomFichier = boiteFichier.getSelectedFile();
			String nom = boiteFichier.getSelectedFile().getName();
			Program.putCaveConfigString("DIR", boiteFichier.getCurrentDirectory().toString());
			//Erreur utilisation de caractères interdits
			if (nom.indexOf("\"") != -1 || nom.indexOf(";") != -1 || nom.indexOf("<") != -1 || nom.indexOf(">") != -1 || nom.indexOf("?") != -1 || nom.indexOf("\\") != -1 || nom.indexOf("/") != -1 ||
					nom.indexOf("|") != -1 || nom.indexOf("*") != -1) {
				new Erreur(Program.getError("Error126"), "");
			}
			else {
				fic = nomFichier.getAbsolutePath();
				int index = fic.indexOf(".");
				if (index == -1) {
					if (MyCellarRadioButtonXML.isSelected()) {
						fic = fic.concat(".xml");
					}
					if (MyCellarRadioButtonHTML.isSelected()) {
						fic = fic.concat(".htm");
					}
					if (MyCellarRadioButtonCSV.isSelected()) {
						fic = fic.concat(".csv");
					}
					if (MyCellarRadioButtonXLS.isSelected()) {
						Filtre filtre = (Filtre) boiteFichier.getFileFilter();
						if (filtre.toString().equals("xls"))
							fic = fic.concat(".xls");
						if (filtre.toString().equals("ods"))
							fic = fic.concat(".ods");
					}
					if (MyCellarRadioButtonPDF.isSelected()) {
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
	void openit_actionPerformed() {

		int resul = 0;
		String nom = file.getText().trim();
		if (!nom.isEmpty()) {
			File f = new File(nom);
			try {
				new FileInputStream(f);
			}
			catch (FileNotFoundException fnfe1) {
				//Insertion classe Erreur
				end.setText("");
				String erreur_txt1 = new String(Program.getError("Error020") + " " + nom + " " + Program.getError("Error021")); //Fichier non trouv�
				String erreur_txt2 = Program.getError("Error022"); //Vérifier le chemin
				new Erreur(erreur_txt1, erreur_txt2);
				resul = 1;
			}

			if (resul == 0) {
				Program.open(new File(file.getText()));
			}
		}
	}

	/**
	 * options_actionPerformed: Appel de la fenêtre d'options.
	 */
	void options_actionPerformed() {
		end.setText("");
		if (MyCellarRadioButtonPDF.isSelected()) {
			ef = new PDFOptions();
			ef.setAlwaysOnTop(true);
			ef.setVisible(true);
			ef = null;
			options.setSelected(false);
		}
		else if (MyCellarRadioButtonXLS.isSelected()) {
			xf = new XLSOptions();
			xf.setAlwaysOnTop(true);
			xf.setVisible(true);
			xf = null;
			options.setSelected(false);
		}
		else if (MyCellarRadioButtonCSV.isSelected()) {
			cf = new CSVOptions();
			cf.setAlwaysOnTop(true);
			cf.setVisible(true);
			cf = null;
			options.setSelected(false);
		}
		else if(MyCellarRadioButtonHTML.isSelected()) {
			LinkedList<MyCellarFields> list = MyCellarFields.getFieldsList();
			LinkedList<MyCellarFields> cols = Program.getHTMLColumns();
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
			JOptionPane.showMessageDialog(null, panel, Program.getLabel("Main.Columns"), JOptionPane.PLAIN_MESSAGE);
			cols = new LinkedList<MyCellarFields>();
			Program.setModified();
			LinkedList<Integer> properties = modelColumn.getSelectedColumns();
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
	void jradio_actionPerformed() {
		end.setText("");
		if (MyCellarRadioButtonXML.isSelected()) {
			options.setEnabled(false);
		}
		else {
			options.setEnabled(true);
		}
	}

	/**
	 * this_windowActivated: Mis au premier plan de l'erreur.
	 *
	 * @param e WindowEvent
	 */
	void this_windowActivated(WindowEvent e) {
		if (ef != null) {
			ef.toFront();
		}
		if (xf != null) {
			xf.toFront();
		}
		if (cf != null) {
			cf.toFront();
		}
	}

	/**
	 * couper_actionPerformed: Couper
	 */
	void couper_actionPerformed() {
		String txt = "";
		try {
			JTextField jtf = (JTextField) objet1;
			txt = jtf.getSelectedText();
			jtf.setText(jtf.getText().substring(0, jtf.getSelectionStart()) + jtf.getText().substring(jtf.getSelectionEnd()));
		}
		catch (Exception e1) {}
		;
		clipboard.copier(txt);
	}

	/**
	 * copier_actionPerformed: Copier
	 */
	void copier_actionPerformed() {
		String txt = "";
		try {
			JTextField jtf = (JTextField) objet1;
			txt = jtf.getSelectedText();
		}
		catch (Exception e1) {}
		clipboard.copier(txt);
	}

	/**
	 * coller_actionPerformed: Couper
	 */
	void coller_actionPerformed() {

		try {
			JTextField jtf = (JTextField) objet1;
			jtf.setText(jtf.getText().substring(0, jtf.getSelectionStart()) + clipboard.coller() + jtf.getText().substring(jtf.getSelectionEnd()));
		}
		catch (Exception e1) {}
	}

	/**
	 * aide_actionPerformed: Aide
	 */
	void aide_actionPerformed() {
		Program.getAide();
	}


	/**
	 * run: Exécution des tâches.
	 */
	public void run() {
		try {
			int resul = 0;
			//int nb_pdf = 0;
			//boolean more2000 = false;
			valider.setEnabled(false);
			openit.setEnabled(false);
			String nom = file.getText().trim();
			File f = new File(nom);
			String path = f.getAbsolutePath();
			end.setText(Program.getLabel("Infos250"));

			if (nom.isEmpty()) {
				end.setText("");
				resul = 1;
				new Erreur(Program.getError("Error106")); //Veuillez saisir un nom de fichier.
			}

			if (resul == 0) {
				String extension = nom;
				if (nom.length() >= 4) {
					extension = nom.substring(nom.length() - 4);
				}
				if (MyCellarRadioButtonXML.isSelected()) {
					if (extension.toLowerCase().compareTo(".xml") != 0) {
						new Erreur(Program.getError("Error087") + " " + extension, "", true); //L'extension du fichier n'est pas XML
						end.setText("");
						resul = 1;
					}
					if (resul == 0 && !isJFile) {
						if (nom.indexOf("\"") != -1 || nom.indexOf(";") != -1 || nom.indexOf("<") != -1 || nom.indexOf(">") != -1 || nom.indexOf("?") != -1 || nom.indexOf("|") != -1 || nom.indexOf("*") != -1) {
							resul = 1;
							new Erreur(Program.getError("Error126"), "");
							end.setText("");
						}
					}

					if (resul == 0) {
						file.setText(path);
						File aFile = new File(file.getText());
						if(!aFile.exists())
							aFile.createNewFile();
						if(bottles == null) {
							if(ListeBouteille.writeXML(aFile))
								resul = 0;
							else
								resul = -2;
						}
						else
						{
							ListeBouteille liste = new ListeBouteille();
							for(Bouteille b: bottles)
								liste.getBouteille().add(b);
							if(ListeBouteille.writeXML(liste, aFile))
								resul = 0;
							else
								resul = -2;
						}
						if (resul != -2) {
							end.setText(Program.getLabel("Infos154")); //"Export terminé."
							openit.setEnabled(true);
						}
						else {
							end.setText(Program.getError("Error129")); //"Erreur lors de l'export"
						}
					}
				}
				else {
					if (MyCellarRadioButtonHTML.isSelected()) {
						if (extension.compareToIgnoreCase(".htm") != 0 && extension.compareToIgnoreCase("html") != 0) {
							end.setText("");
							new Erreur(Program.getError("Error107") + " " + extension, "", true); //L'extension du fichier n'est pas HTML
							resul = 1;
						}
						if (resul == 0) {
							file.setText(path);
							if( null == bottles)
								bottles = Program.getStorage().getAllList();
							if (Rangement.write_HTML(file.getText().trim(), bottles, Program.getHTMLColumns())) {
								end.setText(Program.getLabel("Infos154")); //"Export terminé."
								openit.setEnabled(true);
							}
							else {
								end.setText(Program.getError("Error129")); //"Erreur lors de l'export"
							}
						}
					}
					else {
						if (MyCellarRadioButtonCSV.isSelected()) {
							if (extension.compareToIgnoreCase(".csv") != 0) {
								end.setText("");
								new Erreur(Program.getError("Error108") + " " + extension, "", true); //L'extension du fichier n'est pas CSV
								resul = 1;
							}
							if (resul == 0) {
								file.setText(path);
								if( bottles == null)
									bottles = Program.getStorage().getAllList();
								resul = Rangement.write_CSV(file.getText().trim(), bottles);
								if (resul != -2) {
									end.setText(Program.getLabel("Infos154")); //"Export terminé."
									openit.setEnabled(true);
								}
								else {
									end.setText(Program.getError("Error129")); //"Erreur lors de l'export"
								}
							}
						}
						else {
							if (MyCellarRadioButtonXLS.isSelected()) {
								if (extension.compareToIgnoreCase(".xls") != 0 && extension.compareToIgnoreCase(".ods") != 0) {
									end.setText("");
									new Erreur(Program.getError("Error034") + " " + extension, "", true); //L'extension du fichier n'est pas CSV
									resul = 1;
								}
								if (resul == 0) {
									file.setText(path);
									if(bottles == null)
										bottles = Program.getStorage().getAllList();
									resul = Rangement.write_XLS(file.getText().trim(), bottles, false);
									if (resul != -2) {
										end.setText(Program.getLabel("Infos154")); //"Export terminé."
										openit.setEnabled(true);
									}
									else {
										end.setText(Program.getError("Error129")); //"Erreur lors de l'export"
										new Erreur(Program.getError("Error160"), Program.getError("Error161"));
									}
								}
							}
							else {
								if (MyCellarRadioButtonPDF.isSelected()) {
									if (extension.compareToIgnoreCase(".pdf") != 0) {
										end.setText("");
										new Erreur(Program.getError("Error157") + " " + extension, "", true); //L'extension du fichier n'est pas PDF
										resul = 1;
									}
									if (resul == 0) {
										file.setText(path);
										if(bottles == null)
											bottles = Program.getStorage().getAllList();
										/*int nblign = bottles.length;
                    if (nblign > 2000) {
                      more2000 = true;
                      int i = 0;
                      while (nblign > 2000 && resul != -2) {
                        Bouteille all2[] = new Bouteille[2000];
                        for (int j = 0; j < 2000; j++) {
                          all2[j] = bottles[j + i];
                        }
                        i += 2000;
                        nb_pdf++;
                        nblign -= 2000;
                        Rangement.write_XML(Program.getPreviewXMLFileName(), all2, 2000, false);
                        ExampleXML2PDF pdf = new ExampleXML2PDF();
                        try {
                          pdf.convertXML2PDF(new File(Program.getPreviewXMLFileName()), new File(fileXSL), new File(path.substring(0, path.length() - 4) + nb_pdf + ".pdf"));
                        }
                        catch (TransformerException ex) {
                          resul = -2;
                          Program.showException(ex, false);
                        }
                        catch (FOPException ex) {resul = -2;
                        Program.showException(ex, false);
                        }
                        catch (IOException ex) {resul = -2;
                        new Erreur(Program.getError("Error160"), Program.getError("Error161"));
                        }
                      }
                      if (resul != -2) {
                        Bouteille all2[] = new Bouteille[nblign];
                        for (int j = 0; j < nblign; j++) {
                          all2[j] = bottles[j + i];
                        }
                        nb_pdf++;
                        Rangement.write_XML(Program.getPreviewXMLFileName(), all2, nblign, false);
                        ExampleXML2PDF pdf = new ExampleXML2PDF();
                        try {
                          pdf.convertXML2PDF(new File(Program.getPreviewXMLFileName()), new File(fileXSL), new File(path.substring(0, path.length() - 4) + nb_pdf + ".pdf"));
                        }
                        catch (TransformerException ex) {
                          resul = -2;
                          Program.showException(ex, false);
                        }
                        catch (FOPException ex) {resul = -2;
                        Program.showException(ex, false);
                        }
                        catch (IOException ex) {resul = -2;
                        new Erreur(Program.getError("Error160"), Program.getError("Error161"));
                        }
                      }
                    }
                    else*/ {
                    	if( bottles == null)
                    		bottles = Program.getStorage().getAllList();

                    	if (resul != -2) {
                    		resul = exportToPDF(bottles, new File(path));
                    	}
                    }
                    if (resul != -2) {
                    	end.setText(Program.getLabel("Infos154")); //"Export terminé."
                    	//if (!more2000) {
                    	openit.setEnabled(true);
                    	/*}
                      else {
                        new Erreur(Program.getError("Error180"), nb_pdf + " " + Program.getError("Error181"), true,
                            Program.getError("Error033"));
                      }*/
                    }
                    else {
                    	end.setText(Program.getError("Error129")); //"Erreur lors de l'export"
                    }
									}
								}
							}
						}
					}
				}
			}
			valider.setEnabled(true);
		}
		catch (Exception e1) {
			Program.showException(e1);
		}
	}

	/**
	 * @param resul
	 * @param nomFichier
	 * @return
	 */
	public static int exportToPDF(LinkedList<Bouteille> bottles, File nomFichier) {
		int resul = 0;
		try {
			PDFTools pdf = PDFTools.createPDFFile();
			PDFProperties properties = Program.getPDFProperties();
			pdf.addTitle(properties.getTitle(), 20, properties.isBoldTitle() ? PDType1Font.HELVETICA_BOLD : PDType1Font.HELVETICA, properties.getTitleSize());
			PDFPageProperties pageProperties = new PDFPageProperties(30, 20, 20, 20, PDType1Font.HELVETICA, properties.getFontSize());
			pageProperties.setStartTop(50);
			pdf.drawTable(pageProperties, properties, Program.getPDFRows(bottles, properties), Program.getPDFHeader(properties));
			pdf.save(nomFichier);
			new Erreur(MessageFormat.format(Program.getLabel("Main.savedFile"), nomFichier.getAbsolutePath()), true);
		}
		catch (Exception ex) {resul = -2;
		new Erreur(Program.getError("Error160"), Program.getError("Error161"));
		}
		return resul;
	}

	/**
	 * param_actionPerformed: Appelle la fenêtre de paramètres.
	 */
	void param_actionPerformed() {
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

	/**
	 * <p>Titre : Cave à vin</p>
	 * <p>Description : Votre description</p>
	 * <p>Copyright : Copyright (c) 1998</p>
	 * <p>Société : Seb Informatique</p>
	 * @author Sébastien Duché
	 * @version 0.1
	 * @since 17/04/05
	 */
	class PopupListener extends MouseAdapter {

		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		public void mouseClicked(MouseEvent e) {
			maybeShowPopup(e);
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}

		private void maybeShowPopup(MouseEvent e) {
			JTextField jtf = null;
			try {
				jtf = (JTextField) e.getComponent();
				if (jtf.isEnabled() && jtf.isVisible()) {
					objet1 = e.getComponent();
				}
			}
			catch (Exception ee) {}
			;
			try {
				jtf = (JTextField) objet1;
				if (e.getButton() == MouseEvent.BUTTON3) {
					if (jtf.isFocusable() && jtf.isEnabled()) {
						jtf.requestFocus();
						if (jtf.getSelectedText() == null) {
							couper.setEnabled(false);
							copier.setEnabled(false);
						}
						else {
							couper.setEnabled(true);
							copier.setEnabled(true);
						}
						if (jtf.isEnabled() && jtf.isVisible()) {
							popup.show(e.getComponent(), e.getX(), e.getY());
						}
					}
				}
				if (e.getButton() == MouseEvent.BUTTON1) {
					if (jtf.isFocusable() && jtf.isEnabled()) {
						jtf.requestFocus();
						if (jtf.getSelectedText() == null) {
							cut.setEnabled(false);
							copy.setEnabled(false);
						}
						else {
							cut.setEnabled(true);
							copy.setEnabled(true);
						}
					}
				}
			}
			catch (Exception ee) {}
		}
	}

	@Override
	public boolean tabWillClose(TabEvent event) {
		return true;
	}

	@Override
	public void tabClosed() {
		Start.updateMainPanel();
	}

}
