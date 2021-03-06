/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DB;

import dominio.eccezioni.SqlOccupato;
import dominio.eccezioni.GiocatoreNonTrovato;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author marco
 */
public class SQL {

    private Connection c = null;
    private Statement stmt = null;

    public SQL() {
        creaTabella();
    }

    private void creaTabella() {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:setteEmezzo.db");
            System.out.println("Database aperto");
            stmt = c.createStatement();
            String sql = "CREATE TABLE GIOCATORI "
                    + "(EMAIL TEXT PRIMARY KEY     NOT NULL,"
                    + "PASSWORD TEXT     NOT NULL,"
                    + "USERNAME TEXT    NOT NULL,"
                    + " FICHES             INT , "
                    + "VITTORIE INT)";
            stmt.executeUpdate(sql);
            chiudiDatabase();
            System.out.println("Tabella creata!");
        } catch (Exception e) {
            chiudiDatabase();
        }
    }

    /**
     * Aggiunge un nuovo giocatore al database
     *
     * @param email email del giocatore
     * @param password password del giocatore
     * @param username usernsme che userà il giocatore
     * @param fiches fiches iniziali
     * @throws dominio.eccezioni.SqlOccupato
     */
    public void aggiungiGiocatore(String email, String password, String username, int fiches) throws SqlOccupato {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:setteEmezzo.db");
            c.setAutoCommit(false);
            String dati = "VALUES ('" + email + "', '" + password + "', '" + username + "', " + fiches + ");";

            stmt = c.createStatement();
            String sql = "INSERT INTO GIOCATORI (EMAIL,PASSWORD,USERNAME, FICHES) "
                    + dati;
            stmt.executeUpdate(sql);
            c.commit();
            chiudiDatabase();
            System.out.println("Giocatore aggiunto correttamente");
        } catch (Exception e) {
            chiudiDatabase();
            if (e.getMessage().equals("[SQLITE_BUSY]  The database file is locked (database is locked)")) {
                throw new SqlOccupato();
            }
            System.out.println("dato già presente");
        }

    }

    /**
     * Consente di modificare le fiches di un giocatore
     *
     * @param user username del giocatore
     * @param fiches nuovo numero di fiches per quel giocatore
     */
    public void setFiches(String user, int fiches) throws SqlOccupato {

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:setteEmezzo.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();
            String dato = "UPDATE GIOCATORI set FICHES =" + fiches + " where USERNAME= '" + user + "';";
            String sql = dato;
            stmt.executeUpdate(sql);
            c.commit();
            chiudiDatabase();
            System.out.println("fiches aggiornate con successo");
        } catch (Exception e) {
            chiudiDatabase();
            if (e.getMessage().equals("[SQLITE_BUSY]  The database file is locked (database is locked)")) {
                throw new SqlOccupato();
            }
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

    }

    /**
     * Torna le fiches di un giocatore
     *
     * @param user username del giocatore
     * @return fiches del giocatore
     * @throws org.sqlite.SQLiteException se il database è occupato
     */
    public int getFiches(String user) throws SqlOccupato {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:setteEmezzo.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM GIOCATORI;");
            while (rs.next()) {
                String username = rs.getString("USERNAME");
                int fiches = rs.getInt("FICHES");
                if (username.equals(user)) {
                    rs.close();
                    chiudiDatabase();
                    return fiches;
                }
            }
            rs.close();
            chiudiDatabase();
        } catch (Exception e) {
            chiudiDatabase();
            if (e.getMessage().equals("[SQLITE_BUSY]  The database file is locked (database is locked)")) {
                throw new SqlOccupato();
            }
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return 0;
    }

    /**
     * Permette di controllare la password di un giocatore
     *
     * @param user username del giocatore
     * @param pw password del giocatore
     * @return ritorna true se la password è giusta
     */
    public boolean controllaPassword(String user, String pw) throws SqlOccupato {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:setteEmezzo.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM GIOCATORI;");
            while (rs.next()) {
                String username = rs.getString("USERNAME");
                String password = rs.getString("PASSWORD");
                if (username.equals(user)) {
                    if (pw.equals(password)) {
                        rs.close();
                        chiudiDatabase();
                        return true;
                    } else {
                        rs.close();
                        chiudiDatabase();
                        return false;
                    }

                }
            }
            rs.close();
            chiudiDatabase();
        } catch (Exception e) {
            chiudiDatabase();
            if (e.getMessage().equals("[SQLITE_BUSY]  The database file is locked (database is locked)")) {
                throw new SqlOccupato();
            }
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return false;
    }

    /**
     * Cambia la password di un giocatore
     *
     * @param user username del giocatore
     * @param vecchiaPassword vecchia password del giocatore
     * @param nuovaPassword nuova password del giocatore
     * @return true se l'ha cambiata false se la vecchia è errata
     */
    public boolean cambiaPassword(String user, String vecchiaPassword, String nuovaPassword) throws SqlOccupato {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:setteEmezzo.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM GIOCATORI;");
            while (rs.next()) {
                String username = rs.getString("USERNAME");
                String password = rs.getString("PASSWORD");
                if (username.equals(user)) {
                    if (vecchiaPassword.equals(password)) {
                        String dato = "UPDATE GIOCATORI set PASSWORD ='" + nuovaPassword + "' where USERNAME= '" + user + "';";
                        String sql = dato;
                        stmt.executeUpdate(sql);
                        c.commit();
                        rs.close();
                        chiudiDatabase();
                        return true;
                    } else {
                        rs.close();
                        chiudiDatabase();
                        return false;
                    }

                }
            }
            rs.close();
            chiudiDatabase();
        } catch (Exception e) {
            chiudiDatabase();
            if (e.getMessage().equals("[SQLITE_BUSY]  The database file is locked (database is locked)")) {
                throw new SqlOccupato();
            }
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return false;
    }

    /**
     * Recupera la password di un utente
     *
     * @param user username giocatore
     * @return password del Giocatore
     */
    public String getPassword(String user) throws SqlOccupato {
        try {
            if(user.contains("@"))
                user=getUser(user);
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:setteEmezzo.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM GIOCATORI;");
            while (rs.next()) {
                String username = rs.getString("USERNAME");
                String password = rs.getString("PASSWORD");
                if (username.equals(user)) {
                    rs.close();
                    chiudiDatabase();
                    return password;
                }
            }
            rs.close();
            chiudiDatabase();
        } catch (Exception e) {
            chiudiDatabase();
            if (e.getMessage().equals("[SQLITE_BUSY]  The database file is locked (database is locked)")) {
                throw new SqlOccupato();
            }
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return null;

    }

    /**
     * Permette di avere l'username del giocatore partendo dall'email
     *
     * @param email email del giocatore
     * @return username nel giocatore
     * @throws dominio.eccezioni.GiocatoreNonTrovato se la mail non è nel database
     */
    public String getUser(String email) throws GiocatoreNonTrovato, SqlOccupato {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:setteEmezzo.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM GIOCATORI;");
            while (rs.next()) {
                String username = rs.getString("USERNAME");
                String mail = rs.getString("EMAIL");
                if (mail.equals(email)) {
                    rs.close();
                    chiudiDatabase();
                    return username;
                }
            }
            rs.close();
            chiudiDatabase();
        } catch (Exception e) {
            chiudiDatabase();
            if (e.getMessage().equals("[SQLITE_BUSY]  The database file is locked (database is locked)")) {
                throw new SqlOccupato();
            }
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        throw new GiocatoreNonTrovato();
    }

    /**
     * Consente di controllare l'esistenza di una email nel database
     *
     * @param email email da controllare
     * @return true se esiste, false altrimenti
     */
    public boolean esisteEmail(String email) throws SqlOccupato {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:setteEmezzo.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM GIOCATORI;");
            while (rs.next()) {
                String mail = rs.getString("EMAIL");
                if ((mail.toLowerCase()).equals(email.toLowerCase())) {
                    return true;
                }

            }
            rs.close();
            chiudiDatabase();
        } catch (Exception e) {
            chiudiDatabase();
            if (e.getMessage().equals("[SQLITE_BUSY]  The database file is locked (database is locked)")) {
                throw new SqlOccupato();
            }
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return false;
    }

    /**
     * Consente di controllare l'esistenza di un username nel database
     *
     * @param username username da controllare
     * @return true se esiste, false altrimenti
     */
    public boolean esisteUsername(String username) throws SqlOccupato {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:setteEmezzo.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM GIOCATORI;");
            while (rs.next()) {
                String user = rs.getString("USERNAME");
                if ((user.toLowerCase()).equals(username.toLowerCase())) {
                    chiudiDatabase();
                    return true;
                }

            }
            rs.close();
            chiudiDatabase();
        } catch (Exception e) {
            chiudiDatabase();
            if (e.getMessage().equals("[SQLITE_BUSY]  The database file is locked (database is locked)")) {
                throw new SqlOccupato();
            }
            System.err.println(e.getClass().getName() + ": " + e.getMessage());

        }
        return false;
    }

    /**
     * Aggiunge una vittoria al giocatore
     *
     * @param user username del giocatore
     */
    public void aggiungiVittoria(String user) throws GiocatoreNonTrovato, SqlOccupato {
        int vittorie = 0;
        boolean trovato = false;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:setteEmezzo.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM GIOCATORI;");
            while (rs.next()) {
                String username = rs.getString("USERNAME");
                int vit = rs.getInt("VITTORIE");
                vit++;
                if (username.equals(user)) {
                    vittorie = vit;
                    trovato = true;
                }
            }
            String dato = "UPDATE GIOCATORI set VITTORIE =" + vittorie + " where USERNAME='" + user + "';";
            String sql = dato;
            stmt.executeUpdate(sql);
            c.commit();
            chiudiDatabase();
            System.out.println("aggiunta vittoria");
        } catch (Exception e) {
            chiudiDatabase();
            if (e.getMessage().equals("[SQLITE_BUSY]  The database file is locked (database is locked)")) {
                throw new SqlOccupato();
            }
            System.err.println(e.getClass().getName() + ": " + e.getMessage());

        }
        if (!trovato) {
            throw new GiocatoreNonTrovato();
        }
    }

    /**
     * Ritorna le vittorie del giocatore
     *
     * @param user username del giocatore
     * @return vittorie del giocatore
     */
    public int getVittorie(String user) throws SqlOccupato {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:setteEmezzo.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM GIOCATORI;");
            while (rs.next()) {
                String username = rs.getString("USERNAME");
                int vittorie = rs.getInt("VITTORIE");
                if (username.equals(user)) {
                    rs.close();
                    chiudiDatabase();
                    return vittorie;
                }
            }
            rs.close();
            chiudiDatabase();
        } catch (Exception e) {
            chiudiDatabase();
            if (e.getMessage().equals("[SQLITE_BUSY]  The database file is locked (database is locked)")) {
                throw new SqlOccupato();
            }
            System.err.println(e.getClass().getName() + ": " + e.getMessage());

        }
        return 0;
    }

    private void chiudiDatabase() {
        try {
            stmt.close();
            c.close();
        } catch (SQLException ex) {
            Logger.getLogger(SQL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * ritorna l'email dell'utente data l'username
     * 
     * @param username user dell'utente
     * @return email utente
     * @throws SqlOccupato lanciata quando il database non è accessibile
     */
    public String getEmail(String username) throws SqlOccupato {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:setteEmezzo.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM GIOCATORI;");
            while (rs.next()) {
                String user = rs.getString("USERNAME");
                String mail = rs.getString("EMAIL");
                if (user.equals(username)) {
                    rs.close();
                    chiudiDatabase();
                    return mail;
                }
            }
            rs.close();
            chiudiDatabase();
        } catch (Exception e) {
            chiudiDatabase();
            if (e.getMessage().equals("[SQLITE_BUSY]  The database file is locked (database is locked)")) {
                throw new SqlOccupato();
            }
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return null;
    }

}
