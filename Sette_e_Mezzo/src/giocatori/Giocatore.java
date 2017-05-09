package giocatori;


import eccezioni.MazzierePerdeException;
import eccezioni.SetteeMezzoException;
import eccezioni.SetteeMezzoRealeException;
import eccezioni.SballatoException;
import classi_dati.Giocata;
import classi_dati.Stato;
import eccezioni.FineMazzoException;
import eccezioni.MattaException;
import elementi_di_gioco.Carta;
import java.util.ArrayList;


public abstract class Giocatore {
    private final String nome;    
    private int fiches;
    private boolean mazziere;
    protected Carta carta_coperta;    
    private int puntata;
    protected ArrayList<Carta> carte_scoperte= new ArrayList<>();
    protected double valore_mano = 0;
    private Stato stato;
    private boolean perso = false;
    
    /**
     *
     * @param nome nome del giocatore.
     * @param fiches numero di fiches iniziali del giocatore.
     */
    public Giocatore(String nome, int fiches){
        this.nome = nome;
        this.fiches = fiches;
    }
    
    /**
     * Inizializzazione prima di giocare una nuova mano.
     *  - Elimina la carta coperta della mano precedente
     *  - Elimina le carte scoperte della mano precedente
     *  - Inizializza la puntata a 0
     *  - Inizializza il valore_mano a 0
     *  - Inizializza lo stato della mano a OK
     * 
     */
    public void inizializza_mano(){
        carta_coperta = null;
        puntata = 0;
        carte_scoperte.clear();
        valore_mano = 0;
        stato = Stato.OK;
    }
    
    /**
     * Prende la prima carta della mano e la usa come carta_coperta.
     * 
     * @param carta carta pescata
     * @throws FineMazzoException avvisa se il mazzo non ha piú carte estraibili
     */
    public void prendi_carta_iniziale(Carta carta) throws FineMazzoException{
        carta_coperta = carta;
        aggiorna_valore_mano();
    }

    public boolean effettua_giocata(){
        Giocata giocata = decidi_giocata();
        return gioca(giocata);
    }
    /**
     * Consente di decidere la puntata da effettuare.
     * 
     * @return il valore della puntata scelta
     */
    protected abstract int decidi_puntata();
    
    private void punta(int puntata){
        fiches = fiches - puntata;
        this.puntata = puntata;
    }
    
    public void effettua_puntata(){
        int valore_puntata = decidi_puntata();
        punta(valore_puntata);
    }
    
    /**
     * Consente di decidere la giocata da effettuare.
     * 
     * @return la giocata scelta
     */
    protected abstract Giocata decidi_giocata();
    
    private boolean gioca(Giocata giocata){
        switch(giocata){                
            case Carta: return true;
            case Sto: return false;
            default : return true; //impossibile ma senza da errore
        }
    }
    
    public void chiedi_carta(Carta carta) throws SballatoException, SetteeMezzoRealeException, SetteeMezzoException{
        carte_scoperte.add(carta);                    
        aggiorna_valore_mano();
        controlla_valore_mano();
    }
      
    private void aggiorna_valore_mano() {
        double valore_mano = 0;
        boolean matta = false;
        try {
            valore_mano = carta_coperta.getValoreNumerico();
        } catch (MattaException ex) {
            matta = true;
        }
        for(Carta carta : carte_scoperte){
            try {
                valore_mano += carta.getValoreNumerico();
            } catch (MattaException ex) {
                matta = true;
            }
        }
        if(matta){
            if(carte_scoperte.isEmpty() || valore_mano == 7){ //se la matta è la prima carta pescata, vale 0.5;
                valore_mano += 0.5;
            } else {
                valore_mano += Math.round(7 - valore_mano);
            }
        }
        this.valore_mano= valore_mano;
    }
    
    private void controlla_valore_mano() throws SballatoException, SetteeMezzoRealeException, SetteeMezzoException{
        if(valore_mano > 7.5){
            throw new SballatoException();
        }
        else if (carte_scoperte.size() == 1 && valore_mano == 7.5 && carta_coperta.getSeme().equals(carte_scoperte.get(0).getSeme())){
            throw new SetteeMezzoRealeException();
        }
        else if (valore_mano == 7.5){
            throw new SetteeMezzoException();
        }
    }
    
    /**
     * Consente i pagamenti normali ad un avversario.
     * @param avversario
     * @throws MazzierePerdeException
     */
    public void paga(Giocatore avversario) throws MazzierePerdeException{
        int puntata;
        if(this.isMazziere()){
            puntata = avversario.getPuntata();
            this.paga_giocatore(puntata);
            avversario.riscuoti(puntata);
        } else{
            puntata = this.puntata;
            avversario.riscuoti(puntata);
        }
    }
    
    /**
     * Consente i pagamenti reali ad un avversario.
     * @param avversario
     * @throws MazzierePerdeException
     */
    public void paga_reale(Giocatore avversario) throws MazzierePerdeException{
        int puntata;
        if(this.isMazziere()){
            puntata = avversario.getPuntata() * 2;
            this.paga_giocatore(puntata);
            avversario.riscuoti(puntata);
        } else {
            puntata = this.paga_reale_mazziere();
            avversario.riscuoti(puntata);
        }
    }

    private void paga_giocatore(int puntata) throws MazzierePerdeException{
        if(fiches - puntata < 0){
            throw new MazzierePerdeException();
        }
        punta(puntata);
    }

    private int paga_reale_mazziere(){
        fiches = fiches - (2 *puntata);
        if(fiches < 0){
            int buf = fiches;
            fiches = 0;
            return puntata + (buf + puntata);
        }
        return puntata * 2;
    }
    
    public void riscuoti(int vincita){
        fiches = fiches + puntata + vincita;
    }
    
    public ArrayList<Carta> getTutteLeCarte(){
        ArrayList<Carta> carte = new ArrayList<>();
        carte.add(carta_coperta);
        carte.addAll(carte_scoperte);
        return carte;
    }
    
    public Carta getUltimaCartaOttenuta(){
        return carte_scoperte.get(carte_scoperte.size() - 1);
    }
    
    public void azzera_fiches(){
        fiches = 0;
    }
    
    /**
     * Imposta il booleano perso a true.
     */
    public void perde(){
        perso = true;
    }
    public boolean haPerso(){
        return perso;
    }
    public boolean isMazziere(){
        return mazziere;
    }
    
    public void setMazziere(boolean mazziere){
        this.mazziere = mazziere;
    }
    
    public double getValoreMano(){
        return valore_mano;
    }
    
    public int getFiches(){
        return fiches;
    }
    
    public int getPuntata(){
        return puntata;
    }
    
    public Carta getCartaCoperta(){
        return carta_coperta;
    }
    
    public String getNome(){
        return nome;
    }
    
    public Stato getStato(){
        return stato;
    }

    public void setStato(Stato stato){
        this.stato = stato;
    }
    public ArrayList<Carta> getCarteScoperte() {
        return carte_scoperte;
    }
}
