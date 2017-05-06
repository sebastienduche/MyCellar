package Cave;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.LinkedList;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;

import org.apache.commons.lang.StringUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import Cave.core.MyCellarLabel;
import Cave.core.MyCellarCheckBox;
import Cave.core.MyCellarComboBox;

import net.miginfocom.swing.MigLayout;



/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2003</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 5.0
 * @since 11/01/17
 */
public class Stat extends JPanel implements ITabListener {

	private static final long serialVersionUID = -5333602919958999440L;
	private MyCellarLabel list_rangement[];
	private MyCellarLabel definition = new MyCellarLabel();
	private MyCellarLabel def2 = new MyCellarLabel();
	private MyCellarLabel end = new MyCellarLabel();
	private MyCellarLabel moy = new MyCellarLabel();
	private MyCellarComboBox<String> listOptions = new MyCellarComboBox<String>();
	private MyCellarComboBox<String> listPlaces = new MyCellarComboBox<String>();
	private JScrollPane scroll = new JScrollPane();
	private JPanel panel = new JPanel();
	private MyCellarLabel list_num_empl[];
	private MyCellarLabel list_nb_bottle[];
	private int nbparties = 0;
	private JSeparator separator[];
	private int nb_annee = 0;
	private int nb_bottle = 0;
	private double prix_total = 0;
	private String annee[] = new String[0];
	private PanelChart panelChart = new PanelChart();
	private MyCellarCheckBox options = new MyCellarCheckBox(Program.getLabel("Infos156"));
	private LinkedList<StatData> listPrice = new LinkedList<StatData>();
	private LinkedList<StatData> listYear = new LinkedList<StatData>();

	/**
	 * Stat: Constructeur.
	 *
	 */
	public Stat() {
		try {
			Debug("Stats");
			jbInit();
		}
		catch (Exception e) {
			Program.showException(e);
		}
	}

	/**
	 * jbInit: Fonction d'initialisation.
	 *
	 * @throws Exception
	 */
	private void jbInit() throws Exception {

		Debug("jbInit");
		nbparties = 99;
		definition.setText(Program.getLabel("Infos174")); //"Type de statistiques:");
		def2.setText(Program.getLabel("Infos105") + ":"); //"Rangement:");
		end.setHorizontalAlignment(4);
		moy.setHorizontalAlignment(4);
		panel.setLayout(new MigLayout("","[][][grow]",""));
		panel.setFont(Program.font_panel);
		int caveSize = Program.GetCaveLength();
		list_num_empl = new MyCellarLabel[caveSize * nbparties];
		list_nb_bottle = new MyCellarLabel[caveSize * nbparties];
		separator = new JSeparator[caveSize];
		list_rangement = new MyCellarLabel[caveSize];
		int indx = 0;
		int i=0;
		for (Rangement cave : Program.getCave()) {
			list_rangement[i] = new MyCellarLabel(cave.getNom());
			if (cave.isCaisse()) {
				panel.add(list_rangement[i]);
				if (Program.getCave(i).getNbEmplacements() == 1) {
					list_num_empl[indx] = new MyCellarLabel(Program.getLabel("Infos175")); //"1 emplacement");
				}
				else {
					list_num_empl[indx] = new MyCellarLabel(cave.getNbEmplacements() + " " + Program.getLabel("Infos176")); //emplacements
				}
				if (Program.getCave(i).getNbCaseUseAll() <= 1) {
					list_nb_bottle[indx] = new MyCellarLabel(cave.getNbCaseUseAll() + " " + Program.getLabel("Infos177")); //"bouteille");
				}
				else {
					list_nb_bottle[indx] = new MyCellarLabel(cave.getNbCaseUseAll() + " " + Program.getLabel("Infos178")); //"bouteilles");
				}
				nb_bottle += cave.getNbCaseUseAll();
				panel.add(list_num_empl[indx]);
				panel.add(list_nb_bottle[indx], "align right, wrap");
				separator[i] = new JSeparator();
				panel.add(separator[i], "span 3, grow, wrap");
				indx++;
			}
			else {
				panel.add(list_rangement[i]);

				if (cave.getNbEmplacements() == 1) {
					list_num_empl[indx] = new MyCellarLabel(Program.getLabel("Infos175")); //"1 emplacement");
				}
				else {
					list_num_empl[indx] = new MyCellarLabel(cave.getNbEmplacements() + " " + Program.getLabel("Infos176")); //"emplacements");
				}
				if (cave.getNbCaseUseAll() <= 1) {
					list_nb_bottle[indx] = new MyCellarLabel(cave.getNbCaseUseAll() + " " + Program.getLabel("Infos177")); //"bouteille");
				}
				else {
					list_nb_bottle[indx] = new MyCellarLabel(cave.getNbCaseUseAll() + " " + Program.getLabel("Infos178")); //"bouteilles");
				}
				nb_bottle += cave.getNbCaseUseAll();
				panel.add(list_num_empl[indx]);
				panel.add(list_nb_bottle[indx],"align right, wrap");
				indx++;
				for (int j = 0; j < cave.getNbEmplacements(); j++) {
					list_num_empl[indx] = new MyCellarLabel(Program.getLabel("Infos179") + (j + 1)); //Emplacement n°
					if (cave.getNbCaseUseAll() <= 1) {
						list_nb_bottle[indx] = new MyCellarLabel(cave.getNbCaseUseAll() + " " + Program.getLabel("Infos177")); //"bouteille");
					}
					else {
						list_nb_bottle[indx] = new MyCellarLabel(cave.getNbCaseUse(j) + " " + Program.getLabel("Infos178")); //"bouteilles");
					}
					panel.add(list_num_empl[indx]);
					panel.add(list_nb_bottle[indx], "span 2, align right, wrap");
					indx++;
				}
				separator[i] = new JSeparator();
				panel.add(separator[i], "wrap, span 3, grow");
			}
			i++;
		}
		if (nb_bottle > 1) {
			moy.setText("");
			end.setText(Program.getLabel("Infos181") + " " + nb_bottle); //Nombre de bouteille total:
		}
		else {
			moy.setText("");
			end.setText(Program.getLabel("Infos180") + " " + nb_bottle); //Nombre de bouteilles totales:
		}

		options.addActionListener((e) -> options_actionPerformed(e));

		listPlaces.removeAllItems();
		listPlaces.addItem(Program.getLabel("Infos182")); //"Tous les rangement");
		for (Rangement cave : Program.getCave()) {
			listPlaces.addItem(cave.getNom());
		}
		listOptions.removeAllItems();
		listOptions.addItem(Program.getLabel("Infos183")); //"Par Rangement");
		listOptions.addItem(Program.getLabel("Infos184")); //"Par Année");
		listOptions.addItem(Program.getLabel("Infos185")); //"Par Prix");

		scroll = new JScrollPane(panel);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		listOptions.addItemListener((e) -> list_itemStateChanged(e));
		listPlaces.addItemListener((e) -> {
			if(e.getSource() == listPlaces && e.getStateChange() == ItemEvent.SELECTED)
				list2_itemStateChanged(e);
		});

		this.setLayout(new MigLayout("","[][][grow]", "[][]20px[grow][][]"));
		this.add(definition);
		this.add(listOptions, "wrap");
		this.add(def2);
		this.add(listPlaces, "wrap");
		this.add(scroll, "span 3, split 2, grow");
		this.add(panelChart, "grow, wrap");
		this.add(options);
		this.add(end, "span 2, align right, wrap");
		this.add(moy, "span 3, align right, wrap");
		options.setEnabled(false);

		Debug("jbInit OK");
	}

	/**
	 * list_itemStateChanged: Fonction appellé lors d'un changement dans la
	 * première liste.
	 *
	 * @param e ItemEvent
	 */
	void list_itemStateChanged(ItemEvent e) {
		try {
			if (listOptions.getSelectedIndex() == 0) {
				Debug("By place");
				panelChart.setPlacesChart(Program.getCave());
				def2.setText(Program.getLabel("Infos105") + ":"); //"Rangement:");
				listPlaces.removeAllItems();
				listPlaces.setEnabled(true);
				listPlaces.addItem(Program.getLabel("Infos182")); //"Tous les rangements");
				listPlaces.setSelectedIndex(0);
				for (int i = 0; i < Program.GetCaveLength(); i++) {
					listPlaces.addItem(Program.getCave(i).getNom());
				}
			}
			if (listOptions.getSelectedIndex() == 1) {
				def2.setText("");
				Debug("By year");
				Object obj[] = Program.getStorage().getAnneeList().keySet().toArray();
				nb_annee = obj.length;
				annee = new String[obj.length];
				for (int i = 0; i < annee.length; i++) {
					annee[i] = obj[i].toString();
				}
				java.util.Arrays.sort(annee, java.text.Collator.getInstance());
				listPlaces.removeAllItems();
				listPlaces.addItem(Program.getLabel("Infos186")); //"Toutes les années");
				listPlaces.setSelectedIndex(0);
				listPlaces.setEnabled(false);
			}
			if (listOptions.getSelectedIndex() == 2) {
				Debug("By price");
				def2.setText(Program.getLabel("Infos187")); //"Tranche de prix:");
				listPlaces.removeAllItems();
				listPlaces.addItem(Program.getLabel("Infos188")); //"Toutes les tranches");
				listPlaces.addItem(Program.getLabel("Infos299")); //"Tranches avec bouteilles");
			}
		}
		catch (Exception exc) {
			Program.showException(exc);
		}
	}

	/**
	 * list2_itemStateChanged: Fonction appellé lors d'un changement dans la
	 * seconde liste.
	 *
	 * @param e ItemEvent
	 */
	void list2_itemStateChanged(ItemEvent e) {

		try {
			if (listOptions.getSelectedIndex() == 0) {
				if (listPlaces.getSelectedIndex() == 0) {
					Debug("All places");
					panelChart.setPlacesChart(Program.getCave());
					panel.removeAll();
					panel.repaint();
					panel.setFont(Program.font_panel);

					nb_bottle = 0;
					list_rangement = new MyCellarLabel[Program.GetCaveLength()];
					list_num_empl = new MyCellarLabel[Program.GetCaveLength() * nbparties];
					list_nb_bottle = new MyCellarLabel[Program.GetCaveLength() * nbparties];
					int indx = 0;
					int i = 0;
					for (Rangement cave : Program.getCave()) {
						list_rangement[i] = new MyCellarLabel(cave.getNom());
						panel.add(list_rangement[i]);
						if (cave.isCaisse()) {
							if (cave.getNbEmplacements() == 1) {
								list_num_empl[indx] = new MyCellarLabel(Program.getLabel("Infos175")); //"1 emplacement");
							}
							else {
								list_num_empl[indx] = new MyCellarLabel(cave.getNbEmplacements() + " " + Program.getLabel("Infos176")); //"emplacements");
							}
							if (cave.getNbCaseUseAll() <= 1) {
								list_nb_bottle[indx] = new MyCellarLabel(cave.getNbCaseUseAll() + " " + Program.getLabel("Infos177")); //"bouteille");
							}
							else {
								list_nb_bottle[indx] = new MyCellarLabel(cave.getNbCaseUseAll() + " " + Program.getLabel("Infos178")); //"bouteilles");
							}
							nb_bottle += cave.getNbCaseUseAll();
							panel.add(list_num_empl[indx]);
							panel.add(list_nb_bottle[indx], "span 2, align right, wrap");
							separator[i] = new JSeparator();
							panel.add(separator[i], "span 3, wrap");
							indx++;
						}
						else {
							if (cave.getNbEmplacements() == 1) {
								list_num_empl[indx] = new MyCellarLabel(Program.getLabel("Infos175")); //"1 emplacement");
							}
							else {
								list_num_empl[indx] = new MyCellarLabel(cave.getNbEmplacements() + " " + Program.getLabel("Infos176")); //"emplacements");
							}
							if (cave.getNbCaseUseAll() <= 1) {
								list_nb_bottle[indx] = new MyCellarLabel(cave.getNbCaseUseAll() + " " + Program.getLabel("Infos177")); //"bouteille");
							}
							else {
								list_nb_bottle[indx] = new MyCellarLabel(cave.getNbCaseUseAll() + " " + Program.getLabel("Infos178")); //"bouteilles");
							}
							nb_bottle += cave.getNbCaseUseAll();
							panel.add(list_num_empl[indx]);
							panel.add(list_nb_bottle[indx], "span 2, align right, wrap");
							indx++;
							for (int j = 0; j < cave.getNbEmplacements(); j++) {
								list_num_empl[indx] = new MyCellarLabel(Program.getLabel("Infos179") + (j + 1)); //Emplacement n°
								if (cave.getNbCaseUseAll() <= 1) {
									list_nb_bottle[indx] = new MyCellarLabel(cave.getNbCaseUseAll() + " " + Program.getLabel("Infos177")); //"bouteille");
								}
								else {
									list_nb_bottle[indx] = new MyCellarLabel(cave.getNbCaseUse(j) + " " + Program.getLabel("Infos178")); //"bouteilles");
								}
								panel.add(list_num_empl[indx]);
								panel.add(list_nb_bottle[indx],"span 2, align right, wrap");
								indx++;
							}
							separator[i] = new JSeparator();
							panel.add(separator[i], "span 3, wrap");
						}
						i++;
					}
					if (nb_bottle > 1) {
						moy.setText("");
						end.setText(Program.getLabel("Infos181") + " " + nb_bottle);
					}
					else {
						moy.setText("");
						end.setText(Program.getLabel("Infos180") + " " + nb_bottle);
					}
				}
				else {
					Debug("One place");
					panel.removeAll();
					panel.repaint();
					panel.setFont(Program.font_panel);
					end.setText("");
					moy.setText("");
					options.setEnabled(false);
					list_rangement = new MyCellarLabel[1];
					list_num_empl = new MyCellarLabel[Program.GetCaveLength() * nbparties];
					list_nb_bottle = new MyCellarLabel[Program.GetCaveLength() * nbparties];
					int indx = 0;
					int index = 0;
					if (listPlaces.getSelectedIndex() > 1) {
						index = listPlaces.getSelectedIndex() - 1;
					}
					int i = 0;
					panelChart.setPlaceChart(Program.getCave(index));
					nb_bottle = Program.getCave(index).getNbCaseUseAll();
					list_rangement[i] = new MyCellarLabel(Program.getCave(index).getNom());
					panel.add(list_rangement[i]);
					if (Program.getCave(index).isCaisse()) {
						if (Program.getCave(index).getNbEmplacements() == 1) {
							list_num_empl[indx] = new MyCellarLabel(Program.getLabel("Infos175")); //"1 emplacement");
						}
						else {
							list_num_empl[indx] = new MyCellarLabel(Program.getCave(index).getNbEmplacements() + " " + Program.getLabel("Infos176")); //"emplacements");
						}
						if (Program.getCave(index).getNbCaseUseAll() <= 1) {
							list_nb_bottle[indx] = new MyCellarLabel(Program.getCave(index).getNbCaseUseAll() + " " + Program.getLabel("Infos177")); //"bouteille");
						}
						else {
							list_nb_bottle[indx] = new MyCellarLabel(Program.getCave(index).getNbCaseUseAll() + " " + Program.getLabel("Infos178")); //"bouteilles");
						}
						panel.add(list_num_empl[indx]);
						panel.add(list_nb_bottle[indx], "span 2, align right, wrap");
						separator[i] = new JSeparator();
						panel.add(separator[i], "span 3, wrap");
						indx++;
					}
					else {
						if (Program.getCave(index).getNbEmplacements() == 1) {
							list_num_empl[indx] = new MyCellarLabel(Program.getLabel("Infos175")); //"1 emplacement");
						}
						else {
							list_num_empl[indx] = new MyCellarLabel(Program.getCave(index).getNbEmplacements() + " " + Program.getLabel("Infos176")); //"emplacements");
						}
						if (Program.getCave(index).getNbCaseUseAll() <= 1) {
							list_nb_bottle[indx] = new MyCellarLabel(Program.getCave(index).getNbCaseUseAll() + " " + Program.getLabel("Infos177")); //"bouteille");
						}
						else {
							list_nb_bottle[indx] = new MyCellarLabel(Program.getCave(index).getNbCaseUseAll() + " " + Program.getLabel("Infos178")); //"bouteilles");
						}
						panel.add(list_num_empl[indx]);
						panel.add(list_nb_bottle[indx], "span 2, align right, wrap");
						indx++;
						for (int j = 0; j < Program.getCave(index).getNbEmplacements(); j++) {
							list_num_empl[indx] = new MyCellarLabel(Program.getLabel("Infos179") + (j + 1));
							if (Program.getCave(index).getNbCaseUseAll() <= 1) {
								list_nb_bottle[indx] = new MyCellarLabel(Program.getCave(index).getNbCaseUseAll() + " " + Program.getLabel("Infos177")); //"bouteille");
							}
							else {
								list_nb_bottle[indx] = new MyCellarLabel(Program.getCave(index).getNbCaseUse(j) + " " + Program.getLabel("Infos178")); //"bouteilles");
							}
							panel.add(list_num_empl[indx]);
							panel.add(list_nb_bottle[indx],"span 2, align right, wrap");
							indx++;
						}
						separator[i] = new JSeparator();
						panel.add(separator[i], "span 3, wrap");
					}
					end.setText(Program.getLabel("Infos136") + ": " + nb_bottle);
				}
			}
			else if (listOptions.getSelectedIndex() == 1) { //Par Année
				Debug("By year");
				panel.removeAll();
				panel.setFont(Program.font_panel);
				options.setEnabled(false);
				moy.setText("");
				list_rangement = new MyCellarLabel[nb_annee + 2];
				list_num_empl = new MyCellarLabel[nb_annee + 2];
				list_nb_bottle = new MyCellarLabel[nb_annee + 2];
				if(listYear.isEmpty()) {
    				nb_bottle = 0;
    				for (int i = 0; i < nb_annee; i++) {
    					int v = Integer.parseInt(annee[i].trim());
    					if ( v > 1000 && v < 9000) {
    						if (annee[i] != null) {
    							int count = Program.getStorage().getNbBouteilleAnnee(Integer.parseInt(annee[i].trim()));
    							nb_bottle += count;
    							listYear.add(new StatData(annee[i], count));
    						}
    					}
    				}
    				int nb_autre = Program.getStorage().getNbNonVintage();
    
    				nb_bottle += nb_autre;
    				listYear.add(new StatData(Program.getLabel("Infos390"), nb_autre));
    				
    				nb_autre = Program.getStorage().getNbAutreAnnee();
    
    				nb_bottle += nb_autre;
    				listYear.add(new StatData(Program.getLabel("Infos225"), nb_autre));
				}
				for(StatData data: listYear)
				{
					panel.add(new MyCellarLabel(data.getName()));
					if (data.getCount() <= 1)
						panel.add(new MyCellarLabel(data.getCount() + " " + Program.getLabel("Infos177")), "span 2, align right, wrap"); //"bouteille");
					else
						panel.add(new MyCellarLabel(data.getCount() + " " + Program.getLabel("Infos178")), "span 2, align right, wrap"); //"bouteilles")
				}
				panel.updateUI();
				panelChart.setDataChart(listYear, Program.getLabel("Infos184"));
				end.setText(Program.getLabel("Infos136") + ": " + nb_bottle);
			}
			else if (listOptions.getSelectedIndex() == 2) {
				//Par prix
				boolean all_bracket = true;
				listPlaces.setEnabled(true);
				if (listPlaces.getSelectedIndex() == 1) {
					all_bracket = false;
				}
				panel.removeAll();
				panel.repaint();
				panel.setFont(Program.font_panel);

				options.setEnabled(true);
				int tranche = Program.getCaveConfigInt("TRANCHE_PRIX", 50);

				if (tranche <= 0) {
					tranche = 50;
					Program.putCaveConfigInt("TRANCHE_PRIX", 50);
				}
				list_rangement = new MyCellarLabel[Bouteille.prix_max + tranche + 1];
				list_num_empl = new MyCellarLabel[Bouteille.prix_max + tranche + 1];
				list_nb_bottle = new MyCellarLabel[Bouteille.prix_max + tranche + 1];
				int nb_tot = 0;
				int nb_prix[] = new int[10000];
				int ss_prix = 0;
				char virgule;
				String sVirgule;
				if( Program.hasConfigCaveKey("PRICE_SEPARATOR")) {
					sVirgule = Program.getCaveConfigString("PRICE_SEPARATOR","");
					virgule = sVirgule.charAt(0);
				}
				else
				{
					java.text.DecimalFormat df = new java.text.DecimalFormat();
					virgule = df.getDecimalFormatSymbols().getDecimalSeparator();
				}
				if(listPrice.isEmpty())
				{
					prix_total = 0;
					java.math.BigDecimal bd = null;
					for (int j = 0; j < Program.getStorage().getAllNblign(); j++) {
						int prix_int = 0;
						if (Program.getStorage().getAllAt(j) != null) {
							String prix = Program.getStorage().getAllAt(j).getPrix();
							prix = Program.convertStringFromHTMLString(prix);
							if (virgule == '.') {
								prix = prix.replace(',', ' ');
							}
							if (virgule == ',') {
								prix = prix.replace('.', ' ');
								prix = prix.replace(',', '.');
							}
							int index = prix.indexOf(' ');
							while (index != -1) {
								prix = prix.substring(0, index) + prix.substring(index + 1);
								index = prix.indexOf(' ');
							}
							boolean isNumeric = true;
							try {
								bd = new java.math.BigDecimal(prix);
								bd = bd.setScale(2, java.math.BigDecimal.ROUND_HALF_UP);
							}
							catch (NumberFormatException nfe) {isNumeric = false;
							ss_prix++;
							}
							if (isNumeric) {
								try {
									prix_int = bd.intValue();
									nb_prix[prix_int]++;
								}
								catch (IndexOutOfBoundsException ioobe) {
									int nb_prix_tmp[] = new int[prix_int * 2];
									for (int z = 0; z < nb_prix.length; z++) {
										nb_prix_tmp[z] = nb_prix[z];
									}
									nb_prix = nb_prix_tmp;
									nb_prix[prix_int]++;
								}
								prix_total += bd.doubleValue();
							}
						}
					}
					int i = 0;
					for (i = 0; i <= Bouteille.prix_max; i += tranche) {
						String label = Program.getLabel("Infos190") + " " + i + " " + Program.getLabel("Infos191") + " " + (i + tranche - 1) + " " +
								Program.getCaveConfigString("DEVISE", "");
						int nb = 0;
						for (int j = i; j < (i + tranche); j++) {
							try {
								nb += nb_prix[j];
							}
							catch (ArrayIndexOutOfBoundsException ex) {
								int nb_prix_tmp[] = new int[j * 2];
								for (int z = 0; z < nb_prix.length; z++) {
									nb_prix_tmp[z] = nb_prix[z];
								}
								nb_prix = nb_prix_tmp;
								nb += nb_prix[j];
							}
						}
						nb_tot += nb;
						if (all_bracket || nb > 0)
							listPrice.add(new StatData(label, nb));
					}
					listPrice.add(new StatData(Program.getLabel("Infos192"), ss_prix));
				}
				for(StatData price: listPrice)
				{
					if(all_bracket || price.getCount() > 0) {
						panel.add(new MyCellarLabel(price.getName()));
						if(price.getCount() > 1)
							panel.add(new MyCellarLabel(price.getCount() + " " + Program.getLabel("Infos178")), "span 2, align right, wrap");
						else
							panel.add(new MyCellarLabel(price.getCount() + " " + Program.getLabel("Infos177")), "span 2, align right, wrap");
					}

				}
				panel.updateUI();
				end.setText(Program.getLabel("Infos244") + " " + (int) prix_total + " " + Program.getCaveConfigString("DEVISE",""));
				if (nb_tot > 0)
					moy.setText(Program.getLabel("Infos300") + " " + (int) (prix_total / nb_tot) + " " + Program.getCaveConfigString("DEVISE",""));
				panelChart.setDataChart(listPrice, Program.getLabel("Infos185"));
			}
		}
		catch (Exception exc) {
			Program.showException(exc);
		}
	}

	/**
	 * options_actionPerformed: Appel de la fenètre d'options.
	 *
	 * @param e ActionEvent
	 */
	void options_actionPerformed(ActionEvent e) {
		try {
			Debug("options_actionPerforming...");
			options.setSelected(false);
			String value = JOptionPane.showInputDialog(this, Program.getLabel("Infos194") +"\n"+ Program.getLabel("Infos195") + " " + Program.getCaveConfigString("DEVISE","") + " ):", Program.getCaveConfigString("TRANCHE_PRIX",""));
			if(value != null && StringUtils.isNumeric(value)) {
				Program.putCaveConfigInt("TRANCHE_PRIX", new Integer(value));
				listPrice.clear();
				list2_itemStateChanged(null);
			}
		}
		catch (Exception exc) {
			Program.showException(exc);
		}
	}

	/**
	 * Debug
	 *
	 * @param sText String
	 */
	public static void Debug(String sText) {
		Program.Debug("Stat: " + sText );
	}

	@Override
	public boolean tabWillClose(TabEvent event) {
		return true;
	}

	@Override
	public void tabClosed() {
		Start.updateMainPanel();
	}

	class PanelChart extends JPanel {

		private static final long serialVersionUID = -6697139633950076186L;

		public PanelChart(){
			this.setLayout(new MigLayout("","grow","grow"));
			setPlacesChart(Program.getCave());
		}


		/** * Creates a chart */

		public void setPlacesChart(LinkedList<Rangement> rangements) {

			DefaultPieDataset dataset = new DefaultPieDataset();
			for(Rangement rangement: rangements) {
				if(rangement == null)
					continue;
				dataset.setValue(rangement.getNom(), rangement.getNbCaseUseAll());
			}
			JFreeChart chart = ChartFactory.createPieChart(Program.getLabel("Infos182"),          // chart title
					dataset,                // data
					false,                   // include legend
					true,
					false);

			ChartPanel chartPanel = new ChartPanel(chart);
			this.removeAll();
			this.add(chartPanel, "grow");
		}

		public void setPlaceChart(Rangement rangement) {

			this.removeAll();
			if(rangement.isCaisse())
				return;
			DefaultPieDataset dataset = new DefaultPieDataset();
			for(Part part: rangement.getPlace()){
				dataset.setValue(Program.getLabel("Infos179")+part.getNum(), rangement.getNbCaseUse(part.getNum()-1));
			}
			JFreeChart chart = ChartFactory.createPieChart(rangement.getNom(),          // chart title
					dataset,                // data
					false,                   // include legend
					true,
					false);

			ChartPanel chartPanel = new ChartPanel(chart);
			this.add(chartPanel, "grow");
		}

		public void setDataChart(LinkedList<StatData> datas, String title) {

			this.removeAll();
			DefaultPieDataset dataset = new DefaultPieDataset();
			for(StatData part: datas) {
				if(part.getCount() > 0)
					dataset.setValue(part.getName(), part.getCount());
			}
			JFreeChart chart = ChartFactory.createPieChart(title,          // chart title
					dataset,                // data
					false,                   // include legend
					true,
					false);

			ChartPanel chartPanel = new ChartPanel(chart);
			this.add(chartPanel, "grow");
			this.updateUI();

		}

	}

	class StatData {

		private String name;
		private int count;


		public StatData(String name, int count) {
			super();
			this.name = name;
			this.count = count;
		}

		public String getName() {
			return name;
		}
		public int getCount() {
			return count;
		}


	}

	public void updateView() {
		listYear.clear();
		listPrice.clear();
		list2_itemStateChanged(null);
	}

}
