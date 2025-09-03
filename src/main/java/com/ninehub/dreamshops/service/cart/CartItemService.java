package com.ninehub.dreamshops.service.cart;

import com.ninehub.dreamshops.execptions.ResourceNotFoundException;
import com.ninehub.dreamshops.model.Cart;
import com.ninehub.dreamshops.model.CartItem;
import com.ninehub.dreamshops.model.Product;
import com.ninehub.dreamshops.repositry.CartItemRepository;
import com.ninehub.dreamshops.repositry.CartRepository;
import com.ninehub.dreamshops.service.product.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CartItemService implements ICartItemService {

    @Autowired
    private final CartItemRepository cartItemRepository;
    private final IProductService productService;
    private final ICartService cartService;
    private final CartRepository cartRepository;

    @Override
    public void addItemToCart(Long cartId, Long productId, int quantity) {
        // Check if the product exists
//        if (productService.getProductById(productId) == null) {
//            throw new ResourceNotFoundException("Product not found");
//        }

        //1. Get the cart
        //2. Get the product
        //2. Check if the product already exists in the cart
        //3. If yes, then increase the quantity of the product with the requested quantity
        //4. If no, then initiate a new CartItem entry.

        Cart cart = cartService.getCartById(cartId);
        Product product = productService.getProductById(productId);
        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst().orElse(new CartItem());

        if(cartItem.getId() == null){
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setUnitPrice(product.getPrice());
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        }
        cartItem.setTotalPrice();
        cart.addItem(cartItem);
        cartItemRepository.save(cartItem);
        cartRepository.save(cart);
    }

    @Override
    public void removeItemFromCart(Long cartId, Long productId) {
        Cart cart = cartService.getCartById(cartId);
        CartItem itemToRemove = getCartItem(cartId, productId);
        cart.removeItem(itemToRemove);
        cartRepository.save(cart);
    }

    @Override
    public void updateItemQuantity(Long cartId, Long productId, int quantity) {
        Cart cart = cartService.getCartById(cartId);
        cart.getItems().stream().filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .ifPresentOrElse(item -> {
                    item.setQuantity(quantity);
                    item.setUnitPrice(item.getProduct().getPrice());
                    item.setTotalPrice();
                    BigDecimal totalAmount = cart.getItems().stream()
                            .map(CartItem::getTotalPrice)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    cart.setTotalAmount(totalAmount);
                    cartRepository.save(cart);
                }, () -> {
                    throw new ResourceNotFoundException("product not found");
                });
    }

    @Override
    public CartItem getCartItem(Long cartId, Long productId) {
        Cart cart = cartService.getCartById(cartId);
        return cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
    }
}
