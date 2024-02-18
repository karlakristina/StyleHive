package stylehive.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import stylehive.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
