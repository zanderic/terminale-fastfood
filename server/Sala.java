
import java.util.LinkedList;

/**
 * Classe che raccoglie tutti i metodi riguardanti la sala, ovvero metodi che vengono utilizzati
 * nell'interazione tra cameriere e cliente.
 * @author Flavio Colonna, Riccardo Zandegiacomo
 */
public class Sala {
    
    /**
     * Metodo invocato dal cameriere quando un tavolo esaudisce le sue richieste.
     * Ha la funzione di mettere in coda l'ordinazione appena presa.
     * @param prodotti elenco di prodotti ordinati da un tavolo
     * @param idTavolo numero del tavolo associato a quell'ordinazione
     */
    public static synchronized void ordinazione(LinkedList<Prodotto> prodotti, int idTavolo) {
        // Utilizzo la classe Tavolo per creare una nuova ordinazione, caratterizzata dai prodotti da preparare e dal numero del tavolo
        Tavolo tav = new Tavolo(prodotti, idTavolo);
        // Invio l'ordinazione in cucina
        Metodi.cucina.ordinazioniInCucina.push(tav);
        // Segnalazione di fine servizio al tavolo
        liberaPosto(idTavolo);
    }
    
    /**
     * Metodo che permette al cameriere di consegnare la prima ordinazione pronta al tavolo corrispondente.
     * @return String conferma di presa in consegna dell'ordinazione, con relativo numero di tavolo associato
     */
    public static synchronized String consegnaPasti() {
        String risp = "";
        risp = "\nConsegno al tavolo numero " + Metodi.ordinazioniCucinate.getFirst().getId() + " la sua ordinazione...";
        return risp;
    }

    /**
     * Metodo che si occupa di inviare una notifica ai camerieri qualora un'ordinazione venga completata in cucina.
     */
    public static void avviso() {
        synchronized (Metodi.ordinazioniCucinate) {
            try {
                Metodi.ordinazioniCucinate.wait();
            } catch (InterruptedException ex) {
                System.out.println("InterruptedException: " + ex);
            }
        }
    }
    
    /**
     * Metodo che controlla se un tavolo e' gia' servito o meno.
     * Se non lo e' quel posto viene settato a false e viene ritornato true.
     * @param posto tavolo da servire
     * @return boolean risultato del controllo
     */
    public static synchronized boolean controlloPosti(int posto) {
        if (Metodi.postiOccupati[posto - 1] == false) {
            Metodi.postiOccupati[posto - 1] = true;
            return true;
        }
        return false;
    }
    
    /**
     * Metodo che segnala un tavolo come non piu' servito settandolo a false.
     * @param posto tavolo da liberare
     */
    public static void liberaPosto(int posto) {
        Metodi.postiOccupati[posto - 1] = false;
    }
}
