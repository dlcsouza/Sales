@Service
public class ClientService {

  private final ClientRepository clientRepository;

  public ClientService(ClientRepository clientRepository) {
    this.clientRepository = clientRepository;
  }

  public List<Client> findAllClients() {
    return clientRepository.findAll();
  }

  public Optional<Client> findClientById(Long id) {
    return clientRepository.findById(id);
  }

  public Client createClient(Client client) {
    return clientRepository.save(client);
  }

  public Client updateClient(Client client) {
    return clientRepository.save(client);
  }

  public void deleteClient(Long id) {
    clientRepository.deleteById(id);
  }
}
