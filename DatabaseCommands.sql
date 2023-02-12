CREATE TABLE clients (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL
);

CREATE TABLE products (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL
);

CREATE TABLE sales (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  price DECIMAL(10,2) NOT NULL,
  quantity INT NOT NULL,
  date DATETIME NOT NULL,
  client_id BIGINT NOT NULL,
  FOREIGN KEY (client_id) REFERENCES clients(id)
);

CREATE TABLE sale_products (
  sale_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  PRIMARY KEY (sale_id, product_id),
  FOREIGN KEY (sale_id) REFERENCES sales(id),
  FOREIGN KEY (product_id) REFERENCES products(id)
);
