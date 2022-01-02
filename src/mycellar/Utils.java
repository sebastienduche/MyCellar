package mycellar;

import mycellar.core.uicomponents.JButtonTabComponent;
import mycellar.general.ProgramPanels;

import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import static mycellar.general.ProgramPanels.TABBED_PANE;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2012</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.7
 * @since 02/01/22
 */
public final class Utils {

  public static void addCloseButtonToTab(final Component component) {
    addCloseButtonToTab(component, -1, true);
  }

  public static void addCloseButtonToTab(final Component component, int indexToGoBack, boolean leftTabDirection) {
    final int index = TABBED_PANE.indexOfComponent(component);
    if (index != -1) {
      TABBED_PANE.setTabComponentAt(index,
          new JButtonTabComponent(TABBED_PANE, indexToGoBack));
    }

    TABBED_PANE.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if ((e.getKeyCode() == KeyEvent.VK_W)
            && (e.getModifiersEx() == InputEvent.CTRL_DOWN_MASK)) {

          // Ctrl-W permet de fermer les onglets du JTabbedPane
          final int selectedIndex = TABBED_PANE.getSelectedIndex();
          if ((selectedIndex != -1)
              && (TABBED_PANE.getSelectedComponent().equals(component))) {

            // Un onglet est actif, supprimer le composant
//            TABBED_PANE.remove(component);
            ProgramPanels.removeTabAt(selectedIndex);
            int previousIndex = leftTabDirection ? selectedIndex - 1 : indexToGoBack;
            if (previousIndex != -1 && TABBED_PANE.getTabCount() > previousIndex) {
              TABBED_PANE.setSelectedIndex(previousIndex);
            }

            // Se deferencer en tant que listener
            TABBED_PANE.removeKeyListener(this);

            e.consume();
          }
        }
      }
    });
  }
}
