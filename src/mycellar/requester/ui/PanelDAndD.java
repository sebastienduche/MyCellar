package mycellar.requester.ui;

import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragSource;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static mycellar.core.text.MyCellarLabelManagement.getLabel;

/**
 * <p>Titre : Cave &agrave; vin
 * <p>Description : Votre description
 * <p>Copyright : Copyright (c) 2014
 * <p>Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.4
 * @since 08/03/19
 */
public final class PanelDAndD extends JPanel {

  private static final long serialVersionUID = -3180057277279430308L;
  private final List<LabelSearch> labels = new ArrayList<>();
  public LabelSearch draggingLabel;
  ChangeListener listener = MainChangeListener.getChangeListener();
  private boolean target = false;

  PanelDAndD() {
    super();
    addMouseListener(new PanelHandler());
    setTransferHandler(new PanelLabelTransferHandler());
  }

  PanelDAndD(boolean target) {
    super();
    addMouseListener(new PanelHandler());
    setTransferHandler(new PanelLabelTransferHandler());
    this.target = target;
  }

  public boolean isTarget() {
    return target;
  }

  @Override
  public Component add(Component comp) {
    boolean add = true;
    if (!target) {
      if (!labels.contains(comp))
        labels.add((LabelSearch) comp);
      else
        add = false;
    }
    if (add) {
      listener.stateChanged(new ChangeEvent(this));
      return super.add(comp);
    }
    return null;
  }

  @Override
  public void add(Component comp, Object constraints) {
    boolean add = true;
    if (!target) {
      if (!labels.contains(comp))
        labels.add((LabelSearch) comp);
      else
        add = false;
    }
    if (add) {
      listener.stateChanged(new ChangeEvent(this));
      super.add(comp, constraints);
    }
  }

  @Override
  public void remove(Component comp) {
    super.remove(comp);
    if (comp instanceof LabelSearch) {
      LabelSearch search = (LabelSearch) comp;
      // Lorsque l'on est sur un element qui peut uniquement etre deplace
      // On le supprime de la liste si l'on est sur la source
      // et on l'ajoute a la source si l'on n'est pas deja dessus.
      if (!search.isCopy()) {
        if (!target) {
          labels.remove(search);
          if (search.getSource() != null && !Objects.equals(search.getSource(), this))
            search.getSource().add(search);
        }
        search.setAsKeyword(true);
      }
      listener.stateChanged(new ChangeEvent(this));
    }
  }

}

class PanelHandler extends MouseAdapter {
  @Override
  public void mousePressed(MouseEvent e) {
    final PanelDAndD p = (PanelDAndD) e.getSource();
    Component c = SwingUtilities.getDeepestComponentAt(p, e.getX(), e.getY());
    if (c != null && c.getParent() instanceof LabelSearch && c.getParent().getParent() instanceof PanelDAndD) {
      final LabelSearch labelSearch = (LabelSearch) c.getParent();
      if (e.getButton() == MouseEvent.BUTTON3) {
        if (p.isTarget() && labelSearch.getPredicate().isValueRequired()) {
          JPopupMenu popup = new JPopupMenu();
          JMenuItem menu = new JMenuItem(getLabel("Infos079"));
          menu.addActionListener((e1) -> {
            labelSearch.setValue(null);
            labelSearch.askForValue();
            labelSearch.updateUI();
            p.listener.stateChanged(new ChangeEvent(this));
          });
          popup.add(menu);
          popup.show(c, c.getX() + 5, c.getY() + 5);
        }
      } else {
        p.draggingLabel = labelSearch;
        p.getTransferHandler().exportAsDrag(p, e, labelSearch.isCopy() ? TransferHandler.COPY : TransferHandler.MOVE);
      }
    }
  }
}

class PanelLabelTransferHandler extends TransferHandler {
  private static final long serialVersionUID = -4338469857987642038L;
  private final DataFlavor localObjectFlavor;
  private final JLabel label = new JLabel() {
    private static final long serialVersionUID = 5065631180392050633L;

    @Override
    public boolean contains(int x, int y) {
      return false;
    }
  };
  private final JWindow window = new JWindow();

  PanelLabelTransferHandler() {
    localObjectFlavor = new ActivationDataFlavor(
        PanelDAndD.class, DataFlavor.javaJVMLocalObjectMimeType, "JPanel");
    window.add(label);
    window.setAlwaysOnTop(true);
    window.setBackground(new Color(0, true));
    DragSource.getDefaultDragSource().addDragSourceMotionListener((dsde) -> {
      Point pt = dsde.getLocation();
      pt.translate(5, 5); // offset
      window.setLocation(pt);
    });
  }

  @Override
  protected Transferable createTransferable(JComponent c) {
    PanelDAndD p = (PanelDAndD) c;
    LabelSearch l = p.draggingLabel;
    String text = l.getText();
    final DataHandler dh = new DataHandler(c, localObjectFlavor.getMimeType());
    if (text == null) {
      return dh;
    }
    final StringSelection ss = new StringSelection(text + "\n");
    return new Transferable() {
      @Override
      public DataFlavor[] getTransferDataFlavors() {
        ArrayList<DataFlavor> list = new ArrayList<>();
        Collections.addAll(list, ss.getTransferDataFlavors());
        Collections.addAll(list, dh.getTransferDataFlavors());
        return list.toArray(dh.getTransferDataFlavors());
      }

      @Override
      public boolean isDataFlavorSupported(DataFlavor flavor) {
        for (DataFlavor f : getTransferDataFlavors()) {
          if (flavor.equals(f)) {
            return true;
          }
        }
        return false;
      }

      @Override
      public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (flavor.equals(localObjectFlavor)) {
          return dh.getTransferData(flavor);
        } else {
          return ss.getTransferData(flavor);
        }
      }
    };
  }

  @Override
  public boolean canImport(TransferSupport support) {
    return support.isDrop();
  }

  @Override
  public int getSourceActions(JComponent c) {
    PanelDAndD p = (PanelDAndD) c;
    label.setText(p.draggingLabel.getText());
    window.pack();
    Point pt = p.draggingLabel.getLocation();
    SwingUtilities.convertPointToScreen(pt, p);
    window.setLocation(pt);
    window.setVisible(true);
    if (p.draggingLabel.isCopy())
      return COPY;
    return MOVE;
  }

  @Override
  public boolean importData(TransferSupport support) {
    if (!canImport(support))
      return false;
    final PanelDAndD target = (PanelDAndD) support.getComponent();
    try {
      PanelDAndD src = (PanelDAndD) support.getTransferable().getTransferData(localObjectFlavor);
      final LabelSearch l = new LabelSearch(src.draggingLabel.getPredicate(), src.draggingLabel.getSource());
      l.setLabel(src.draggingLabel.getLabel());
      l.setAsKeyword(!target.isTarget());
      if (target.isTarget()) {
        l.setValue(src.draggingLabel.getValueSearch());
        l.setAsKeyword(false);
        SwingUtilities.invokeLater(() -> {
          l.askForValue();
          target.listener.stateChanged(new ChangeEvent(this));
        });
      }
      target.add(l);
      target.revalidate();
      return true;
    } catch (UnsupportedFlavorException | IOException ufe) {
      ufe.printStackTrace();
    }
    return false;
  }

  @Override
  protected void exportDone(JComponent c, Transferable data, int action) {
    PanelDAndD src = (PanelDAndD) c;
    if (action == TransferHandler.MOVE) {
      src.remove(src.draggingLabel);
      src.revalidate();
      src.repaint();
    }
    src.draggingLabel = null;
    window.setVisible(false);
  }
}
