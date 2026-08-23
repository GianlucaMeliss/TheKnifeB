package theknife.client;

import theknife.remote.TheKnifeService;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Gestore della connessione RMI lato Client.
 * Implementa il pattern Singleton per fornire un accesso centralizzato al servizio remoto.
 */
public class RmiClientManager {
    private static RmiClientManager instance;
    private TheKnifeService service;
    
    // Configurazione predefinita (potrebbe essere caricata da un file di config in futuro)
    private static final String SERVICE_NAME = "TheKnifeService";
    private String host = "localhost";
    private int port = 1099;

    private RmiClientManager() {}

    /**
     * Restituisce l'istanza unica del manager.
     * @return l'istanza di RmiClientManager
     */
    public static synchronized RmiClientManager getInstance() {
        if (instance == null) {
            instance = new RmiClientManager();
        }
        return instance;
    }

    /**
     * Inizializza la connessione al registro RMI e recupera lo stub del servizio.
     * @return true se la connessione è riuscita, false altrimenti
     */
    public boolean connect() {
        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            service = (TheKnifeService) registry.lookup(SERVICE_NAME);
            System.out.println("Client RMI: Connesso con successo a " + host + ":" + port);
            return true;
        } catch (Exception e) {
            System.err.println("Client RMI: Errore di connessione a " + host + ":" + port + " - " + e.getMessage());
            // e.printStackTrace();
            return false;
        }
    }

    /**
     * Restituisce il riferimento al servizio remoto.
     * @return lo stub del servizio TheKnifeService, o null se non connesso
     */
    public TheKnifeService getService() {
        return service;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
