package com.ninehub.dreamshops.service.cart;

import com.ninehub.dreamshops.model.Cart;
import com.ninehub.dreamshops.model.User;

import java.math.BigDecimal;

public interface ICartService {
    Cart getCartById(Long id);
    void clearCart(Long id);
    BigDecimal getTotalPrice(Long id);


    Cart initializeNewCart(User user);

    Cart getCartByUserId(Long userId);
}
