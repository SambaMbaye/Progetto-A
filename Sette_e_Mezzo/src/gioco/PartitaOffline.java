package gioco;


import eccezioni.DifficoltaBotException;
import eccezioni.FichesInizialiException;
import eccezioni.NumeroBotException;
import elementi_di_gioco.Mazzo;
import giocatori.BotFacile;
import giocatori.GiocatoreUmano;
import giocatori.Giocatore;
import classi_dati.DifficoltaBot;
import classi_dati.Stato;
import eccezioni.FineMazzoException;
import eccezioni.MazzierePerdeException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;


public class PartitaOffline {
    private ArrayList<Giocatore> giocatori=new ArrayList<>();
    private final Mazzo mazzo = new Mazzo();
    private Giocatore mazziere = null;
    private Giocatore next_mazziere = null;
    int pausa_breve = 1000; //ms
    int pausa_lunga = 2000; //ms
    int n_bot;
    int n_bot_sconfitti = 0;
    InputStream in;
    PrintStream out;
    
    /**
     *
     * @param numero_bot numero di bot iniziali
     * @param fiches_iniziali numero di fiches iniziali per ogni giocatore
     * @param difficolta_bot difficoltá di tutti i bot della partita
     * @param in InputStream (es. System.in)
     * @param out PrintStream (es. System.out)
     * @throws InterruptedException lanciata dai Thread.pause
     */
    public PartitaOffline(int numero_bot, int fiches_iniziali, DifficoltaBot difficolta_bot, InputStream in, PrintStream out) throws InterruptedException{
        this.in = in;
        this.out = out;
        this.n_bot = numero_bot;
        try {
            inizializza_partita(numero_bot, fiches_iniziali, difficolta_bot);
            estrai_mazziere();
            mazzo.aggiorna_fine_round();
            mazzo.rimescola();
            for(int i = 0; i < 100; i++){ //ci sono 100 round solo per prova
                gioca_round();
                try {
                    calcola_risultato();
                } catch (MazzierePerdeException ex) {
                    //da fare, per ora sceglie solo un nuovo mazziere
                    out.println("Il mazziere ha perso");
                    mazziere_successivo();
                    out.println("il nuovo mazziere é: " + mazziere.getNome());
                }
                fine_round();
                mazzo.aggiorna_fine_round();
                if(n_bot_sconfitti == n_bot){
                    vittoria();
                }
            }
            fine_partita();
        }catch (NumeroBotException ex) {
            this.out.println("Il numero di bot dev'essere un valore compreso tra 1 ed 11.");
        }catch (FichesInizialiException ex) {
            this.out.println("Il numero di fiches iniziali dev'essere maggiore di 0");
        }catch (DifficoltaBotException ex) {
            this.out.println("Le difficolta disponibili sono: Facile. //Work in Progress\\");
        }
    }
    
    private void inizializza_partita(int numero_bot, int fiches_iniziali, DifficoltaBot difficolta_bot) throws NumeroBotException, FichesInizialiException, DifficoltaBotException{
        inizzializza_fiches(fiches_iniziali);
        inizializza_bots(numero_bot, fiches_iniziali, difficolta_bot);
        inizializza_giocatore(fiches_iniziali); 
    }

    private void inizzializza_fiches(int fiches_iniziali) throws FichesInizialiException {
        if(fiches_iniziali <= 0){
            throw new FichesInizialiException();
        }
    }
    
    private void inizializza_bots(int numero_bot, int fiches_iniziali, DifficoltaBot difficolta_bot) throws NumeroBotException, DifficoltaBotException{
        if(numero_bot <= 0 || numero_bot >= 12){
            throw new NumeroBotException();
        }
        for(int i = 0; i < numero_bot; i++){
            switch(difficolta_bot){
                case Facile : {
                    giocatori.add(new BotFacile("bot"+i, fiches_iniziali)); //nomi bot: bot0, bot1, ...
                    break;
                }
                default: throw new DifficoltaBotException();       
            }
        }
    }
    
    private void inizializza_giocatore(int fiches_iniziali){
        out.println("Come ti chiami?");
        String nome = richiedi_nome_giocatore();
        giocatori.add(new GiocatoreUmano(nome,fiches_iniziali,in,out));
        out.print("\n");
    }
    
    private String richiedi_nome_giocatore(){
        String nome;
        Scanner scan = new Scanner(System.in);
        
        nome = scan.next();
        return nome;
    }

    private void estrai_mazziere() throws InterruptedException {
        mazzo.mischia();
        Thread.sleep(pausa_breve);
        out.println("Estrazione del mazziere:\n");
        Thread.sleep(pausa_breve);
        for(Giocatore giocatore : giocatori){
            while(true){
                try {
                    giocatore.prendi_carta_iniziale(mazzo);
                    mostra_carta_coperta_e_valore_mano(giocatore);
                    Thread.sleep(pausa_breve);
                    break;
                }catch (FineMazzoException ex) {
                    mazzo.rimescola();
                    stampa_messaggio_rimescola_mazzo();
                }
            }
            seleziona_mazziere(giocatore);
        }
        stampa_messaggio_mazziere();
        Thread.sleep(pausa_lunga);
        mazziere.setMazziere(true);
    }
    
    private void mostra_carta_coperta_e_valore_mano(Giocatore giocatore){
        out.println(giocatore.getNome() + " [" + giocatore.getCartaCoperta() + "] " + giocatore.getValoreMano());
    }
    
    private void stampa_messaggio_rimescola_mazzo(){
        out.println("Rimescolo il mazzo.");
    }
    
    private void seleziona_mazziere(Giocatore giocatore){
        if(mazziere == null){
            aggiorna_mazziere(giocatore);
        }else if(giocatore.getValoreMano() > mazziere.getValoreMano()){
            aggiorna_mazziere(giocatore);
        }else if(giocatore.getValoreMano() == mazziere.getValoreMano()){
            if(giocatore.getCartaCoperta().getSeme().equals("c")){
                aggiorna_mazziere(giocatore);
            }else if(giocatore.getCartaCoperta().getSeme().equals("q") && ! mazziere.getCartaCoperta().getSeme().equals("c")){
                aggiorna_mazziere(giocatore);
            }else if(giocatore.getCartaCoperta().getSeme().equals("f") && mazziere.getCartaCoperta().getSeme().equals("p")){
                aggiorna_mazziere(giocatore);
            }else if(giocatore.getCartaCoperta().getSeme().equals(mazziere.getCartaCoperta().getSeme())){
                if(giocatore.getCartaCoperta().getSimbolo().equals("K")){
                    aggiorna_mazziere(giocatore);
                }else if(giocatore.getCartaCoperta().getSimbolo().equals("Q") && mazziere.getCartaCoperta().getSimbolo().equals("J")){
                    aggiorna_mazziere(giocatore);
                }
            }
        }
    }
    
    private void aggiorna_mazziere(Giocatore giocatore){
        mazziere = giocatore;
    }
    
    private void stampa_messaggio_mazziere(){
        out.println("\nIl Mazziere é: " + mazziere.getNome() + "\n");
    }

    private void gioca_round() throws InterruptedException {
        int pos_mazziere = giocatori.indexOf(mazziere);
        int pos_next_giocatore = pos_mazziere + 1;
        Giocatore giocatore;
        
        inizializza_round();
        distribuisci_carta_coperta();
        for(int i = 0; i < giocatori.size(); i++){
            if(pos_next_giocatore == giocatori.size()){
                pos_next_giocatore = 0;
            }
            giocatore = getProssimoGiocatore(pos_next_giocatore);
            if(! giocatore.haPerso()){
                giocatore.gioca_mano(mazzo);
                if(!giocatore.isMazziere() && giocatore.getStato() == Stato.Sballato){
                    giocatore_paga_mazziere(giocatore); //giocatore se sballa paga subito.
                    if(giocatore.getFiches() == 0){
                        if(giocatore instanceof GiocatoreUmano){
                            stampa_se_stato_non_ok(giocatore);
                            Thread.sleep(pausa_lunga);
                            game_over();
                        }else{
                            giocatore.perde();
                            n_bot_sconfitti += 1;
                        }
                    }
                }
                if(giocatore instanceof GiocatoreUmano && giocatore.getStato() != Stato.OK){
                    stampa_se_stato_non_ok(giocatore);
                    Thread.sleep(pausa_lunga);
                }
            }
            if(! (giocatore instanceof GiocatoreUmano)){
                stampa_giocata_bot(giocatore);
                Thread.sleep(pausa_breve);
            }
            pos_next_giocatore += 1;
        }
        out.print("\n");
    }
    
    private void inizializza_round(){
        for(Giocatore giocatore : giocatori){
            giocatore.inizializza_mano();
        }
        next_mazziere = null;
    }
    
    private void distribuisci_carta_coperta(){
        for(Giocatore giocatore : giocatori){
            while(true){
                try {
                    if(! giocatore.haPerso()){
                        giocatore.prendi_carta_iniziale(mazzo);
                    }
                    break;
                } catch (FineMazzoException ex) {
                    mazzo.rimescola();
                    stampa_messaggio_rimescola_mazzo();
                }
            }
        }
    }
    
    private Giocatore getProssimoGiocatore(int posizione){
        return giocatori.get(posizione);
    }
    
    private void stampa_se_stato_non_ok(Giocatore giocatore){
        out.println("Carta Ottenuta: " + giocatore.getUltimaCartaOttenuta());
        out.println("Valore Mano: " + giocatore.getValoreMano() + "\n");
        out.println(giocatore.getStato());
        out.print("\n");
    }
    
    private void stampa_giocata_bot(Giocatore giocatore){
        out.println(giocatore.getNome() + " " + giocatore.getCarteScoperte() + " " + giocatore.getStato() + " " + giocatore.getPuntata());
    }
    
    private void calcola_risultato() throws MazzierePerdeException{
        for(Giocatore giocatore : giocatori){
            if(! giocatore.isMazziere()){
                switch(mazziere.getStato()){
                    case Sballato: {
                        switch(giocatore.getStato()){
                            case SetteeMezzo:{
                                mazziere_paga_giocatore(giocatore);
                                break;
                            }
                            case OK:{
                                mazziere_paga_giocatore(giocatore);
                                break;
                            }
                            case SetteeMezzoReale:{
                                mazziere_paga_reale_giocatore(giocatore);
                                next_mazziere = giocatore; //ultimo che fa sette e mezzo reale
                                break;
                            }
                        } break;
                    }
                    case OK: {
                        switch(giocatore.getStato()){
                            case SetteeMezzo:{
                                mazziere_paga_giocatore(giocatore);
                                break;
                            }
                            case OK:{ 
                                if(mazziere.getValoreMano() >= giocatore.getValoreMano()){
                                    giocatore_paga_mazziere(giocatore);
                                }else{
                                    mazziere_paga_giocatore(giocatore);
                                } 
                                break;
                            }
                            case SetteeMezzoReale:{
                                mazziere_paga_reale_giocatore(giocatore);
                                next_mazziere = giocatore; //ultimo che fa sette e mezzo reale
                                break;
                            }
                        } break;
                    }
                    case SetteeMezzo: {
                        switch(giocatore.getStato()){
                            case SetteeMezzo:{
                                giocatore_paga_mazziere(giocatore);
                                break;
                            }
                            case OK:{
                                giocatore_paga_mazziere(giocatore);
                                break;
                            }
                            case SetteeMezzoReale:{
                                mazziere_paga_reale_giocatore(giocatore); 
                                next_mazziere = giocatore; //ultimo che fa sette e mezzo reale
                                break;
                            }
                        } break;
                    }
                    case SetteeMezzoReale: {
                        switch(giocatore.getStato()){
                            case SetteeMezzo:{
                                giocatore_paga_reale_mazziere(giocatore);
                                break;
                            }
                            case OK:{
                                giocatore_paga_reale_mazziere(giocatore);
                                break;
                            }
                            case SetteeMezzoReale:{
                                giocatore_paga_mazziere(giocatore);
                                next_mazziere = giocatore; //ultimo che fa sette e mezzo reale
                                break;
                            }
                        }break;
                    }
                }
            }
        }
    }
    
    private void giocatore_paga_mazziere(Giocatore giocatore){
        mazziere.riscuoti(giocatore.paga_mazziere());
    }
    
    private void giocatore_paga_reale_mazziere(Giocatore giocatore){
        mazziere.riscuoti(giocatore.paga_reale_mazziere());
    }
    
    private void mazziere_paga_giocatore(Giocatore giocatore) throws MazzierePerdeException{
        giocatore.riscuoti(mazziere.paga_giocatore(giocatore.getPuntata()));
    }
    
    private void mazziere_paga_reale_giocatore(Giocatore giocatore) throws MazzierePerdeException{
        giocatore.riscuoti(mazziere.paga_reale_giocatore(giocatore.getPuntata()));
    }
    
   private void mazziere_successivo(){
       int pos_next_mazziere = giocatori.indexOf(mazziere) + 1;
       if(pos_next_mazziere == giocatori.size()){
           pos_next_mazziere = 0;
       }
       for(int i = 0; i < giocatori.size(); i++){
           if(giocatori.get(pos_next_mazziere).haPerso()){
                pos_next_mazziere += 1;
                if(pos_next_mazziere == giocatori.size()){
                    pos_next_mazziere = 0;                
                }              
           } else {
               mazziere = giocatori.get(pos_next_mazziere);
               break;
           }
       }
   }
    
    private void fine_round() throws InterruptedException{
        for(Giocatore giocatore : giocatori){
            stampa_risultato_round(giocatore);
            Thread.sleep(pausa_breve);
            if(giocatore.getFiches() == 0 && ! giocatore.haPerso()){
                if(giocatore instanceof GiocatoreUmano){
                    game_over();
                } else {
                    giocatore.perde();
                    n_bot_sconfitti += 1;
                }
            }
        }
        out.print("\n");
        aggiorna_mazziere();
        Thread.sleep(pausa_lunga);
    }
    
    private void stampa_risultato_round(Giocatore giocatore){
        out.println(giocatore.haPerso() + " " + giocatore.isMazziere() + " " + giocatore.getNome() + " " + giocatore.getTutteLeCarte() + " " + giocatore.getValoreMano() + " "+ giocatore.getStato() + " " + giocatore.getFiches());
    }
    
    private void aggiorna_mazziere(){
        if(next_mazziere != null){
            mazziere.setMazziere(false);
            next_mazziere.setMazziere(true);
            mazziere = next_mazziere;
            out.println("il nuovo mazziere é: " + mazziere.getNome() + "\n");
        }
    }

    private void fine_partita() {
        //da fare
    }   

    private void game_over() {
        out.println("Game Over");
        System.exit(0);
    }

    private void vittoria() throws InterruptedException {
        out.println("Complimenti! Hai vinto.");
        System.exit(0);
    }
}