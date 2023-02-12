public interface ClientRepository extends JpaRepository<Client, Long> {
  Optional<Client> findByName(String name);
}
