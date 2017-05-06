
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;

/**
 * Classe Metodi, raccoglie tutti i metodi utilizzati dal programma.
 */
public class Metodi extends UnicastRemoteObject implements Interfaccia {
    
    final private int maxTavoli = 5; // Numero totale dei tavoli disponibili nel ristorante
    protected static Cucina cucina = new Cucina();
    protected static Coda<Tavolo> ordinazioniCucinate; // Coda delle ordinazioni pronte
    protected static boolean[] postiOccupati; // Array che tiene conto dei tavoli che si stanno servendo
    
    public Metodi() throws RemoteException, InterruptedException {
        super();
        // Creazione delle code
        cucina.ordinazioniInCucina = new Coda(); // Coda delle ordinazioni che dovranno cucinare i cuochi
        ordinazioniCucinate = new Coda(); // Coda delle ordinazioni cucinate dai cuochi, pronte per essere servite
        postiOccupati = new boolean[maxTavoli];
    }

    /*--------------------------------- METODI CUCINA ------------------------------------*/
    /* I seguenti metodi richiamano a loro volta i metodi corrispondenti nella classe Cucina.
    Per la loro descrizione si faccia quindi riferimento a quella classe. */
    
    public String connessioneCuoco() throws RemoteException {
        return cucina.connessioneCuoco();
    }

    public int getIdVassoio() throws RemoteException {
        return cucina.getIdActualVassoio();
    }

    public synchronized int getListaProdottiSize() throws RemoteException {
        return cucina.getListaProdottiSize();
    }

    public synchronized Object[] iniziaPreparazione(int numero) throws RemoteException {
        return cucina.iniziaPreparazione(numero);
    }

    public synchronized String getProdotto(int numero) throws RemoteException {
        return cucina.getProdotto(numero);
    }

    public String finisciPreparazione(Prodotto prodotto, int id, int size, int posizione) throws RemoteException {
        return cucina.concludiPreparazione(prodotto, id, size, posizione);
    }

    public String check() {
        return cucina.check();
    }

    public String getListaProdotti() throws RemoteException {
        return cucina.ordinazione();
    }
    
    public String toMagazzino(Prodotto prodotto) throws RemoteException {
        return cucina.toMagazzino(prodotto);
    }

    /*--------------------------------- METODI SALA ------------------------------------*/
    /**
     * Metodo invocato dal cameriere quando un tavolo esaudisce le sue richieste.
     * Ha la funzione di mettere in coda l'ordinazione appena presa.
     * @param prodotti elenco di prodotti ordinati da un tavolo
     * @param idTavolo numero del tavolo associato a quell'ordinazione
     */
    public synchronized void ordinazione(LinkedList<Prodotto> prodotti, int idTavolo) {
        Sala.ordinazione(prodotti, idTavolo);
    }

    /**
     * Metodo che permette al cameriere di consegnare la prima ordinazione pronta al tavolo corrispondente.
     * @return String conferma di presa in consegna dell'ordinazione, con relativo numero di tavolo associato
     */
    public synchronized String consegnaOrdinazione() {
        return Sala.consegnaPasti();
    }

    /**
     * Metodo che permette al cameriere di sapere quanti tavoli ci sono all'interno del locale.
     * @return Integer numero di tavoli a disposizione nel ristorante
     */
    public int maxTavoli() {
        return maxTavoli;
    }

    /**
     * Metodo che permette di sapere quante ordinazioni sono state cucinate.
     * @return int numero di ordinazioni pronte
     */
    public int ordinazioniCucinate() {
        return ordinazioniCucinate.size();
    }

    /**
     * Metodo che ritorna in un array l'insieme delle pietanze e delle bevande trasformate da stringhe ad oggetti Prodotto.
     * @return Prodotto[] array contente gli elementi del menu' salvati come oggetto Prodotto
     */
    public Prodotto[] menuProdotti() {
        return Server.menu;
    }
    
    /**
     * Metodo che ritorna sottoforma di stringa il menu' del ristorante.
     * @return String menu' del ristorante
     */
    public String menu() {
        return Server.prodo;
    }
    
    /**
     * Metodo che si occupa di inviare una notifica ai camerieri qualora un'ordinazione venga completata in cucina
     */
    public void avviso() throws RemoteException {
        Sala.avviso();
    }
    
    /**
     * Metodo che ritorna l'array booleano che rappresenta il servizio attuale in sala.
     * @return postiOccupati simulazione di tavoli in sala
     */
    public boolean[] posti() {
        return postiOccupati;
    }
    
    /**
     * Metodo utilizzato per controllare che il tavolo che si sta per servire non sia gia' stato servito.
     * @param posto tavolo che si intende servire
     * @return booleano che indica se il tavolo e' servito oppure no
     */
    public synchronized boolean controlloPosti(int posto) {
        return Sala.controlloPosti(posto);
    }
    
    /**
     * Metodo che segnala un tavolo come non servito.
     * @param posto tavolo che si vuole segnalare come non servito
     */
    public void liberaPosto(int posto) {
        Sala.liberaPosto(posto);
    }
}
