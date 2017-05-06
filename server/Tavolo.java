
import java.util.LinkedList;

/**
 * Classe che permette di creare un oggetto Tavolo, il quale rappresenta un'ordinazione.
 * @author Flavio Colonna, Riccardo Zandegiacomo
 */
public class Tavolo {
    // private LinkedList<Prodotto> ordinazione;
    private LinkedList<Prodotto> prodottiDaPreparare;
    private int idTavolo;
    
    /**
     * Costruttore dell'oggetto Tavolo caratterizzato dall'insieme dei prodotti e dall'id del tavolo.
     * @param lista lista di prodotti da preparare
     * @param idTavolo numero del tavolo associato all'ordinazione
     */
    public Tavolo(LinkedList<Prodotto> lista, int idTavolo) {
        this.idTavolo = idTavolo;
        this.prodottiDaPreparare = lista;
        // this.ordinazione = lista;
    }
    
    /**
     * Metodo estrattore che ritorna la lista dei prodotti ordinati.
     * @return ordinazione lista dei prodotti
     */
    public LinkedList<Prodotto> getListaProdotti() {
        return prodottiDaPreparare;
    }
    
    /**
     * Metodo che ritorna l'id del tavolo associato all'ordinazione.
     * @return id del tavolo
     */
    public int getId() {
        return idTavolo;
    }
}
