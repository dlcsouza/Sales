@Entity
@Table(name = "sales")
public class Sale {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "price")
  private BigDecimal price;

  @Column(name = "quantity")
  private Integer quantity;

  @Column(name = "date")
  private LocalDateTime date;

  @ManyToOne
  @JoinColumn(name = "client_id", referencedColumnName = "id")
  private Client client;

  @ManyToMany
  @JoinTable(name = "sale_products",
      joinColumns = @JoinColumn(name = "sale_id"),
      inverseJoinColumns = @JoinColumn(name = "product_id"))
  private Set<Product> products;

  public Sale() {}

  public Sale(BigDecimal price, Integer quantity, LocalDateTime date, Client client, Set<Product> products) {
    this.price = price;
    this.quantity = quantity;
    this.date = date;
    this.client = client;
    this.products = products;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public BigDecimal getPrice() {
    return price;
  }
