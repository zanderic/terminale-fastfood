
import java.io.Serializable;

/**
 * Classe che definisce l'oggetto Prodotto, i suoi attributi e i metodi necessari alla sua gestione,
 * sia in cucina da parte dei camerieri, che in sala da parte dei cuochi.
 */
public class Prodotto implements Serializable {

    // Attributi del prodotto
    private int indice;
    private String tipo;
    
    // Stato del prodotto
    private boolean preparing;
    private boolean ready;
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Oggetto Prodotto, costituito principalmente da un indice e da un nome.
     * @param indice indice del file lista.txt
     * @param tipo nome reale del prodotto
     */
    public Prodotto(int indice, String tipo) {
        this.indice = indice;
        this.tipo = tipo;
        this.preparing = false;
        this.ready = false;
    }
    
    /**
     * Metodo che ritorna descrizione del prodotto.
     * @return String descrizione prodotto
     */
    public String getProdotto() {
        return indice + ": " + tipo;
    }
    
    /**
     * Metodo che ritorna il nome del prodotto.
     * @return String nome del prodotto
     */
    public String getTipo() {
        return tipo;
    }
    
    /**
     * Metodo che ritorna l'indice del prodotto.
     * @return String nome del prodotto
     */
	public int getIndice() {
        return indice;
	}
    
    /**
     * Metodo estrattore, mi ritorna l'attributo preparing di un Prodotto
     * @return preparing boolean
     */
    public boolean getPreparing() {
        return preparing;
    }
    
    /**
     * Metodo che modifica stato di un prodotto mettendolo in preparazione.
     */
    public void isPreparing(){
        this.preparing = true;
    }
    
    /**
     * Metodo che definisce pronto un Prodotto, pronto da servire al tavolo.
     */
    public void isReady(){
        this.ready = true;
    }
}
