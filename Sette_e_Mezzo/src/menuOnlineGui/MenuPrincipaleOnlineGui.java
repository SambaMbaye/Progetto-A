package menuOnlineGui;

import net.Client;
import dominio.classi_dati.OpzioniMenuOnline;
import dominio.gui.Sfondo;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;

public class MenuPrincipaleOnlineGui extends JFrame implements ActionListener {

    private Sfondo sfondo;
    private JButton login, regis, recupero, indietro;
    private Client client;
    private MenuLogin menu_login;
    private MenuRegistrazione menu_registrazione;
    private MenuRecuperoPassword menu_recupero_passwd;

    /**
     * 
     * @param client oggetto client
     */
    public MenuPrincipaleOnlineGui(Client client) {
        setTitle("Fai il Login o Registrati !");
        setPreferredSize(new Dimension(1000, 800));
        setMinimumSize(new Dimension(1000, 800));
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        this.client = client;

        inizializzaGUI();
        this.setVisible(true);
    }

    private void inizializzaGUI() {
        menu_login = new MenuLogin(client);
        menu_registrazione = new MenuRegistrazione(client);
        menu_recupero_passwd = new MenuRecuperoPassword(client);

        menu_login.addIndietroActionListener(this);
        menu_registrazione.addIndietroActionListener(this);
        menu_recupero_passwd.addIndietroActionListener(this);

        sfondo = new Sfondo("dominio/immagini/sfondo.png", 995, 765);
        sfondo.setBounds(0, 0, 1000, 800);
        add(sfondo);

        login = new JButton(caricaImmagine("dominio/immagini/login.png"));
        regis = new JButton(caricaImmagine("dominio/immagini/registrati.png"));
        recupero = new JButton(caricaImmagine("dominio/immagini/recuperoPw.png"));
        indietro = new JButton(caricaImmagine("dominio/immagini/indietro.png"));

        login.setBounds(this.getWidth() / 2 - 100, 150, 200, 80);
        regis.setBounds(this.getWidth() / 2 - 100, 250, 200, 80);
        recupero.setBounds(this.getWidth() / 2 - 100, 350, 200, 80);
        indietro.setBounds(this.getWidth() / 2 - 100, 600, 200, 80);

        login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runOpzione(OpzioniMenuOnline.Login);
            }
        });

        regis.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runOpzione(OpzioniMenuOnline.Registrazione);
            }
        });

        recupero.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runOpzione(OpzioniMenuOnline.Recupero);
            }
        });

        indietro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.close();
                dispose();
            }
        });

        sfondo.add(login);
        sfondo.add(regis);
        sfondo.add(recupero);
        sfondo.add(indietro);
        sfondo.repaint();
    }

    /**
     * 
     * @param l action listener
     */
    public void addIndietroActionListener(ActionListener l) {
        indietro.addActionListener(l);
    }

    private ImageIcon caricaImmagine(String nome) {
        ClassLoader loader = getClass().getClassLoader();
        URL percorso = loader.getResource(nome);
        return new ImageIcon(percorso);
    }

    private void runOpzione(OpzioniMenuOnline opzione) {
        this.setVisible(false);
        switch (opzione) {
            case Login:
                menu_login.run();
                break;
            case Registrazione:
                menu_registrazione.run();
                break;
            case Recupero:
                menu_recupero_passwd.run();
                break;
        }
    }

    /**
     * 
     * @param e evento
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (menu_login.isVisible()) {
            menu_login.setVisible(false);
        } else if (menu_registrazione.isVisible()) {
            menu_registrazione.setVisible(false);
        } else if (menu_recupero_passwd.isVisible()) {
            menu_recupero_passwd.setVisible(false);
        }
        this.setVisible(true);
    }

}
