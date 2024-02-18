package stylehive.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import stylehive.model.Product;

public interface ProductRepository extends JpaRepository <Product, Long> {
}
