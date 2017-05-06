
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;

/**
 * Classe che rappresenta l'inizio del turno di un Cuoco.
 * Viene stabilita la connessione con il Server.
 * @author Antonio Faienza, Mirco Lacalandra
 */
public class ClientCuoco {
    
    static Interfaccia metodi;

    public static void main(String args[]) throws NotBoundException, MalformedURLException, RemoteException, UnknownHostException {
        
        // Settaggio policy
        System.setProperty("java.security.policy", "rmi.policy");
        
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }
		
		/* Creazione della stringa per indirizzare la connessione al server.
         * La porta ed il nome dell'oggetto remoto sono sempre le stesse.
         * L'hostname del server verrà inserito manualmente dall'utente.
         */
        String name = "rmi://" + args[0] + ":" + 1099 + "/Metodi";
        
        /*Naming è una classe che si occupa di istanziare un oggetto Registry e associargli
        L'oggetto remoto presente all'url inserito (name) */
        metodi = (Interfaccia) Naming.lookup(name);
        System.out.println("ClientCuoco Connesso!");

        // Avviamento del thread Cuoco
        Thread cuoco = new Thread(new Cuoco());
        cuoco.start();
    }
}
