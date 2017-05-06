
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Scanner;

/**
 * Classe che gestisce la preparazione dei prodotti in cucina da parte del cuoco.
 * @author Antonio Faienza, Mirco Lacalandra
 */
public class Cuoco implements Runnable {

    private Scanner input = new Scanner(System.in);
    private String a;

    // Questi booleani servono a gestire i vari cicli durante l'interazione con l'utente
    private boolean on = true;   // Booleano che decreta la fine del thread
    private boolean onNew = true; //Booleano che permette di tornare o non tornare al menu iniziale
    private boolean once = true; // Booleano che decide l'attivazione del servizio notifica
    private boolean auto = false; // Booleano che permette di saltare il menu iniziale 

    public void run() {

        System.out.println("Benvenuto nel client Cuoco!");

        while (on) {

            try {

                if (auto) {
                    /*se auto è true, 'a' viene settato automaticamente ad uno 
                    (senza bisogno dell' input utente).
                    Ciò consentirà di mostrare immediatamente la lista dei prodotti.*/
                    a = "1";
                    auto = false;
                } else {

                    System.out.println(ClientCuoco.metodi.connessioneCuoco()); //messaggio di benvenuto

                    a = input.next().toLowerCase(); //input utente per proseguire

                    onNew = true;

                }

                while (onNew) {

                    if (a.equalsIgnoreCase("q")) {
                        //uscita dal programma
                        on = false;
                        break;
                    } else if (a.equalsIgnoreCase("1")) {
                        
                        //Viene memorizzato l'id del vassoio attuale (servirà per successive verifiche
                        int idAttuale = ClientCuoco.metodi.getIdVassoio();

                        //Il server comunica al client quali sono i prodotti richiesti
                        String risposta = ClientCuoco.metodi.getListaProdotti();

                        //controllo che nel frattempo il vassoio non si sia svuotato
                        if (vassoioSvuotato()) {
                            onNew = false;
                            break;
                        }

                        //stampo i prodotti richiesti dal server nel client
                        System.out.println(risposta);

                        /*memorizzo la dimensione attuale del vassoio ( mi servirà per effettuare
                        successive verifiche )
                        Il motivo di queste verifiche è che il cuoco, per la preparazione dei prodotti, sarà
                        vincolato strettamente ai prodotti richiesti.
                        Se, successivamente, la dimensione della lista cambierà, il cuoco sarà invitato
                        a confermare la sua scelta riguardante la preparazione di un determinato prodotto*/
                        int size = ClientCuoco.metodi.getListaProdottiSize();

                        boolean on2 = true;

                        while (on2) {

                            //l'utente sceglie quale prodotto preparare (tra quelli richiesti)
                            String pos = input.next();

                            //controllo che nel frattempo non ci sia stato un cambio di vassoio
                            if (idAttuale != ClientCuoco.metodi.getIdVassoio()) {
                                System.out.println("Si prega di selezionare nuovamente il prodotto a causa di un cambio di vassoio");
                                auto = true;
                                break;
                            }

                            //Controllo che il valore inserito sia corretto
                            while (!isInteger(pos)) {
                                System.out.println("Inserire un numero valido");
                                pos = input.next();
                            }
                            int posizione = Integer.valueOf(pos);

                            /*N.B. Il cuoco può scegliere di preparare un prodotto solo dalla lista
                             di quelli richiesti. Il cuoco NON PUò prepararne uno a suo piacimento. Può
                             solo attenersi alla lista che viene proposta dal server. 
                            ---------------------------------------------------------------------------
                             Ancora una volta, viene controllato se il vassoio è stato completato.
                             Viene controllato anche che non sia cambiata la dimensione del vassoio; 
                             Questo perchè, in caso la dimensione del vassoio sia cambiata, il client potrebbe
                             ricevere risposte inattese dal server (es. mancata corrispondenza indice - prodotto)
                             */
                            if (vassoioSvuotato() || statoVassoioCambiato(size)) {
                                onNew = false;
                                break;
                            }

                            //Ultimo controllo
                            /*
                            Controllo che il valore inserito corrisponda ad un indice 
                            esistente nella lista dei prodotti richiesti.
                            */
                            if (posizione >= size) {
                                System.out.println("Errore : Selezione non valida");
                                break;
                            }

                            boolean on3 = true;

                            while (on3) {

                                //Stampa di verifica del prodotto selezionato
                                String prodotto = ClientCuoco.metodi.getProdotto(posizione);
                                System.out.println("Stai per preparare " + prodotto);

                                if (posizione < size) {

                                    System.out.println("Digita 1 per confermare la preparazione, altrimenti q per annullare");
                                    String scelta = input.next().toLowerCase();

                                    /*
                                    Vengono effettuati nuovamente diversi controlli
                                    - il vassoio non deve essersi svuotato
                                    - il vassoio non deve aver cambiato la sua dimensione
                                    - il vassoio non deve essere stato cambiato con un nuovo vassoio
                                    */
                                    if (vassoioSvuotato() || statoVassoioCambiato(size)) {
                                        onNew = false;
                                        break;
                                    }
                                    if (idAttuale != ClientCuoco.metodi.getIdVassoio()) {
                                        System.out.println("Si prega di selezionare nuovamente il prodotto a causa di un cambio di vassoio");
                                        break;
                                    }
                                    // Fine controlli.
                                    
                                    
                                    if (scelta.equals("1")) {

                                        System.out.println("Stai preparando -> " + prodotto);
                                        System.out.println("Controllo il magazzino....");

                                        /*Il server invia un array al client.
                                         In prosizione 0 ci sarà il prodotto da preparare.
                                         In posizione 1 ci sarà una stringa che dirà al client se il prodotto
                                         è disponibile in magazzino o no. */
                                        Object[] check = ClientCuoco.metodi.iniziaPreparazione(posizione);
                                        Prodotto pr = (Prodotto) check[0];

                                        if (check[1].equals("MAG")) {
                                            System.out.println("il prodotto era in magazzino. verrà usato quello");
                                            break;
                                        } else {
                                            System.out.println("Il prodotto e' in preparazione \nPremi ENTER quando hai finito di cucinare");
                                        }
                                        System.in.read(); //attesa conferma utente

                                        //Controllo che nel frattempo il vassoio non si sia svuotato
                                        /*N.B. Se si è svuotato, il prodotto preparato viene inviato al server che 
                                         provvede a riporlo in magazzino.
                                         */
                                        if (vassoioSvuotato(pr)) {
                                            onNew = false;
                                            break;
                                        }

                                        /* Parte conclusiva della comunicazione client-server. 
                                         Il client invia al server il prodotto 'completato'
                                         Inoltre invia l'id del vassoio a cui stava lavorando (utile x confronto nel caso, nel
                                         frattempo, l'id sia cambiato, ovvero il vassoio sia stato ultimato), 
                                         la dimensione del vassoio e la posizione del prodotto (utili a confronti nel caso lo stato
                                         del vassoio sia cambiato, in modo tale da evitare errori di interazione col server).
                                         */
                                        String fine = ClientCuoco.metodi.finisciPreparazione(pr, idAttuale, size, posizione);
                                        System.out.println(fine);

                                        on3 = false;
                                        onNew = false;
                                    } else if (scelta.equalsIgnoreCase("q")) {
                                        on3 = false;
                                        onNew = false;
                                    }
                                } else {
                                    System.out.println("Errore, inserire un numero valido");
                                }
                            }
                            break;
                        }

                    } else {
                        break;
                    }
                }

            } catch (RemoteException ex) {
                System.out.println(ex + "\nRemoteException :(\n Il server ha terminato la comunicazione.");
                System.exit(0);
            } catch (IOException ex) {
                System.out.println(ex + "\n IOException :(\n");
                System.exit(0);
            }
        }
        System.out.println("Chiusura in corso.");
        System.exit(0);
    }


    /**
     * Metodo che verifica che la stringa inserita equivalga ad un numero intero
     * @param s
     * @return 
     */
    protected static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /*
     * Metodo che verifica che il Server abbia dei vassoi su cui lavorare, piu' precisamente
     * serve a controllare il vassoio PRIMA che il prodotto venga ultimato.
     * 
     * Nel caso il Server non abbia vassoi e se e' la prima volta che viene
     * lanciato questo metodo, verra' attivato il 'servizio notifiche', che notifichera'
     * ai client Cameriere che hanno fatto richiesta quando sara' possibile lavorare
     * ad un nuovo vassoio.
     */
    private boolean vassoioSvuotato() throws RemoteException {
        String risposta = ClientCuoco.metodi.getListaProdotti();

        if (risposta.equals("empty")) {
            System.out.println("\n********************\nNessuna ordinazione in attesa"
                    + ". Riprova più tardi.\n********************\n");
            /* Viene avviato il thread check, che si occupera' di avvisare il Client
             * quando sara' disponibile un vassoio sul lato server.
             * N.B. il servizio notifica viene attivato UNA VOLTA SOLTANTO per ogni client.
             */
            if (once) {
                new check().start();
                once = false;
            }
            return true;
        } else {
            return false;
        }
    }

    /*
     * Metodo che verifica che il Server abbia dei vassoi su cui lavorare.
     * piu' precisamente, serve a controllare che il vassoio non si sia svuotato MENTRE 
     * il cuoco preparava il prodotto.
     * Nel caso il Server non abbia vassoi e se e' la prima volta che viene
     * lanciato questo metodo, verra' attivato il 'servizio notifiche', che notifichera'
     * ai client Cameriere che hanno fatto richiesta quando sara' possibile lavorare
     * ad un nuovo vassoio.
     */
    private boolean vassoioSvuotato(Prodotto pr) throws RemoteException {
        // Caso vassoio vuoto
        String risposta = ClientCuoco.metodi.getListaProdotti();

        if (risposta.equals("empty")) {
            System.out.println("\n********************\nNessuna ordinazione in attesa"
                    + ". Riprova piu' tardi.\n********************\n");
            /* Viene avviato il thread check, che si occupera' di avvisare il Client
             * quando sara' disponibile un vassoio sul lato server.
             * N.B. il servizio notifica viene attivato UNA VOLTA SOLTANTO per ogni client.
             */
            System.out.println(ClientCuoco.metodi.toMagazzino(pr));
            if (once) {
                new check().start();
                once = false;
            }
            return true;
        } else {
            return false;
        }
    }

    /* Metodo che controlla che sul lato Server il vassoio attuale non sia cambiato di dimensione.
     * Utile per evitare problemi di interazione Client-Server
     * Esempio: il client vuole preparare il prodotto X, in posizione 3,
     * ma nel frattempo sul Server il vassoio viene svuotato, e in posizione 3 
     * compare l'elemento Y.
     * Il problema viene rilevato e si invita l'utente a riselezionare il prodotto desiderato.
     */
    private boolean statoVassoioCambiato(int size) throws RemoteException {
        if (size != ClientCuoco.metodi.getListaProdottiSize()) {
            System.out.println("\n---------------------------------\n\n****Attenzione: Lo stato "
                    + "del vassoio e' cambiato. \nSelezionare "
                    + "nuovamente un prodotto da preparare.**** \n\n ----------------"
                    + "---------------\n");
            auto = true;
            return true;
        }
        return false;
    }

   /*
     * Questa classe contiene un thread che si attiva quando il server non ha vassoi che attendono di essere preparati.
     * Il thread aspetta una risposta dal Server. La risposta ovviamente, arrivera' quando sara' disponibile un vassoio.
     * Una volta risposto, il thread muore.
     * Il thread sarà eventualmente richiamato quando il server si trovera' nuovamente
     * in condizioni di non avere vassoi che attendono preparazione.
     */
    private class check extends Thread {

        public void run() {
            try {
                System.out.println("\nServizio notifica attivo : Verra' visualizzato un avviso "
                        + "quando arrivera' un'ordinazione.\n");

                System.out.println(ClientCuoco.metodi.check());

                once = true;
            } catch (RemoteException e) {
                System.out.println(e +  "RemoteException");
                System.exit(0);
            }
        }
    }
}