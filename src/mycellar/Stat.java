package mycellar;

import mycellar.core.MyCellarCheckBox;
import mycellar.core.MyCellarComboBox;
import mycellar.core.MyCellarLabel;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang.StringUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.math.BigDecimal;
import java.text.Collator;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2003</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 5.5
 * @since 02/03/18
 */
public class Stat extends JPanel implements ITabListener {

	private static final long serialVersionUID = -5333602919958999440L;
	private MyCellarLabel list_rangement[];
	private final MyCellarLabel definition = new MyCellarLabel();
	private final MyCellarLabel def2 = new MyCellarLabel();
	private final MyCellarLabel end = new MyCellarLabel();
	private final MyCellarLabel moy = new MyCellarLabel();
	private final MyCellarComboBox<String> listOptions = new MyCellarComboBox<>();
	private final MyCellarComboBox<String> listPlaces = new MyCellarComboBox<>();
	private final JPanel panel = new JPanel();
	private MyCellarLabel list_num_empl[];
	private MyCellarLabel list_nb_bottle[];
	private int nbparties = 0;
	private JSeparator separator[];
	private int nb_annee = 0;
	private int nb_bottle = 0;
	private double prix_total = 0;
	private String annee[] = new String[0];
	private final PanelChart panelChart = new PanelChart();
	private final MyCellarCheckBox options = new MyCellarCheckBox(Program.getLabel("Infos156"));
	private final List<StatData> listPrice = new LinkedList<>();
	private final List<StatData> listYear = new LinkedList<>();

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
		end.setHorizontalAlignment(SwingConstants.RIGHT);
		moy.setHorizontalAlignment(SwingConstants.RIGHT);
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
				if (cave.getNbEmplacements() == 1) {
					list_num_empl[indx] = new MyCellarLabel(Program.getLabel("Infos175")); //"1 emplacement");
				}
				else {
					list_num_empl[indx] = new MyCellarLabel(MessageFormat.format(Program.getLabel("Infos176"), cave.getNbEmplacements())); //emplacements
				}
				if (cave.getNbCaseUseAll() <= 1) {
					list_nb_bottle[indx] = new MyCellarLabel(cave.getNbCaseUseAll() + " " + Program.getLabel("Infos177")); //"bouteille");
				}
				else {
					list_nb_bottle[indx] = new MyCellarLabel(MessageFormat.format(Program.getLabel("Infos161"), cave.getNbCaseUseAll())); //"bouteilles");
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
					list_num_empl[indx] = new MyCellarLabel(MessageFormat.format(Program.getLabel("Infos176"), cave.getNbEmplacements())); //"emplacements");
				}
				if (cave.getNbCaseUseAll() <= 1) {
					list_nb_bottle[indx] = new MyCellarLabel(cave.getNbCaseUseAll() + " " + Program.getLabel("Infos177")); //"bouteille");
				}
				else {
					list_nb_bottle[indx] = new MyCellarLabel(MessageFormat.format(Program.getLabel("Infos161"), cave.getNbCaseUseAll())); //"bouteilles");
				}
				nb_bottle += cave.getNbCaseUseAll();
				panel.add(list_num_empl[indx]);
				panel.add(list_nb_bottle[indx],"align right, wrap");
				indx++;
				for (int j = 0; j < cave.getNbEmplacements(); j++) {
					list_num_empl[indx] = new MyCellarLabel(MessageFormat.format(Program.getLabel("Infos179"), (j + 1))); //Emplacement n°
					if (cave.getNbCaseUseAll() <= 1) {
						list_nb_bottle[indx] = new MyCellarLabel(cave.getNbCaseUseAll() + " " + Program.getLabel("Infos177")); //"bouteille");
					}
					else {
						list_nb_bottle[indx] = new MyCellarLabel(MessageFormat.format(Program.getLabel("Infos161"), cave.getNbCaseUse(j))); //"bouteilles");
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
		moy.setText("");
		if (nb_bottle > 1) {
			end.setText(MessageFormat.format(Program.getLabel("Infos181"), nb_bottle)); //Nombre de bouteille total:
		}
		else {
			end.setText(MessageFormat.format(Program.getLabel("Infos180"), nb_bottle)); //Nombre de bouteilles totales:
		}

		options.addActionListener(this::options_actionPerformed);

		listPlaces.removeAllItems();
		listPlaces.addItem(Program.getLabel("Infos182")); //"Tous les rangement");
		for (Rangement cave : Program.getCave()) {
			listPlaces.addItem(cave.getNom());
		}
		listOptions.removeAllItems();
		listOptions.addItem(Program.getLabel("Infos183")); //"Par Rangement");
		listOptions.addItem(Program.getLabel("Infos184")); //"Par Année");
		listOptions.addItem(Program.getLabel("Infos185")); //"Par Prix");

		JScrollPane scroll = new JScrollPane(panel);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		listOptions.addItemListener(this::list_itemStateChanged);
		listPlaces.addItemListener((e) -> {
			if(e.getSource().equals(listPlaces) && e.getStateChange() == ItemEvent.SELECTED)
				list2_itemStateChanged(e);
		});

		setLayout(new MigLayout("","[][][grow]", "[][]20px[grow][][]"));
		add(definition);
		add(listOptions, "wrap");
		add(def2);
		add(listPlaces, "wrap");
		add(scroll, "span 3, split 2, grow");
		add(panelChart, "grow, wrap");
		add(options);
		add(end, "span 2, align right, wrap");
		add(moy, "span 3, align right, wrap");
		options.setEnabled(false);

		Debug("jbInit OK");
	}

	/**
	 * list_itemStateChanged: Fonction appellé lors d'un changement dans la
	 * première liste.
	 *
	 * @param e ItemEvent
	 */
	private void list_itemStateChanged(ItemEvent e) {
		try {
			if (listOptions.getSelectedIndex() == 0) {
				Debug("By place");
				panelChart.setPlacesChart(Program.getCave());
				def2.setText(Program.getLabel("Infos105") + ":"); //"Rangement:");
				listPlaces.removeAllItems();
				listPlaces.setEnabled(true);
				listPlaces.addItem(Program.getLabel("Infos182")); //"Tous les rangements");
				listPlaces.setSelectedIndex(0);
				for (Rangement r : Program.getCave()) {
					listPlaces.addItem(r.getNom());
				}
			}
			if (listOptions.getSelectedIndex() == 1) {
				def2.setText("");
				Debug("By year");
				int obj[] = Program.getAnnees();
				nb_annee = obj.length;
				annee = new String[obj.length];
				for (int i = 0; i < annee.length; i++) {
					annee[i] = Integer.toString(obj[i]);
				}
				Arrays.sort(annee, Collator.getInstance());
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
	private void list2_itemStateChanged(ItemEvent e) {

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
								list_num_empl[indx] = new MyCellarLabel(MessageFormat.format(Program.getLabel("Infos176"), cave.getNbEmplacements())); //"emplacements");
							}
							if (cave.getNbCaseUseAll() <= 1) {
								list_nb_bottle[indx] = new MyCellarLabel(cave.getNbCaseUseAll() + " " + Program.getLabel("Infos177")); //"bouteille");
							}
							else {
								list_nb_bottle[indx] = new MyCellarLabel(MessageFormat.format(Program.getLabel("Infos161"),cave.getNbCaseUseAll())); //"bouteilles");
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
								list_num_empl[indx] = new MyCellarLabel(MessageFormat.format(Program.getLabel("Infos176"), cave.getNbEmplacements())); //"emplacements");
							}
							if (cave.getNbCaseUseAll() <= 1) {
								list_nb_bottle[indx] = new MyCellarLabel(cave.getNbCaseUseAll() + " " + Program.getLabel("Infos177")); //"bouteille");
							}
							else {
								list_nb_bottle[indx] = new MyCellarLabel(MessageFormat.format(Program.getLabel("Infos161"),cave.getNbCaseUseAll())); //"bouteilles");
							}
							nb_bottle += cave.getNbCaseUseAll();
							panel.add(list_num_empl[indx]);
							panel.add(list_nb_bottle[indx], "span 2, align right, wrap");
							indx++;
							for (int j = 0; j < cave.getNbEmplacements(); j++) {
								list_num_empl[indx] = new MyCellarLabel(MessageFormat.format(Program.getLabel("Infos179"), (j + 1))); //Emplacement n°
								if (cave.getNbCaseUseAll() <= 1) {
									list_nb_bottle[indx] = new MyCellarLabel(cave.getNbCaseUseAll() + " " + Program.getLabel("Infos177")); //"bouteille");
								}
								else {
									list_nb_bottle[indx] = new MyCellarLabel(MessageFormat.format(Program.getLabel("Infos161"), cave.getNbCaseUse(j))); //"bouteilles");
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
					moy.setText("");
					if (nb_bottle > 1) {
						end.setText(MessageFormat.format(Program.getLabel("Infos181"), nb_bottle));
					}
					else {
						end.setText(MessageFormat.format(Program.getLabel("Infos180"), nb_bottle));
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
					Rangement cave = Program.getCave(index);
					panelChart.setPlaceChart(cave);
					nb_bottle = cave.getNbCaseUseAll();
					list_rangement[i] = new MyCellarLabel(cave.getNom());
					panel.add(list_rangement[i]);
					if (cave.isCaisse()) {
						if (cave.getNbEmplacements() == 1) {
							list_num_empl[indx] = new MyCellarLabel(Program.getLabel("Infos175")); //"1 emplacement");
						}
						else {
							list_num_empl[indx] = new MyCellarLabel(MessageFormat.format(Program.getLabel("Infos176"), cave.getNbEmplacements())); //"emplacements");
						}
						if (cave.getNbCaseUseAll() <= 1) {
							list_nb_bottle[indx] = new MyCellarLabel(cave.getNbCaseUseAll() + " " + Program.getLabel("Infos177")); //"bouteille");
						}
						else {
							list_nb_bottle[indx] = new MyCellarLabel(MessageFormat.format(Program.getLabel("Infos161"), cave.getNbCaseUseAll())); //"bouteilles");
						}
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
							list_num_empl[indx] = new MyCellarLabel(MessageFormat.format(Program.getLabel("Infos176"), cave.getNbEmplacements())); //"emplacements");
						}
						if (cave.getNbCaseUseAll() <= 1) {
							list_nb_bottle[indx] = new MyCellarLabel(cave.getNbCaseUseAll() + " " + Program.getLabel("Infos177")); //"bouteille");
						}
						else {
							list_nb_bottle[indx] = new MyCellarLabel(MessageFormat.format(Program.getLabel("Infos161"), cave.getNbCaseUseAll())); //"bouteilles");
						}
						panel.add(list_num_empl[indx]);
						panel.add(list_nb_bottle[indx], "span 2, align right, wrap");
						indx++;
						for (int j = 0; j < cave.getNbEmplacements(); j++) {
							list_num_empl[indx] = new MyCellarLabel(MessageFormat.format(Program.getLabel("Infos179"), (j + 1)));
							if (cave.getNbCaseUseAll() <= 1) {
								list_nb_bottle[indx] = new MyCellarLabel(cave.getNbCaseUseAll() + " " + Program.getLabel("Infos177")); //"bouteille");
							}
							else {
								list_nb_bottle[indx] = new MyCellarLabel(MessageFormat.format(Program.getLabel("Infos161"), cave.getNbCaseUse(j))); //"bouteilles");
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
    							int count = Program.getNbBouteilleAnnee(Integer.parseInt(annee[i].trim()));
    							nb_bottle += count;
    							listYear.add(new StatData(annee[i], count));
    						}
    					}
    				}
    				int nb_autre = Program.getNbNonVintage();
    
    				nb_bottle += nb_autre;
    				listYear.add(new StatData(Program.getLabel("Infos390"), nb_autre));
    				
    				nb_autre = Program.getNbAutreAnnee();
    
    				nb_bottle += nb_autre;
    				listYear.add(new StatData(Program.getLabel("Infos225"), nb_autre));
				}
				for(StatData data: listYear)
				{
					panel.add(new MyCellarLabel(data.getName()));
					if (data.getCount() <= 1)
						panel.add(new MyCellarLabel(data.getCount() + " " + Program.getLabel("Infos177")), "span 2, align right, wrap"); //"bouteille");
					else
						panel.add(new MyCellarLabel(MessageFormat.format(Program.getLabel("Infos161"), data.getCount())), "span 2, align right, wrap"); //"bouteilles")
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
				else {
					DecimalFormat df = new DecimalFormat();
					virgule = df.getDecimalFormatSymbols().getDecimalSeparator();
				}
				if(listPrice.isEmpty())
				{
					prix_total = 0;
					BigDecimal bd = null;
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
								bd = new BigDecimal(prix).setScale(2, BigDecimal.ROUND_HALF_UP);
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
						String label = MessageFormat.format(Program.getLabel("Infos190"), i, (i + tranche - 1), Program.getCaveConfigString("DEVISE", ""));
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
							panel.add(new MyCellarLabel(MessageFormat.format(Program.getLabel("Infos161"), price.getCount())), "span 2, align right, wrap");
						else
							panel.add(new MyCellarLabel(price.getCount() + " " + Program.getLabel("Infos177")), "span 2, align right, wrap");
					}

				}
				panel.updateUI();
				end.setText(MessageFormat.format(Program.getLabel("Infos244"),(int) prix_total, Program.getCaveConfigString("DEVISE","")));
				if (nb_tot > 0)
					moy.setText(MessageFormat.format(Program.getLabel("Infos300"),(int) (prix_total / nb_tot), Program.getCaveConfigString("DEVISE","")));
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
	private void options_actionPerformed(ActionEvent e) {
		try {
			Debug("options_actionPerforming...");
			options.setSelected(false);
			String value = JOptionPane.showInputDialog(this, Program.getLabel("Infos194"));
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

	private class PanelChart extends JPanel {

		private static final long serialVersionUID = -6697139633950076186L;

		private PanelChart(){
			setLayout(new MigLayout("","grow","grow"));
			setPlacesChart(Program.getCave());
		}


		/** * Creates a chart */

		private void setPlacesChart(List<Rangement> rangements) {

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
			removeAll();
			add(chartPanel, "grow");
		}

		private void setPlaceChart(Rangement rangement) {

			removeAll();
			if(rangement.isCaisse())
				return;
			DefaultPieDataset dataset = new DefaultPieDataset();
			for(Part part: rangement.getPlace()){
				dataset.setValue(MessageFormat.format(Program.getLabel("Infos179"),part.getNum()), rangement.getNbCaseUse(part.getNum()-1));
			}
			JFreeChart chart = ChartFactory.createPieChart(rangement.getNom(),          // chart title
					dataset,                // data
					false,                   // include legend
					true,
					false);

			ChartPanel chartPanel = new ChartPanel(chart);
			this.add(chartPanel, "grow");
		}

		private void setDataChart(List<StatData> datas, String title) {

			removeAll();
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
			add(chartPanel, "grow");
			updateUI();
		}
	}

	class StatData {

		private final String name;
		private final int count;


		private StatData(String name, int count) {
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
