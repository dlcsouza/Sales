public interface SaleRepository extends JpaRepository<Sale, Long> {
  List<Sale> findByClientId(Long clientId);
}
