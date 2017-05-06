
import java.util.LinkedList;

/**
 * Classe che definisce il tipo di coda che verra' utilizzata nel programma.
 * La coda in questione e' una coda di tipo FIFO, First In First Out.
 * @param <T> oggetto che andra' a contenere
 */
public class Coda<T> {
    
    private LinkedList<T> list = new LinkedList();
    
    public Coda() {
        list = new LinkedList();
    }

    /**
     * Metodo che estrae il primo oggetto nella coda.
     * @return T primo oggetto della coda
     */
    public synchronized T getFirst() {
        try {
            while (list.isEmpty()) {
                wait();
            }
        } catch (InterruptedException ex) {
        }
        return list.remove(); // Estrazione del primo elemento
    }

    /**
     * Metodo che ritorna la lista intera.
     * @return list lista intera
     */
    public synchronized LinkedList<T> lista() {
        return list;
    }
    
    /**
     * Metodo che legge il primo elemento della coda.
     * @return T primo elemento della coda
     */
    public synchronized T readFirst() {
        try {
            while (list.isEmpty()) {
                wait();
            }
        } catch (InterruptedException ex) {
        }
        return list.peek(); // Lettura del primo elemento
    }
    
    /**
     * Metodo che inserisce un oggetto nella lista.
     * @param element oggetto da inserire
     * @return 
     */
    public synchronized boolean push(T element) {
        list.add(element);
        //Viene notificato ad eventuali thread in attesa che è stato aggiunto un elemento alla lista
        notifyAll();
        return true;
    }

    /**
     * Metodo che verifica se la coda e' vuota o no.
     * @return boolean
     */
    public synchronized boolean isEmpty() {
        return (list.isEmpty());
    }

    /**
     * Metodo che rimuove il primo elemento della lista senza visualizzare nulla.
     */
    public synchronized void removeFirst() {
        list.remove();
    }
    
    /**
     * Metodo che ritorna il numero di elementi presenti nella lista.
     * @return int numero di elementi
     */
    public synchronized int size() {
        return list.size();
    }
}
