package stylehive.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import stylehive.model.*;
import stylehive.repositories.CartRepository;
import stylehive.repositories.CategoryRepository;
import stylehive.repositories.UserRepository;

import java.util.List;

@Controller
public class CategoryController {

    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CartRepository cartRepository;

    @GetMapping("/categories")
    public String showCategories(Model model, @AuthenticationPrincipal UserDetails userDetails){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails user = (UserDetails) auth.getPrincipal();
        Long userId = userDetails.getUserId();
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("userId", userId);
        model.addAttribute("user", user);
        model.addAttribute("category", new Category());
        model.addAttribute("added", false);
        model.addAttribute("activeLink", "Category");
        User userr = userDetails.getUser();
        Long userIdd = user.getUserId();

        List<Category> categories = categoryRepository.findAll();
        int numberOfCategories = categories.size();
        model.addAttribute("numberOfCategories", numberOfCategories);

        List<Cart> carts = cartRepository.findByUser(userDetails.getUser());

        int cartNum = carts.size();

        if (cartNum > 0) {
            model.addAttribute("carts", carts);
            model.addAttribute("cartNum", cartNum);
            model.addAttribute("show", true);
        } else {
            model.addAttribute("show", false);
        }

        return "categories";
    }

    @PostMapping("/category/add")
    public String addCategory (@Valid Category category, BindingResult result, Model model, RedirectAttributes redirectAttributes, UserDetails userDetails) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails user = (UserDetails) auth.getPrincipal();
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("added", false);
            model.addAttribute("user", user);
            return "categories";
        }
        Long userIdd = user.getUserId();
        User selectedUser = userRepository.findById(userIdd).orElse(null);
        category.setUser(selectedUser);
        categoryRepository.save(category);
        redirectAttributes.addFlashAttribute("success", "Category successfully added!");
        return "redirect:/categories";
    }

    @GetMapping("/category/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {



        try {
            categoryRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Category successful deleted!");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Delete failed: Foreign key");
        }
        return "redirect:/categories";
    }

}
