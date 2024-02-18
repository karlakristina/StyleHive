package stylehive.controller;

import ch.qos.logback.classic.util.StatusViaSLF4JLoggerFactory;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import stylehive.model.*;
import stylehive.repositories.CartRepository;
import stylehive.repositories.CategoryRepository;
import stylehive.repositories.ProductRepository;
import stylehive.repositories.UserRepository;
import stylehive.config.WebResourceConfig;
import javax.naming.Binding;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class ProductController {


    @Autowired
    ProductRepository productRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    CartRepository cartRepository;

    private static String UPLOUDED_FOLDER = "./uploads/" ;


    @GetMapping("/products")
    public String showProducts(Model model, @AuthenticationPrincipal UserDetails userDetails){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails user = (UserDetails) auth.getPrincipal();
        Long userId = userDetails.getUserId();
        model.addAttribute("products", productRepository.findAll());
        model.addAttribute("userId", userId);
        model.addAttribute("user", user);
        model.addAttribute("product", new Product());
        model.addAttribute("added", false);
        model.addAttribute("activeLink", "Products");
        User userr = userDetails.getUser();
        Long userIdd = user.getUserId();
        List<Product> products = productRepository.findAll();
        int numProduct = products.size();

        List<Cart> carts = cartRepository.findByUser(userDetails.getUser());

        int cartNum = carts.size();

        if (cartNum > 0) {
            model.addAttribute("carts", carts);
            model.addAttribute("cartNum", cartNum);
            model.addAttribute("show", true);
        } else {
            model.addAttribute("show", false);
        }
        return "products";
    }

    @GetMapping("/edit")
    public String editProducts(Model model, @AuthenticationPrincipal UserDetails userDetails){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails user = (UserDetails) auth.getPrincipal();
        Long userId = userDetails.getUserId();
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        model.addAttribute("products", productRepository.findAll());
        model.addAttribute("userId", userId);
        model.addAttribute("user", user);
        model.addAttribute("product", new Product());
        model.addAttribute("added", false);
        model.addAttribute("activeLink", "Products");
        User userr = userDetails.getUser();
        Long userIdd = user.getUserId();
        List<Product> products = productRepository.findAll();
        int numProduct = products.size();
        model.addAttribute("numProduct", numProduct);

        List<Cart> carts = cartRepository.findByUser(userDetails.getUser());

        int cartNum = carts.size();

        if (cartNum > 0) {
            model.addAttribute("carts", carts);
            model.addAttribute("cartNum", cartNum);
            model.addAttribute("show", true);
        } else {
            model.addAttribute("show", false);
        }
        return "edit";
    }

    @PostMapping("/product/add")
    public String addProduct (@Valid Product products, BindingResult result, Model model, RedirectAttributes redirectAttributes, UserDetails userDetails, @RequestParam("file") MultipartFile file) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails user = (UserDetails) auth.getPrincipal();
        model.addAttribute("user", user);
        model.addAttribute("activeLink", "Product");
        if (file.isEmpty()) {
            result.addError(new FieldError("product", "image", "Please choose your image."));
        } else if (!file.getContentType().startsWith("image/")) {
            result.addError(new FieldError("product", "image", "Invalid format."));
        } else {
            try {
                byte[] bytes = file.getBytes();
                Path path = Paths.get(UPLOUDED_FOLDER + file.getOriginalFilename());
                Files.write(path, bytes);
                products.setImage(path.toString());
            } catch (IOException e) {
                result.addError(new FieldError("product", "image", "Problem with upload."));
            }
        }
        if (result.hasErrors()) {
            List<Category> categories = categoryRepository.findAll();
            model.addAttribute("categories", categories);
            model.addAttribute("product", products);
            model.addAttribute("products", productRepository.findAll());
            model.addAttribute("added", true);
            return "edit";
        }

        Long userIdd = user.getUserId();
        User selectedUser = userRepository.findById(userIdd).orElse(null);
        products.setUser(selectedUser);
        Long categoryId = products.getCategory().getId();
        Category selectedCategory = categoryRepository.findById(categoryId).orElse(null);
        products.setCategory(selectedCategory);

        productRepository.save(products);
        redirectAttributes.addFlashAttribute("successProduct", "Product added successful!");
        return "redirect:/edit";
    }

    @GetMapping("/product/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {

        try {
            productRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successProduct", "Product successful deleted.");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Delete failed: Foreign key");
        }

        return "redirect:/edit";
    }

    @GetMapping("/product/edit/{id}")
    public String editProductForm(@PathVariable("id") Long id, Model model, @AuthenticationPrincipal  UserDetails userDetails) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails user = (UserDetails) auth.getPrincipal();
        model.addAttribute("user", user);
        Product products = productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException());
        model.addAttribute("products", products);
        model.addAttribute("product", productRepository.findAll());
        model.addAttribute("activeLink", "Categories");
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);

        List<Cart> carts = cartRepository.findByUser(userDetails.getUser());

        int cartNum = carts.size();

        if (cartNum > 0) {
            model.addAttribute("carts", carts);
            model.addAttribute("cartNum", cartNum);
            model.addAttribute("show", true);
        } else {
            model.addAttribute("show", false);
        }

        return "edit_product";
    }

    @PostMapping("edit/product/{id}")
    public String editProduct (@PathVariable("id") Long id, @Valid Product products, BindingResult result, Model model, RedirectAttributes redirectAttributes,@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            result.addError(new FieldError("article", "image", "Please choose your image."));
        } else if (!file.getContentType().startsWith("image/")) {
            result.addError(new FieldError("article", "image", "Invalid format."));
        } else {
            try {
                byte[] bytes = file.getBytes();
                Path path = Paths.get(UPLOUDED_FOLDER + file.getOriginalFilename());
                Files.write(path, bytes);
                products.setImage(path.toString());
            } catch (IOException e) {
                result.addError(new FieldError("article", "image", "Problem with upload."));
            }
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails user = (UserDetails) auth.getPrincipal();
        if (result.hasErrors()) {
            model.addAttribute("products", products);
            model.addAttribute("activeLink", "Product");
            return "edit_product";
        }
        Long userIdd = user.getUserId();
        User selectedUser = userRepository.findById(userIdd).orElse(null);
        products.setUser(selectedUser);
        productRepository.save(products);
        redirectAttributes.addFlashAttribute("successProduct", "Product successful updated! ");
        return "redirect:/edit";
    }
}
