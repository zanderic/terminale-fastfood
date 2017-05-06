
import java.util.LinkedList;

/**
 * Classe che gestisce la preparazione dei prodotti in cucina.
 * @author Antonio Faienza, Mirco Lacalandra
 */
public class Cucina {

    // Variabile che viene incrementata ogni volta che viene completato un vassoio
    private Integer idActualVassoio = 1;

    //lista per il magazzino
    private LinkedList<Prodotto> magazzino = new LinkedList();
    
    //Coda contenente i vassoi (tavoli) che i cuochi dovranno cucinare
    protected Coda<Tavolo> ordinazioniInCucina; //coda delle comande da cucinare 

    //LinkedList di appoggio che contiene il vassoio con i prodotti preparati dai cuochi. ---> CIOE' FINO A QUESTO MOMENTO CHE HANNO PREPARATO I CUOCHI?
    private LinkedList<Prodotto> vassoioAttuale = new LinkedList();
    
    /**
     * Metodo che ritorna un messaggio per il client Cuoco.
     * @return 
     */
    protected String connessioneCuoco() {
        return "Premi 1 per visualizzare la lista dei prodotti da preparare o Q per uscire, infine INVIO per confermare";
    }

    /**
     * Metodo che ritorna l'id del vassoio attuale
     * @return 
     */
    protected int getIdActualVassoio() {
        return idActualVassoio;
    }


    /**
     * Questo metodo ritorna al client una stringa contenente l'elenco
     * dei prodotti da preparare (o in preparazione) nel vassoio attuale.
     * @return 
     */
    protected String ordinazione() {

        /*Innanzitutto viene controllato che nella coda delle ordinazioni
        ci sia almeno un elemento*/
        synchronized (ordinazioniInCucina) {
            if (ordinazioniInCucina.isEmpty())
                return "empty";
        }
       
        /* Se la coda delle ordinazioni non è vuota, viene 'costruita' la stringa
         contenente i prodotti da preparare.
         */
        synchronized (ordinazioniInCucina.readFirst()) {
            String s = "";

            LinkedList<Prodotto> a = ordinazioniInCucina.readFirst().getListaProdotti();

            for (int i = 0; i < a.size(); i++) {
                
                // il getPreparing mi dice se questio prodotto lo sta preparando qualche altro cuoco --> E' un Boolean
                if (a.get(i).getPreparing()) { //i prodotti in preparazione verranno trattati diversamente
                    s += i + ": " + a.get(i).getTipo() + " - " + " IN PREPARAZIONE DA UN ALTRO CUOCO ";
                } else {
                    s += i + ": " + a.get(i).getTipo();
                }

                s += "\n";

            }
            return s += "\nDigita il numero del prodotto che vuoi preparare, quindi premi invio";
        }
    }

    /**
     * Questo metodo ritorna la grandezza della lista con i prodotti da
     * preparare. (ovvero quanti prodotti ci sono ancora da preparare nel
     * vassoio attuale).
     */
    protected int getListaProdottiSize() {
        synchronized(ordinazioniInCucina.readFirst()) {
        return ordinazioniInCucina.readFirst().getListaProdotti().size();
        }
    }

    /**
     * Metodo che ritorna il nome del prodotto selezionato
     * @param numero posizione del prodotto nella lista
     */
    protected String getProdotto(int numero) {
        synchronized(ordinazioniInCucina.readFirst()) {
        return ordinazioniInCucina.readFirst().getListaProdotti().get(numero).getTipo();
        }
    }

    /**
     * Metodo per iniziare la preparazione di un prodotto. Viene verificato
     * prima che il prodotto sia in magazzino. Nel caso, viene rimosso dal
     * magazzino e immediatamente segnato come preparato. Il metodo ritorna un
     * array contenente due informazioni : 1 - il prodotto che il cuoco dovrà
     * cucinare 2 - una stringa che informa riguardo la presenza di quel
     * prodotto in magazzino o meno.
     *
     * @param numero Indice del prodotto nella lista.
     */
    protected Object[] iniziaPreparazione(int numero) {
        synchronized (ordinazioniInCucina.readFirst()) {

            Object[] infos = new Object[2];

            Prodotto prodotto = ordinazioniInCucina.readFirst().getListaProdotti().get(numero);
            /*
            Per consentire uno scambio di oggetto 'prodotto' tra server e client, viene creato
            un oggetto Prodotto chiamato 'ingredienti', identico a prodotto.
            Questo viene inviato al client, che lo restituirà 'cucinato'.
            Come spiegato nella relazione tutto questo è una simulazione al fine di permettere
            lo scambio di oggetti serializzati. 
            */
            Prodotto ingredienti = prodotto;

            //Viene inserito il primo prodotto nell'array
            infos[0] = (ingredienti);

            int i;
            // verifico che il prodotto sia in amgazzino
            if ((i = contains(prodotto, magazzino, true)) != -1) { // se è contenuto in magazzino
                //Rimuovo il prodotto dal magazzino
                magazzino.remove(i);
                //Rimuovo il prodotto dalla coda delle ordinazioni
                ordinazioniInCucina.readFirst().getListaProdotti().remove(numero);
                //aggiungo al vassoio attuale
                vassoioAttuale.add(prodotto);

                //Controllo nel caso questo prodotto fosse l'ultimo del vassoio
                checkVassoioCompletato();

                infos[1] = ("MAG"); //MAG sta per 'prodotto in magazzino'
                return infos;
            }

            //imposto lo stato 'in preparazione' per il prodotto selezionato dal cuoco
            ordinazioniInCucina.readFirst().getListaProdotti().get(numero).isPreparing();

            infos[1] = ("PREP"); //PREP sta per 'in preparazione'

            return infos;
        }
    }

    /**
     * Metodo per completare la preparazione di un prodotto. Vengono effettuati
     * diversi controlli, ed infine viene ritornata una stringa con un messaggio
     * in base all'esito dell'operazione.
     *
     * @param p è il prodotto preparato dal cuoco
     * @param id è l'id del vassoio al quale stava lavorando il cuoco
     * @param size è la dimensione del vassoio al quale stava lavorando il cuoco
     * @param posizione è la posizione (nella linkedList dei prodotti) del
     * prodotto a cui stava lavorando il cuoco
     */
    protected String concludiPreparazione(Prodotto p, int id, int size, int posizione) {

        Prodotto prodotto =  p; //è il prodotto cucinato dal cuoco

        synchronized (ordinazioniInCucina) {
            /*controlla che nel frattempo la ordinazioniInCucina non si sia svuotata */
            if (ordinazioniInCucina.isEmpty()) {
                magazzino.push(prodotto);
                return "Tutte le richieste sono state già evase. Il tuo prodotto verrà riposto in magazzino";
            }
        }

        /* 
         A seconda dell'esito dell'operazione, potranno verificarsi quattro casi diversi.
        
         Caso 1 (base) = l'id del vassoio da preparare non è cambiato (nessun vassoio è stato ultimato
         mentre il cuoco cucinava il prodotto), ed inoltre la dimensione della lista dei prodotti da preparare
         è rimasta invariata.
        
         Il prodotto viene quindi rimosso dalla lista dei prodotti da preparare ed
         inserito nella lista vassoioAttuale (che, quando sarà ultimato, verrà portato in sala dai camerieri).
      
         Nel caso l'id del vassoio da preparare sia rimasto lo stesso, ma la dimensione di quel vassoio è cambiata
         (ovvero altri cuochi hanno preparato altri prodotti da quel vassoio), vengono effettuate ulteriori verifiche.
         Potranno quindi presentarsi i seguenti casi:
        
         Caso 2 :  tra i prodotti da preparare è presente almeno un prodotto uguale a quello preparato dal cuoco,
         ed è già in lavorazione da un altro cuoco 'Y'.
         Il prodotto già pronto del nostro cuoco 'X' verrà utilizzato al posto di quello di 'Y'. Quando 'Y' terminerà
         la preparazione, il suo prodotto potrà essere messo in magazzino (nel caso non siano richiesti altri
         prodotti di quel tipo) o utilizzato lo stesso.
        
         Caso 3: tra i prodotti da preparare è presente almeno un prodotto uguale a quello preparato dal cuoco, e nessun altro lo sta preparando.
         Il prodotto viene quindi inserito nella lista vassoioAttuale, e quel prodotto viene dichiarato come già cucinato.
        
         Caso 4 : La dimensione del vassoio è cambiata e non è più richiesto il prodotto presente dal cuoco.
         Il prodotto preparato dal cuoco viene riposto in magazzino.
        
         è inoltre presente un ultimo caso
        
         Caso 5: Mentre il cuoco preparava quel prodotto, gli altri cuochi hanno completato il vassoio in corso. Il valore
         id vassoio è stato incrementato e non corrisponde più a quello a cui stava lavorando il cuoco. 
         Il prodotto prepratao dal cuoco verrà riposto in magazzino ,e questo potrà scegliere di preparare altri prodotti
         dal nuovo vassoio (se nel nuovo vassoio sarà richiesto lo stesso prodotto, il cuoco potrà scegliere se evaderlo
         utilizzando quello in magazzino o preparare un altro.
         N.B. la procedura di ricerca dello stesso prodotto nel nuovo vassoio NON è automatica. Deve essere il cuoco a deciderlo.
         */
        synchronized (ordinazioniInCucina.readFirst()) {
            int i;

            if (id == idActualVassoio) 
            { //viene confrontato l'id vassoio del cuoco con quello attuale

                if (size == ordinazioniInCucina.readFirst().getListaProdotti().size()) { //viene confrontata la dimensione del vassoio attuale con quella del cuoco
                    vassoioAttuale.push(prodotto); //inserisco il prodotto nel vassoio dei prodotti prearati.

                    ordinazioniInCucina.readFirst().getListaProdotti().remove(posizione); //rimuovo il prodotto da quelli da preparare

                    checkVassoioCompletato(); //controllo nel caso quel prodotto fosse l'ultimo del vassoio.

                    return "Preparazione completata (codice 1) "; //Caso 1
                } //Se la dimensione è cambiata, controllo che ci sia comunque un prodotto uguale in preparazione, e lo evado.
                else if ((i = containsPreparing(prodotto, ordinazioniInCucina.readFirst().getListaProdotti())) != -1) {
                    vassoioAttuale.push(prodotto); //aggiungo il prodotto al vassoio in preparazione

                    ordinazioniInCucina.readFirst().getListaProdotti().remove(i); 

                    checkVassoioCompletato(); //..

                    return "Preparazione Completata (codice 2)."; //Caso 2
                } //Se la dimensione è cambiata, controllo che ci sia comunque un prodotto uguale NON in preparazione , e lo evado.
                else if ((i = contains(prodotto, ordinazioniInCucina.readFirst().getListaProdotti(), false)) != -1) {
                    vassoioAttuale.push(prodotto); //aggiungo il prodotto al vassoio in preparazione

                    ordinazioniInCucina.readFirst().getListaProdotti().remove(i); 

                    checkVassoioCompletato();

                    return "Preparazione Completata (codice 3)."; //Caso 3

                } else {
                    magazzino.push(prodotto);

                    return "Il tuo prodotto è stato riposto in magazzino "
                            + "poiche' un altro cameriere ha completato la preparazione prima di te. (Codice 4)"; //Caso 4
                }
            } else { //Se nessuna delle condizioni è stata esaudita, il prodotto finisce comunque in magazzino.
                magazzino.push(prodotto);

                return "Il tuo prodotto e' stato riposto in magazzino a causa di un cambio di vassoio (Codice 5)"; //Caso 5
            }
        }
    }

    /**
     * Metodo che, dato un prodotto ed una lista id prodotti, controlla che quel
     * prodotto sia presente nella lista e sia già in preparazione.
     *  Il metodo ritorna la posizione del prodotto, oppure -1 nel caso il prodotto 
     * non sia presente.
     * @param p1 prodotto target
     * @param p lista di prodotti
     */
    private int containsPreparing(Prodotto p1, LinkedList<Prodotto> p) {
        for (int i = 0; i < p.size(); i++) {
            if (p.get(i).getIndice() == p1.getIndice()) {
                if (p.get(i).getPreparing()) {
                    return i;
                }
            }
        }
        return -1;
    }

     /**
     * Metodo che, dato un prodotto ed una lista id prodotti, controlla che quel
     * prodotto sia presente nella lista e che non sia già in preparazione.
     * Il metodo ritorna la posizione del prodotto, oppure -1 nel caso il prodotto 
     * non sia presente.
     * Questo metodo è inoltre utilizzato per la ricerca di un prodotto nel magazzino.
     * Poichè questa ricerca è indipendente dallo stato 'IN PREPARAZIONE, un booleano indicherà
     * la modalità di esecuzione.
     * @param p1 prodotto target
     * @param p lista di prodotti
	* @param magazzino nel caso si stia cercando un prodotto in magazzino verrà ritornata la posizione
	 del prodotto indipendentemente dallo stato (in preparazione o no).
     */
    private int contains(Prodotto p1, LinkedList<Prodotto> p, boolean magazzino) {


        for (int i = 0; i < p.size(); i++) {
            System.out.println("controllo " + p.get(i).getTipo() + " con " + p1.getTipo());
            if (p.get(i).getIndice() == p1.getIndice()) {
                
                if (magazzino) {
                    return i; //se sto cercando nel magazzino ritorno immetiatamente l'indice di posizione
                }

                if (!p.get(i).getPreparing()) {
                    return i;
                }
            }
        }
        return -1;
    }


    /**
     * Metodo che controlla se il primo vassoio della lista è stato completato.
     * In caso positivo, lo rimuove, prepara il nuovo vassoio e lo inserisce
     * nella coda ordinazioniCucinate. Inoltre incrementa l'id del vassoio
     * attuale
     */
    private void checkVassoioCompletato() {
        //controllo che la lista sia vuota
        if (ordinazioniInCucina.readFirst().getListaProdotti().size() == 0) {

            int i = ordinazioniInCucina.readFirst().getId(); //salvo l'id del vassoio/tavolo

            ordinazioniInCucina.removeFirst(); //rimuovo quel vassoio completato

            idActualVassoio++; //incremento id vassoio. Questo sta a significare che
            //un vassoio è stato completato.

            /*Creo infine un oggetto di tipo Tavolo contenente il vassoio con tutti i
             prodotti completati. l'oggetto Tavolo sarà quindi inserito nella lista delle
             ordinazioni pronte. I camerieri provvederanno ad evaderle, 'riportandole in sala'
             (ovvero svuotando la lista)
             */
            Tavolo tav = new Tavolo(vassoioAttuale, i);
            Metodi.ordinazioniCucinate.push(tav);

        }
    }
        
    /**
     * Metodo che, quando la lista delle ordinazioni è vuota, attende che si
     * riempia e ritorna un messaggio di notifica al client.
     * @return
     */
    protected String check() {
        synchronized (ordinazioniInCucina) {
            while (ordinazioniInCucina.isEmpty()) 
            {
                try {
                    ordinazioniInCucina.wait();
                } catch (InterruptedException e) {
                    System.out.println(e+ "InterruptedException");
                }
            }
            return "\n******************\nè arrivata una"
                    + " nuova ordinazione!\n******************";
        }
    }

    /**
     * Metodo che inserisce il prodotto in magazzino
     * (chiamato in caso il vassoio si sia svuotato mentre un cuoco preparava un prodotto).
     * @param prodotto
     * @return 
     */
    String toMagazzino(Prodotto prodotto) {
        magazzino.push(prodotto);
        return "Durante la preparazione, il vassoio e' stato completato. Il tuo prodotto e' stato riposto in magazzino";
    }
}
