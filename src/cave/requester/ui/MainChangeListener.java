package Cave.requester.ui;

import javax.swing.event.ChangeListener;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2014</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.1
 * @since 11/06/14
 */
public class MainChangeListener {
    
    private ChangeListener cl;
    
    private static final MainChangeListener instance = new MainChangeListener();
    
    private MainChangeListener(){}
    
    public MainChangeListener getInstance() {
        return instance;
    }
    
    public static ChangeListener getChangeListener(){
        return instance.cl;
    }
    
    public static void setChangeListener(ChangeListener cl){
        instance.cl = cl;
    }
}
