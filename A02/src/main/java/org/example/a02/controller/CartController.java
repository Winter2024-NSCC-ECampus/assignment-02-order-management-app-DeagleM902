package org.example.a02.controller;

import jakarta.servlet.http.HttpSession;
import org.example.a02.dto.CartItem;
import org.example.a02.model.Product;
import org.example.a02.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/cart")
public class CartController {
    private final ProductService productService;

    public CartController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId,
                            @RequestParam int quantity,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.getProductById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid product ID"));

            Map<Long, CartItem> cart = getCart(session);

            // Existing cart logic
            cart.compute(productId, (key, existingItem) -> {
                if (existingItem != null) {
                    int newQuantity = existingItem.getQuantity() + quantity;
                    if (newQuantity > product.getQuantity()) {
                        throw new IllegalArgumentException("Exceeds available stock");
                    }
                    existingItem.setQuantity(newQuantity);
                    return existingItem;
                }
                return new CartItem(product, quantity);
            });

            redirectAttributes.addFlashAttribute("success",
                    quantity + " × " + product.getName() + " added to cart");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/products";
    }

    @GetMapping
    public String viewCart(Model model, HttpSession session) {
        Map<Long, CartItem> cart = getCart(session);
        model.addAttribute("cartItems", cart.values()); // Pass Collection<CartItem>
        model.addAttribute("cartMap", cart); // Optional: Pass the actual Map if needed
        model.addAttribute("total", calculateTotal(cart));
        return "cart";
    }

    @PostMapping("/remove")
    public String removeItem(@RequestParam Long productId,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        Map<Long, CartItem> cart = getCart(session);
        if (cart.containsKey(productId)) {
            String productName = cart.get(productId).getProduct().getName();
            cart.remove(productId);
            redirectAttributes.addFlashAttribute("success",
                    productName + " removed from cart");
        }
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateQuantity(@RequestParam Long productId,
                                 @RequestParam int quantity,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        try {
            Map<Long, CartItem> cart = getCart(session);

            cart.computeIfPresent(productId, (key, item) -> {
                Product product = productService.getProductById(key)
                        .orElseThrow(() -> new IllegalArgumentException("Product not found"));

                if (quantity > product.getQuantity()) {
                    throw new IllegalArgumentException("Only " + product.getQuantity() + " available");
                }

                if (quantity > 0) {
                    item.setQuantity(quantity);
                    redirectAttributes.addFlashAttribute("success",
                            product.getName() + " quantity updated");
                } else {
                    cart.remove(key);
                    redirectAttributes.addFlashAttribute("success",
                            product.getName() + " removed from cart");
                }
                return item;
            });

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cart";
    }

    private Map<Long, CartItem> getCart(HttpSession session) {
        Map<Long, CartItem> cart = (Map<Long, CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    private BigDecimal calculateTotal(Map<Long, CartItem> cart) {
        return cart.values().stream()
                .map(item -> BigDecimal.valueOf(item.getProduct().getPrice())
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
