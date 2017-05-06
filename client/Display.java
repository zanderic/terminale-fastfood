
import java.util.Scanner;

/**
 * Classe Display, si occupa della visualizzazione del display sul terminale che viene usato dai camerieri.
 * Piu' precisamente permette al cameriere di decidere cosa fare: prendere una nuova ordinazione o consegnare
 * un'ordinazione pronta al tavolo.
 * @author Flavio Colonna, Riccardo Zandegiacomo
 */
public class Display implements Runnable {

    public void run() {
        Scanner tastiera = Cameriere.tastiera;
        System.out.println("\n*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*\n");
        System.out.println("Cosa desidera fare?");
        System.out.println("0: Chiudere sessione");
        System.out.println("1: Prendere ordinazione");
        System.out.println("2: Consegna ordinazioni al tavolo");
        System.out.println("\n*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*\n");
        int indiceInserito = 0;
        while (tastiera.hasNextLine()) {
            try {
                int codice = Integer.parseInt(tastiera.nextLine());
                if (codice <= 2 && codice >= 0) {
                    indiceInserito = codice;
                    break;
                } else {
                    System.out.println("\nCodice errato, digitare nuovamente!\n0: Chiudere connessione\n1: Prendere ordinazione\n2: Consegna ordinazione al tavolo\n");
                }
            } catch(NumberFormatException e) {
                System.out.println("\nCodice errato, digitare nuovamente!\n0: Chiudere connessione\n1: Prendere ordinazione\n2: Consegna ordinazione al tavolo\n");
            }
        }
        // Qualora venga digitato il numero 0, la sessione verrà chiusa
        if (indiceInserito == 0) {
            System.out.println("La connessione verrà chiusa! Arrivederci!");
            System.exit(0);
        }
        // Qualora venga digitato il numero 1, verra' mostrato a video il menu' e verra' chiesto di indicare con un indice il prodotto ordinato dal cliente
        if (indiceInserito == 1) {
            Thread ordina = new Thread(new Ordinazione());
            ordina.start();
        }
        // Qualora venga digitato il numero 2, il cameriere prendera' il primo vassoio pronto in cucina e lo consegnera' al tavolo
        if (indiceInserito == 2) {
            Thread consegna = new Thread(new Consegna());
            consegna.start();
        }
    }
}
