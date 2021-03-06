package menuPrincipale;

import dominio.eccezioni.OpzioneSceltaNonValidaException;
import dominio.eccezioni.PartitaOnlineIniziataException;
import dominio.classi_dati.Banners;
import dominio.classi_dati.OpzioniMenu;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import menuOpzioni.MenuOpzioniConsole;
import menuPrePartita.MenuPrePartitaConsole;
import menuRegole.RegoleConsole;
import moduli.PartitaOnlineConsole;
import net.Client;

public class MenuPrincipaleConsole {

    private OpzioniMenu opzione;
    private String opzione_inserita;
    private RegoleConsole regole;
    private MenuOpzioniConsole opzioni;
    private Banners banner;
    private Client client;

    public MenuPrincipaleConsole() {
        this.regole = new RegoleConsole();
        this.opzioni = new MenuOpzioniConsole();
        this.banner = new Banners();
        this.client = new Client();
        
        System.out.println(banner.randomBanner());
        run();
        
    }

    private void run() {
        try {
            printScelte();
            richiediOpzione();
            controllaOpzione();
            runOpzione();
            run();
        } catch (OpzioneSceltaNonValidaException ex) {
            System.err.println("Errore: La scelta effettuata non é valida.\n");
            run();
        } catch (PartitaOnlineIniziataException ex) {
        }
    }

    private void printScelte() {
        System.out.println("  ---------------------------------------------------------------------------  ");
        System.out.println("                       < SELEZIONA UN OPZIONE DAL MENU >                       ");
        System.out.println("  ---------------------------------------------------------------------------  ");
        System.out.println("                                1. GiocaOffline                                ");
        System.out.println("                                2. GiocaOnline                                 ");
        System.out.println("                                3. Impostazioni                                ");
        System.out.println("                                4. RegoleDiGioco                               ");
        System.out.print("\n");
        System.out.print("                                         ");
    }

    private void richiediOpzione() {
        String opzione;
        Scanner scanner = new Scanner(System.in);
        opzione_inserita = scanner.next();
        System.out.print("\n");
    }

    private void controllaOpzione() throws OpzioneSceltaNonValidaException {
        if (opzione_inserita.equalsIgnoreCase("giocaoffline") || opzione_inserita.equals("1")) {
            opzione = OpzioniMenu.GiocaOffline;
        } else if (opzione_inserita.equalsIgnoreCase("giocaonline") || opzione_inserita.equals("2")) {
            opzione = OpzioniMenu.GiocaOnline;
        } else if (opzione_inserita.equalsIgnoreCase("impostazioni") || opzione_inserita.equals("3")) {
            opzione = OpzioniMenu.Impostazioni;
        } else if (opzione_inserita.equalsIgnoreCase("regoledigioco") || opzione_inserita.equals("4")) {
            opzione = OpzioniMenu.RegoleDiGioco;
        } else {
            throw new OpzioneSceltaNonValidaException();
        }
    }

    private void runOpzione() throws PartitaOnlineIniziataException {
        switch (opzione) {

            case GiocaOffline:
                new MenuPrePartitaConsole();
                break;
            case GiocaOnline:
                try{
                    client.connetti();
                    new PartitaOnlineConsole(client);
                    throw new PartitaOnlineIniziataException();
                } catch (IOException e){
                    System.err.println("Errore: Impossibile connettersi al server.");
                }
                break;
            case Impostazioni:
                opzioni.run();
                break;
            case RegoleDiGioco:
                regole.run();
                break;
        }
    }
}
