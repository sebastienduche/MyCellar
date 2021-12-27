package mycellar;

import mycellar.core.uicomponents.JButtonTabComponent;

import javax.swing.JTabbedPane;
import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2012</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.5
 * @since 21/10/21
 */
public final class Utils {

  public static void addCloseButton(final JTabbedPane tabbedPane,
                                    final Component component) {
    addCloseButton(tabbedPane, component, -1);
  }

  public static void addCloseButton(final JTabbedPane tabbedPane,
                                    final Component component, int indexToGoBack) {
    final int index = tabbedPane.indexOfComponent(component);
    if (index != -1) {
      tabbedPane.setTabComponentAt(index,
          new JButtonTabComponent(tabbedPane, indexToGoBack));
    }

    tabbedPane.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if ((e.getKeyCode() == KeyEvent.VK_W)
            && (e.getModifiersEx() == InputEvent.CTRL_DOWN_MASK)) {

          // Ctrl-W permet de fermer les onglets du JTabbedPane
          if ((tabbedPane.getSelectedIndex() != -1)
              && (tabbedPane.getSelectedComponent().equals(component))) {

            // Un onglet est actif, supprimer le composant
            tabbedPane.remove(component);
            if (indexToGoBack != -1 && tabbedPane.getTabCount() > indexToGoBack) {
              tabbedPane.setSelectedIndex(indexToGoBack);
            }

            // Se deferencer en tant que listener
            tabbedPane.removeKeyListener(this);

            e.consume();
          }
        }
      }
    });
  }
}
