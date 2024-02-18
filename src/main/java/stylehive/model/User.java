package stylehive.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

@Entity
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(min = 2, max = 50, message = "Field must contain at least 2 characters and less than 50 characters.")
    @Column(nullable = false, length = 50)
    private String firstname;

    @Size(min = 2, max = 50, message = "Field must contain at least 2 characters and less than 50 characters.")
    @Column(nullable = false, length = 50)
    private String lastname;
    @NotBlank(message="Email address required.")
    @Email(message = "Invalid format email.")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message="Password required.")
    @Column(nullable = false)
    private String password;

    @NotBlank(message="Repeat your password.")
    @Transient
    private String passwordRepeat;

    @Column(nullable = false)
    private String role;

    @OneToMany(mappedBy = "user")
    List<Category> category;

    @OneToMany(mappedBy = "user")
    List<Product> product;

    @OneToMany(mappedBy = "user")
    List<Cart> cart;
  

    public User(Long id, String firstname, String lastname, String email, String password, String role) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public User() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }



    public String getPasswordRepeat() {
        return passwordRepeat;
    }

    public void setPasswordRepeat(String passwordRepeat) {
        this.passwordRepeat = passwordRepeat;
    }

    public List<Category> getCategory() {
        return category;
    }

    public void setCourses(List<Category> category) {
        this.category = category;
    }

    public List<Product> getProduct() {
        return product;
    }

    public void setProduct(List<Product> product) {
        this.product = product;
    }

    public List<Cart> getCart() {
        return cart;
    }

    public void setCart(List<Cart> cart) {
        this.cart = cart;
    }
}

