@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private final ProductService productService;

    @GetMapping("/products")
    public List<Product> findAllProducts() {
        return productService.findAll();
    }

    @GetMapping("/products/{id}")
    public Product findProductById(@PathVariable(value = "id") Long productId) {
        return productService.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
    }

    @PostMapping("/products")
    public Product createProduct(@Valid @RequestBody Product product) {
        return productService.save(product);
    }

    @PutMapping("/products/{id}")
    public Product updateProduct(@PathVariable(value = "id") Long productId, @Valid @RequestBody Product productDetails) {
        Product product = productService.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        product.setName(productDetails.getName());
        product.setPrice(productDetails.getPrice());
        return productService.save(product);
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable(value = "id") Long productId) {
        Product product = productService.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + productId));
        productService.delete(product);
        return ResponseEntity.ok().build();
    }
}