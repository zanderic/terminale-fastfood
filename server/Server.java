import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Classe Server, si occupa del collegamento dell'architettura client-server,
 * del caricamento in memoria del menu' dal file lista.txt e della
 * trasformazione di quest'ultimo da array di semplici int a array di oggetti
 * Prodotto veri e propri.
 * @author Flavio Colonna, Mirco Lacalandra
 */
public class Server
{
    public static Prodotto[] menu;
    public static String prodo;

    public static void main(String ar[]) throws RemoteException, MalformedURLException, AlreadyBoundException, InterruptedException, FileNotFoundException, ServerNotActiveException, IOException 
    {
        // Creazione registro
        java.rmi.registry.LocateRegistry.createRegistry(1099);
        // Settaggio policy
        System.setProperty("java.security.policy", "rmi.policy");
        
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }

        /* Acquisizione hostname del computer
         * N.B. riguardo la comunicazione Client-Server in sistemi distribuiti:
         * nelle macchine di laboratorio come indirizzo esterno si può usare il nome del pc o l'indirizzo ethernet.
         * Dato che risalire all'indirizzo ethernet IPV4 è abbastanza complicato, per semplificare abbiamo usato
         * Ip.getHostName(), che legge e rileva il nome della postazione (es. mercedes, doncurzio, frasquita..).
         * In questo modo l'utente non deve inserire manualmente nessun indirizzo ed il tutto avviene automaticamente.
         */
        InetAddress IP = InetAddress.getLocalHost();
        String pcServer = IP.getHostName();
        
        System.setProperty("java.rmi.server.hostname", pcServer);

        // Informazioni su URL RMI
        String name = "rmi://" + pcServer + "/Metodi";
        // System.out.println("url -> " + name);

        // Registrazione oggetto remoto
        Naming.rebind(name, new Metodi());
        System.out.println("Security manager configurato!");

        // Caricamento del menu' trascritto in lista.txt
        Scanner lista = new Scanner(new File("lista.txt"));
        menu = new Prodotto[12];
        int i = 0;
        StringBuilder men = new StringBuilder();
        // Trasformazione degli elementi del menu' da semplici stringhe ad oggetti Prodotto
        while (lista.hasNextLine())
        {
            String linea = lista.nextLine();
            List<String> prodotto = Arrays.asList(linea.split(" "));
            int indice = Integer.parseInt(prodotto.get(0));
            String tipo = "";
            for (int k = 1; k < prodotto.size(); k++) {
                tipo = tipo.concat(prodotto.get(k) + " ");
            }
            menu[i] = new Prodotto(indice, tipo);
            men.append(indice).append(": ").append(tipo).append("\n");
            i++;
        }
        // Questa stringa verra' inviata al client poiche' rappresenta il menu' per prendere le ordinazioni
        prodo = men.toString();
        System.out.println("Server pronto!");
    }
}
