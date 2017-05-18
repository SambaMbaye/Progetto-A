package partitaOffline.view;

import partitaOffline.events.SetGiocata;
import partitaOffline.events.SetPuntata;
import dominio.giocatori.Giocatore;
import dominio.view.ViewEvent;
import dominio.view.ViewEventListener;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import partitaOffline.events.AggiornamentoMazziere;
import partitaOffline.events.EstrattoMazziere;
import partitaOffline.events.FineManoAvversario;
import partitaOffline.events.FineRound;
import partitaOffline.events.GameOver;
import partitaOffline.events.GiocatoreLocaleEvent;
import partitaOffline.events.MazzierePerde;
import partitaOffline.events.MazzoRimescolato;
import partitaOffline.events.RichiediGiocata;
import partitaOffline.events.RichiediNome;
import partitaOffline.events.RichiediPuntata;
import partitaOffline.events.RisultatoManoParticolare;
import partitaOffline.events.SetNome;
import partitaOffline.events.Vittoria;
import partitaOffline.model.PartitaOfflineModel;

public class PartitaOfflineConsoleView implements PartitaOfflineView, Observer{
    private final CopyOnWriteArrayList<ViewEventListener> listeners;
    private PartitaOfflineModel model;
    private Scanner scanner;
    int pausa_breve = 1000; //ms
    int pausa_lunga = 2000; //ms

    public PartitaOfflineConsoleView(PartitaOfflineModel model) {
        this.listeners = new CopyOnWriteArrayList<>();
        this.model = model;
        this.model.addObserver(this);
        scanner = new Scanner(System.in);
    }

    @Override
    public void addPartitaOfflineViewEventListener(ViewEventListener l) {
        listeners.add(l);
    }

    @Override
    public void removePartitaOfflineViewEventListener(ViewEventListener l) {
        listeners.remove(l);
    }

    protected void fireViewEvent(Object arg) {
        ViewEvent evt = new ViewEvent(this, arg);

        for (ViewEventListener l : listeners) {
            l.ViewEventReceived(evt);
        }
    }
    
    private void richiediNome(){
        String nome;
        System.out.println("---------------------------------");
        System.out.println("         Come ti chiami?         \n");
        System.out.print("            ");
        nome = scanner.next();
        System.out.println("---------------------------------");        
        fireViewEvent(new SetNome(nome));  
    }
    
    private void stampaSchermataEstrazioneMazziere(){
        System.out.println("  -----------------------------  ");
        System.out.println("<      ESTRAZIONE MAZZIERE      >");
        System.out.println("  -----------------------------  ");
        try {
            Thread.sleep(pausa_breve);
        } catch (InterruptedException ex) {
        }
        for(Giocatore giocatore : model.getGiocatori()){
            mostra_carta_coperta_e_valore_mano(giocatore);
            try {
                Thread.sleep(pausa_breve);
            } catch (InterruptedException ex) {
            }
        }
        stampa_messaggio_mazziere();
        System.out.println("---------------------------------"); 
        try {
            Thread.sleep(pausa_lunga);
        } catch (InterruptedException ex) {
        }
    }
    
    private void mostra_carta_coperta_e_valore_mano(Giocatore giocatore){
        System.out.println(giocatore.getNome() + " [" + giocatore.getCartaCoperta() + "] " + giocatore.getValoreMano());
    }
    
    private void stampa_messaggio_mazziere(){
        System.out.println("\nIl Mazziere é: " + model.getMazziere().getNome() + "\n");
    }

    @Override
    public void update(Observable o, Object arg) {
        if(arg instanceof RichiediNome){
            richiediNome();
        }else if(arg instanceof Error){
            System.err.println(((Error) arg).getMessage());
        } else if(arg instanceof EstrattoMazziere){
            stampaSchermataEstrazioneMazziere();
        } else if(arg instanceof MazzoRimescolato){
            stampaSchermataRimescolaMazzo();
            System.out.println("\nRimescolo mazzo\n");
        } else if(arg instanceof RisultatoManoParticolare){
            System.out.println(model.getGiocatoreLocale().getUltimaCartaOttenuta());
            System.out.println(model.getGiocatoreLocale().getValoreMano());
            System.out.println(model.getGiocatoreLocale().getStato());
            System.out.println("\n");
        } else if(arg instanceof FineManoAvversario){
            FineManoAvversario avversario = (FineManoAvversario) arg;
            System.out.println(avversario.getNome() + " " + avversario.getCarteScoperte() + " " + avversario.getStato() + " " + avversario.getPuntata());
        } else if(arg instanceof FineRound){
            Giocatore giocatore = ((FineRound) arg).getGiocatore();
            System.out.println(giocatore.haPerso() + " " + giocatore.isMazziere() + " " + giocatore.getNome() + " " + giocatore.getTutteLeCarte() + " " + giocatore.getValoreMano() + " "+ giocatore.getStato() + " " + giocatore.getFiches());
        } else if(arg instanceof MazzierePerde){
            System.out.println("Il mazziere ha perso");
        } else if(arg instanceof AggiornamentoMazziere){
            System.out.println("il nuovo mazziere é: " + model.getMazziere().getNome() + "\n");
        } else if(arg instanceof GameOver){
            System.out.println("Game Over");
        } else if(arg instanceof Vittoria){
            System.out.println("Complimenti! Hai sconfitto tutti i bot");
        }
    }

    @Override
    public void GiocatoreLocaleEventReceived(GiocatoreLocaleEvent evt) {
        if(evt.getArg() instanceof RichiediPuntata){
            richiediPuntata(evt);
        } else if(evt.getArg() instanceof Error){
            System.err.println(((Error)evt.getArg()).getMessage());
        } else if(evt.getArg() instanceof RichiediGiocata){
            richiediGiocata(evt);
        }
    }

    private void richiediPuntata(GiocatoreLocaleEvent evt) {
        System.out.println("Carta coperta: " + ((RichiediPuntata) evt.getArg()).getCarta_coperta());
        System.out.println("Valore Mano : " + ((RichiediPuntata) evt.getArg()).getValore_mano());
        System.out.println("Fiches: " + ((RichiediPuntata) evt.getArg()).getFiches());
        System.out.println("Quante fiches vuoi puntare?");
        String puntata = scanner.next();
        fireViewEvent(new SetPuntata(puntata));
    }
    
    private void richiediGiocata(GiocatoreLocaleEvent evt){
        System.out.println("Valore Mano : " + ((RichiediGiocata) evt.getArg()).getValoreMano());
        System.out.println("Carta coperta: " + ((RichiediGiocata) evt.getArg()).getCartaCoperta());
        System.out.println("Carte scoperte: " + ((RichiediGiocata) evt.getArg()).getCarteScoperte());
        System.out.println("Carta o Stai?");
        String giocata = scanner.next();
        fireViewEvent(new SetGiocata(giocata));
    }

    private void stampaSchermataRimescolaMazzo() {
        System.out.println("  ----------------------------- ");
        System.out.println("<      RIMESCOLO IL MAZZO      >");
        System.out.println("  ----------------------------- ");
    }
    
}