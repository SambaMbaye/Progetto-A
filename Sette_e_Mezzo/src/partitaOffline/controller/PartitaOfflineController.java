package partitaOffline.controller;

import dominio.view.ViewEvent;
import dominio.view.ViewEventListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import partitaOffline.events.SetGiocata;
import partitaOffline.events.SetNome;
import partitaOffline.events.SetPuntata;
import partitaOffline.model.PartitaOfflineModel;
import partitaOffline.view.PartitaOfflineView;


public class PartitaOfflineController implements ViewEventListener{
    private PartitaOfflineModel model;
    private PartitaOfflineView view;

    public PartitaOfflineController(PartitaOfflineModel model, PartitaOfflineView view) {
        this.model = model;
        this.view = view;
        view.addPartitaOfflineViewEventListener(this);
    }
    
    public void run(){
        this.model.inizializza_partita();
        model.addGiocatoreLocaleEventListener(view);
        try {
            this.model.gioca();
        } catch (InterruptedException ex) {
            Logger.getLogger(PartitaOfflineController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void ViewEventReceived(ViewEvent evt) {
        if(evt.getArg() instanceof SetNome){
            model.setNomeGiocatore(((SetNome)evt.getArg()).getNome());
        } else if(evt.getArg() instanceof SetPuntata){
        model.getGiocatoreLocale().PuntataInserita(((SetPuntata)evt.getArg()).getPuntata());
        } else if(evt.getArg() instanceof SetGiocata){
            model.getGiocatoreLocale().GiocataInserita(((SetGiocata)evt.getArg()).getGiocata());
        }
    }   
    
}
