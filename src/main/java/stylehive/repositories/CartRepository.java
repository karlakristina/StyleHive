package stylehive.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import stylehive.model.Cart;
import stylehive.model.Product;
import stylehive.model.User;

import java.util.List;

public interface CartRepository extends JpaRepository <Cart, Long> {
    Cart findByUserAndProduct(User createdBy, Product product);
    List<Cart> findByUser(User createdBy);
}
