
import java.rmi.RemoteException;

/**
 * Classe che si occupa di controllare se ci sono ordinazioni pronte in cucina.
 * In caso affermativo viene prelevato il primo elemento dalla coda.
 * @author Flavio Colonna, Riccardo Zandegiacomo
 */
public class Consegna implements Runnable {

    public void run() {
        try {
            if (Cameriere.metodi.ordinazioniCucinate() > 0) {
                // Esiste un'ordinazione pronta, la prendo in consegna
                System.out.println(Cameriere.metodi.consegnaOrdinazione());
                System.out.println("Ordinazione presa in consegna!");
                
                // Visualizzazione Display
                Thread display = new Thread(new Display());
                display.start();
            } else {
                System.out.println("\nImpossibile consegnare vassoi, nessuna ordinazione pronta in cucina.");
                Thread display = new Thread(new Display());
                display.start();
            }
        } catch (RemoteException ex) {
            System.out.println(ex);
            // Chiusura Client
            System.exit(0);
        }
    }
}
