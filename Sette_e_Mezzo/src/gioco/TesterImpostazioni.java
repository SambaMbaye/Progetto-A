/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gioco;

import eccezioni.CanzoneNonTrovataException;
import eccezioni.CaricamentoCanzoneException;

/**
 *
 * @author Max & family1
 * 
 */
public class TesterImpostazioni {
    
    public static void main (String[] args) throws InterruptedException, CanzoneNonTrovataException, CaricamentoCanzoneException {
        
        Impostazioni impostazioni = new Impostazioni();
        impostazioni.selezionaImpostazione();
    }
}