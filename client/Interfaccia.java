
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.LinkedList;

/**
 * Interfacccia dell'architettura Client-Server.
 */
public interface Interfaccia extends Remote {
     
    /*-------------- METODI CUCINA -----------------*/
	/* La descrizione dei metodi è nella classe Cucina.java */
    
    String connessioneCuoco() throws RemoteException;
    
    int getIdVassoio() throws RemoteException;
        
    String getListaProdotti() throws RemoteException;
    
    int getListaProdottiSize() throws RemoteException;
    
    String getProdotto(int numero) throws RemoteException;
    
    Object[] iniziaPreparazione(int numero) throws RemoteException;
    
    String finisciPreparazione(Prodotto prodotto, int id, int size, int posizione) throws RemoteException;
    
    String check() throws RemoteException;
    
    String toMagazzino(Prodotto prodotto) throws RemoteException;
    
    /*--------------- METODI SALA ------------------*/
    
    // Metodo che permette di prendere un'ordinazione
    void ordinazione(LinkedList<Prodotto> prodotti, int idTavolo) throws RemoteException;
    
    // Metodo che permette di far comparire sul display il menu' del ristorante
    String menu() throws RemoteException;
    
    // Metodo che permette di consegnare ai tavoli le ordinazioni pronte
    String consegnaOrdinazione() throws RemoteException;

    // Metodo che permette al cameriere di sapere quanti tavoli ci sono all'interno del locale
    int maxTavoli() throws RemoteException; //permette al cameriere di sapere quanti tavoli ci sono all'interno del locale.

    // Metodo che permette al cameriere di verificare se c'è qualcosa pronto
    int ordinazioniCucinate() throws RemoteException;

    // Metodo che ritorna al cameriere l'insieme dei prodotti ordinabili dal cliente
    Prodotto[] menuProdotti() throws RemoteException;
    
    // Metodo che permette ai camerieri di essere aggiornati qualora sia pronta un'ordinazione
    void avviso() throws RemoteException;
    
    // Metodo  che ritorna l'array che tiene conto dei posti occupati
    boolean[] posti() throws RemoteException;
    
    // Metodo che permette al cameriere di controllare se un posto e' occupato
    boolean controlloPosti(int posto) throws RemoteException;
    
    // Metodo che segnala un posto come libero
    void liberaPosto(int posto) throws RemoteException;
}
