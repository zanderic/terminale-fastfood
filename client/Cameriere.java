
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.util.Scanner;

/**
 * Classe che rappresenta l'inizio del turno di un cameriere. Essenzialmente questa figura puo' fare due cose:
 * 1: Prendere un'ordinazione e subito dopo trasmetterla in cucina
 * 2: Servire ai tavoli le ordinazioni pronte
 * Oltre a gestire il collegamento con il server, questa classe si occupa della visualizzazione del Display e della
 * gestione degli avvisi provenienti dalla cucina.
 * @author Flavio Colonna, Riccardo Zandegiacomo
 */
public class Cameriere {

    static Interfaccia metodi;
    public static Scanner tastiera;
    public static int maxTavoli;
    public static Prodotto[] prodotti;
    public static String menu;
    
    public static void main(String args[]) throws NotBoundException, MalformedURLException, RemoteException {
        
        // Settaggio policy
        System.setProperty("java.security.policy", "rmi.policy");
        
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }
        
        // Nome maccchina e porta per stabilire la connessione
        String name = "rmi://" + args[0] + ":" + 1099 + "/Metodi";
        
        metodi = (Interfaccia) Naming.lookup(name);
        tastiera = new Scanner(System.in);
        System.out.println("Collegamento effettuato!");
        
        // Preparazione del Display del terminale e sua visualizzazione
        maxTavoli = metodi.maxTavoli();
        prodotti = metodi.menuProdotti();
        menu = metodi.menu();
        
        // Avviamento del Display
        System.out.println("\nNumero di tavoli disponibili nel ristorante: " + maxTavoli);
        Thread display = new Thread(new Display());
        display.start();
        
        // Thread che si occupa di gestire gli avvisi provenienti dalla cucina riguardo ordinazioni pronte da servire
        Thread controlloCucina = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        Cameriere.metodi.avviso();
                    } catch (RemoteException ex) {
                        System.out.println(ex);
                        // Chiusura Client
                        System.exit(0);
                    }
                    System.out.println("\n*************** AVVISO ***************\n"
                            + "* Ordinazione completata in cucina!\n* Per ritirarla digitare il numero 2 nel menu' principale.\n"
                            + "**************************************\n");
                }
            }
        });
        controlloCucina.start();
    }
}
