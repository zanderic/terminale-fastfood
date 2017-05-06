
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Classe Ordinazione, si occupa di servire il cameriere nel prendere un'ordinazione. Dopo l'inserimento del numero di tavolo
 * che si sta per servire, vine richiesto di aggiugere di volta in volta i prodotti da mandare in cucina.
 * Una volta esaurite le richieste del cliente, viene inviata in cucina l'ordinazione completa di prodotti piu' id del tavolo.
 * @author Flavio Colonna, Riccardo Zandegiacomo
 */
public class Ordinazione implements Runnable {
    
    private LinkedList<Prodotto> prodottiOrdinati = new LinkedList();
    private int max = Cameriere.maxTavoli;
    private int tavServito;

    @Override
    public void run() {
        Scanner tast = Cameriere.tastiera;
        // Richiesta del numero del tavolo che si sta per servire
        System.out.println("\nIndicare con un indice il numero del tavolo da servire:");
        boolean valid = false;
        while (!valid) {
            try {
                int a = Integer.parseInt(tast.nextLine());
                if (a >= 1 && a <= max) {
                    // Controllo che il tavolo che si sta servendo non sia gia' stato servito
                    if (Cameriere.metodi.controlloPosti(a)) {
                        valid = true;
                        tavServito = a;
                        System.out.println("\nStai servendo il tavolo numero " + a + ".");
                    } else {
                        System.out.println("Il tavolo scelto e' gia' stato servito, scegliere un altro tavolo.");
                    }
                } else {
                    System.out.println("Errore, numero tavolo sbagliato. Inserire un indice compreso tra 1 e " + max + "!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Errore, inseriere indice valido. L'indice deve essere compreso tra 1 e " + max + "!");
            } catch (RemoteException ex) {
                System.out.println(ex);
                // Chiusura Client
                System.exit(0);
            }
        }
        // Visualizzazione del menu' e inizio ordinazione
        System.out.println("\nMENU'\n" + Cameriere.menu);
        System.out.println("Indicare con un indice cosa si desidera ordinare.");
        System.out.println("Se si vuole chiudere l'ordinazione, premere 0.");
        while (tast.hasNextLine()) {
            try {
                int indi = Integer.parseInt(tast.nextLine());
                if (indi == 0) {
                    System.out.println("\nOrdinazione conclusa, in attesa di elaborazione...\n");
                    if (prodottiOrdinati.size() == 0) {
                        // Lista ordinazioni vuota, il cliente non ha ordinato nulla
                        System.out.println("Nessun prodotto ordinato, e' necessario ordinare almeno un prodotto!");
                    } else {
                        Cameriere.metodi.ordinazione(prodottiOrdinati, tavServito);
                        System.out.println("Ordinazione effettuata con successo!");
                        
                        // Riassunto dell'ordinazione
                        System.out.println("Questa ordinazione comprende i seguenti piatti e bevande:");
                        for (int i = 0; i < prodottiOrdinati.size(); i++) {
                            System.out.println("* " + prodottiOrdinati.get(i).getTipo());
                        }
                        // Visualizzazione menu' principale
                        Thread disp = new Thread(new Display());
                        disp.start();
                        break;
                    }
                } else {
                    // Controllo che l'indice digitato stia nel range di indici disponibili
                    if (indi < 13 && indi > 0) {
                        // Richiesta una bevanda
                        if (indi > 6) {
                            System.out.println("Digitare 1 per bevanda con ghiaccio, 2 per bevanda senza ghiaccio.");
                            while (tast.hasNextLine()) {
                                try {
                                    int ice = Integer.parseInt(tast.nextLine());
                                    if (ice == 1 || ice == 2) {
                                        switch (ice) {
                                            case 1: {
                                                String tipo = Cameriere.prodotti[indi - 1].getTipo() + " con ghiaccio";
                                                prodottiOrdinati.add(new Prodotto(indi, tipo)); // Prodotto aggiunto in coda
                                                System.out.println("* " + tipo + " aggiunta");
                                                break;
                                            }
                                            case 2: {
                                                String tipo = Cameriere.prodotti[indi - 1].getTipo() + " senza ghiaccio";
                                                prodottiOrdinati.add(new Prodotto(indi, tipo)); // Prodotto aggiunto in coda
                                                System.out.println("* " + tipo + " aggiunta");
                                                break;
                                            }
                                        }
                                        break;
                                    } else {
                                        System.out.println("Errore, inserire 1 per l'opzione con ghiaccio o 2 per l'opzione senza ghiaccio!");
                                    }
                                } catch (NumberFormatException e) {
                                    System.out.println("Errore, inserire un indice valido!");
                                }
                            }
                        } else {
                            // Richiesta una pietanza 
                            String tipo = Cameriere.prodotti[indi - 1].getTipo();
                            if (indi == 3 || indi == 5) {
                                System.out.println("* " + tipo + " aggiunte");
                            }
                            else if (indi == 6) {
                                System.out.println("* " + tipo + " aggiunta");
                            }
                            else {
                                System.out.println("* " + tipo + " aggiunto");
                            }
                            prodottiOrdinati.add(new Prodotto(indi, tipo)); // Prodotto aggiunto in coda
                        }
                    } else {
                        System.out.println("Inserire un indice compreso tra 1 e 12!");
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("Errore nella digitazione, inserire un indice valido!");
            } catch (RemoteException ex) {
                System.out.println(ex);
                // Chiusura Client
                System.exit(0);
            }
        }
    }
}
