package partitaOffline.view;

import dominio.events.SetGiocata;
import dominio.events.SetPuntata;
import dominio.giocatori.Giocatore;
import dominio.view.ViewEvent;
import dominio.view.ViewEventListener;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import dominio.events.FineRound;
import dominio.events.*;
import partitaOffline.events.GiocatoreLocaleEvent;
import dominio.events.MazzierePerde;
import dominio.events.MazzoRimescolato;
import dominio.events.RichiediGiocata;
import dominio.events.RichiediNome;
import dominio.events.RichiediPuntata;
import dominio.events.RisultatoManoParticolare;
import dominio.events.SetNome;
import dominio.events.Vittoria;
import partitaOffline.model.PartitaOfflineModel;

public class PartitaOfflineConsoleView implements PartitaOfflineView, Observer{
    private final CopyOnWriteArrayList<ViewEventListener> listeners;
    private final PartitaOfflineModel model;
    private final Scanner scanner;
    int pausa_breve = 1000; //ms
    int pausa_lunga = 2000; //ms

    /**
     * 
     * @param model modello partita offline
     */
    public PartitaOfflineConsoleView(PartitaOfflineModel model) {
        this.listeners = new CopyOnWriteArrayList<>();
        this.model = model;
        this.model.addObserver(this);
        scanner = new Scanner(System.in);
    }

    /**
     * 
     * @param l evento
     */
    @Override
    public void addPartitaOfflineViewEventListener(ViewEventListener l) {
        listeners.add(l);
    }

    /**
     * 
     * @param l evento
     */
    @Override
    public void removePartitaOfflineViewEventListener(ViewEventListener l) {
        listeners.remove(l);
    }

    /**
     * 
     * @param arg argomenti dell'evento
     */
    protected void fireViewEvent(Object arg) {
        ViewEvent evt = new ViewEvent(this, arg);

        for (ViewEventListener l : listeners) {
            l.ViewEventReceived(evt);
        }
    }
    
    private void richiediNome(){
        String nome;
        System.out.println("-------------------------------------------------------------------------------");
        System.out.println("                               Come ti chiami?                               \n");
        System.out.print("                                        ");
        nome = scanner.next();
        System.out.println("-------------------------------------------------------------------------------");        
        fireViewEvent(new SetNome(nome));  
    }
    
    private void stampaSchermataEstrazioneMazziere(){
        System.out.println("  ---------------------------------------------------------------------------  ");
        System.out.println("<                             ESTRAZIONE MAZZIERE                             >");
        System.out.println("  ---------------------------------------------------------------------------  \n");
        pausa(pausa_breve);
        for(Giocatore giocatore : model.getGiocatori()){
            mostra_carta_coperta_e_valore_mano(giocatore);
            pausa(pausa_breve);
        }
        stampa_messaggio_mazziere();
        System.out.println("-------------------------------------------------------------------------------"); 
        pausa(pausa_lunga);
    }
    
    private void mostra_carta_coperta_e_valore_mano(Giocatore giocatore){
        System.out.println(giocatore.getNome() + " [" + giocatore.getCartaCoperta() + "] " + giocatore.getValoreMano());
    }
    
    private void stampa_messaggio_mazziere(){
        System.out.println("\n------> il Mazziere é: " + model.getMazziere().getNome() + " <------");
    }

    /**
     * 
     * @param o
     * @param arg argomenti dell'evento
     */
    @Override
    public void update(Observable o, Object arg) {
        if(arg instanceof RichiediNome){
            richiediNome();
        }else if(arg instanceof Error){
            System.err.println(((Error) arg).getMessage());
            pausa(pausa_breve);
        } else if(arg instanceof EstrattoMazziere){
            stampaSchermataEstrazioneMazziere();
        } else if(arg instanceof MazzoRimescolato){
            stampaSchermataRimescolaMazzo();
        } else if(arg instanceof RisultatoManoParticolare){
            stampaSchermataManoParticolare();
        } else if(arg instanceof FineManoAvversario){
            FineManoAvversario avversario = (FineManoAvversario) arg;
            System.out.println(avversario.getNome() + " " + avversario.getCarteScoperte() + " " + avversario.getStato() + " " + avversario.getPuntata());
            pausa(pausa_breve);
        } else if(arg instanceof FineRound){
            Giocatore giocatore = ((FineRound) arg).getGiocatore();
            if(giocatore.equals(model.getGiocatori().get(0))){
                pausa(pausa_lunga);
                System.out.print("\n");
            }
            System.out.println(giocatore.haPerso() + " " + giocatore.isMazziere() + " " + giocatore.getNome() + " " + giocatore.getTutteLeCarte() + " " + giocatore.getValoreMano() + " "+ giocatore.getStatoMano() + " " + giocatore.getFiches());
            if(giocatore.equals(model.getGiocatori().get(model.getGiocatori().size() - 1))){
                pausa(pausa_lunga);
            } else {
                pausa(pausa_breve);
            }
        } else if(arg instanceof MazzierePerde){
            System.out.println("\n");
            System.out.println("---------------------------> Il mazziere ha perso <----------------------------");
            pausa(pausa_breve);
        } else if(arg instanceof AggiornamentoMazziere){
            System.out.println("\n");
            System.out.println("---> il nuovo mazziere é: " + model.getMazziere().getNome() + " <---\n");
            pausa(pausa_breve);
        } else if(arg instanceof GameOver){
            System.out.println("\n");
            System.out.println("---------------------------------> Game Over <---------------------------------");
            pausa(pausa_breve);
            System.exit(0);
        } else if(arg instanceof Vittoria){
            System.out.println("\n");
            System.out.println("-------------------> Complimenti! Hai sconfitto tutti i bot <------------------");
            pausa(pausa_breve);
            System.exit(0);
        }
    }

    /**
     * 
     * @param evt evento
     */
    @Override
    public void GiocatoreLocaleEventReceived(GiocatoreLocaleEvent evt) {
        if(evt.getArg() instanceof RichiediPuntata){
            richiediPuntata(evt);
        } else if(evt.getArg() instanceof Error){
            System.err.println(((Error)evt.getArg()).getMessage());
            pausa(pausa_breve);
        } else if(evt.getArg() instanceof RichiediGiocata){
            richiediGiocata(evt);
        }
    }

    private void richiediPuntata(GiocatoreLocaleEvent evt) {
        System.out.print("\n");
        System.out.println("Carta coperta: " + ((RichiediPuntata) evt.getArg()).getCarta_coperta());
        System.out.println("Valore Mano : " + ((RichiediPuntata) evt.getArg()).getValore_mano());
        System.out.println("Fiches: " + ((RichiediPuntata) evt.getArg()).getFiches());
        System.out.println("Quante fiches vuoi puntare?");
        String puntata = scanner.next();
        fireViewEvent(new SetPuntata(puntata));
        System.out.print("\n");
    }
    
    private void richiediGiocata(GiocatoreLocaleEvent evt){
        System.out.print("\n");
        System.out.println("Valore Mano : " + ((RichiediGiocata) evt.getArg()).getValoreMano());
        System.out.println("Carta coperta: " + ((RichiediGiocata) evt.getArg()).getCartaCoperta());
        System.out.println("Carte scoperte: " + ((RichiediGiocata) evt.getArg()).getCarteScoperte());
        System.out.println("Carta o Stai?");
        String giocata = scanner.next();
        fireViewEvent(new SetGiocata(giocata));
        System.out.print("\n");
    }
    
    

    private void stampaSchermataRimescolaMazzo() {
        System.out.println("\n-----------------------------> Rimescolo il mazzo <----------------------------\n");
        pausa(pausa_lunga);
    }

    private void stampaSchermataManoParticolare() {
        System.out.println("Carta Ottenuta: " + model.getGiocatoreLocale().getUltimaCartaOttenuta());
        System.out.println("Valore Mano: " + model.getGiocatoreLocale().getValoreMano());
        System.out.println("--> " + model.getGiocatoreLocale().getStatoMano() + " <--");
        System.out.print("\n");
        pausa(pausa_lunga);
    }
    

    
    private void pausa(int tempo){
        try {
            Thread.sleep(tempo);
        } catch (InterruptedException ex) {
            Logger.getLogger(PartitaOfflineConsoleView.class.getName()).log(Level.SEVERE, null, ex);
        }
    } 

    
}
