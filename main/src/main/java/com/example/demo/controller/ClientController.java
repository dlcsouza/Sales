@RestController
@RequestMapping("/clients")
public class ClientController {
  
  @Autowired
  private final ClientService clientService;

  @GetMapping("/clients")
  @GetMapping
  public List<Client> findAllClients() {
    return clientService.findAllClients();
  }

  @GetMapping("/clients/{id}")
  public ResponseEntity<Client> findClientById(@PathVariable Long id) {
    Optional<Client> client = clientService.findClientById(id);
    if (!client.isPresent()) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(client.get());
  }

  @PostMapping("/clients")
  public ResponseEntity<Client> createClient(@RequestBody Client client) {
    Client createdClient = clientService.createClient(client);
    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest().path("/{id}")
        .buildAndExpand(createdClient.getId()).toUri();
    return ResponseEntity.created(location).body(createdClient);
  }

  @PutMapping("/clients/{id}")
  public ResponseEntity<Client> updateClient(@PathVariable Long id, @RequestBody Client client) {
    if (!clientService.findClientById(id).isPresent()) {
      return ResponseEntity.notFound().build();
    }
    client.setId(id);
    Client updatedClient = clientService.updateClient(client);
    return ResponseEntity.ok(updatedClient);
  }

  @DeleteMapping("/clients/{id}")
  public ResponseEntity<?> deleteClient(@PathVariable Long id) {
    if (!clientService.findClientById(id).isPresent()) {
      return ResponseEntity.notFound().build();
    }
    clientService.deleteClient(id);
    return ResponseEntity.noContent().build();
  }
}
