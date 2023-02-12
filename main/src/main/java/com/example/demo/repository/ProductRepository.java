public interface ProductRepository extends JpaRepository<Product, Long> {
  Optional<Product> findByName(String name);
}
