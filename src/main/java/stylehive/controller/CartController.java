package stylehive.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import stylehive.model.*;
import stylehive.repositories.CartRepository;
import stylehive.repositories.CategoryRepository;
import stylehive.repositories.ProductRepository;
import stylehive.repositories.UserRepository;

import java.util.List;

@Controller
public class CartController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CategoryRepository categoryRepository;


    @GetMapping("/cart")
    public String showCart(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails user = (UserDetails) auth.getPrincipal();
        Long userId = userDetails.getUserId();
        List<Product> products = productRepository.findAll();

        model.addAttribute("carts", cartRepository.findAll());
        model.addAttribute("products", products);
        model.addAttribute("userId", userId);
        model.addAttribute("user", user);
        model.addAttribute("cart", new Cart());
        model.addAttribute("added", false);
        model.addAttribute("activeLink", "Cart");
        User userr = userDetails.getUser();
        Long userIdd = user.getUserId();

        List<Cart> carts = cartRepository.findByUser(userDetails.getUser());

        int cartNum = carts.size();

        if (cartNum > 0) {
            model.addAttribute("carts", carts);
            model.addAttribute("cartNum", cartNum);
            model.addAttribute("show", true);
        } else {
            model.addAttribute("show", false);
        }

        return "cart";
    }

    @GetMapping("/cart/{id}")
    public String addCart(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes, UserDetails userDetails) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails user = (UserDetails) auth.getPrincipal();



        Product selectedProduct = productRepository.findById(id).orElse(null);

        if (selectedProduct == null) {
            return "redirect:/error";
        }
        Long userIdd = user.getUserId();
        User selectedUser = userRepository.findById(userIdd).orElse(null);

        Cart existingProduct = cartRepository.findByUserAndProduct(selectedUser, selectedProduct);

        if (existingProduct != null) {
            redirectAttributes.addFlashAttribute("error", "Product is added yet");
            return "redirect:/products";
        }

        Cart newCart = new Cart();
        newCart.setUser(selectedUser);
        newCart.setProduct(selectedProduct);
        newCart.setUser(selectedUser);

        cartRepository.save(newCart);

        redirectAttributes.addFlashAttribute("successCart", "Product added successful in cart");
        return "redirect:/products";
    }

    @GetMapping("/cart/delete/{id}")
    public String deleteWatch(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {

        Cart cart = cartRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Wrong ID"));
        cartRepository.delete(cart);
        redirectAttributes.addFlashAttribute("successDelete", "Delete successful!");


        return "redirect:/cart";
    }


}
