package mycellar;

import javax.swing.JTabbedPane;
import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2012</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.2
 * @since 02/03/18
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
            && (e.getModifiersEx() == InputEvent.CTRL_DOWN_MASK)) {

          // Ctrl-W permet de fermer les onglets du JTabbedPane
          if ((tabbedPane.getSelectedIndex() != -1)
              && (tabbedPane.getSelectedComponent().equals(component))) {

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
