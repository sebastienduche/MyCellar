package mycellar;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTabbedPane;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2012</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.1
 * @since 25/08/12
 */
public final class Utils {

	public static void addCloseButton(final JTabbedPane tabbedPane,
            final Component component) {

    //if (!DISABLE_TAB_COMPONENT) {
            // Ne pas afficher les composants d'onglets si ça a été
            // explicitement demandé (ne fonctionne pas avec tous les look and
            // feels)
            tabbedPane.setTabComponentAt(
                            tabbedPane.indexOfComponent(component),
                            new JButtonTabComponent(tabbedPane));
    //}

    tabbedPane.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                    if ((e.getKeyCode() == KeyEvent.VK_W)
                                    && (e.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK)) {

                            // Ctrl-W permet de fermer les onglets du JTabbedPane
                            if ((tabbedPane.getSelectedIndex() != -1)
                                            && (tabbedPane.getSelectedComponent() == component)) {

                                    // Un onglet est actif, supprimer le composant concerné
                                    tabbedPane.remove(component);

                                    // Se déréférencer en tant que listener
                                    tabbedPane.removeKeyListener(this);

                                    e.consume();
                            }
                    }
            }
    });
}
}
