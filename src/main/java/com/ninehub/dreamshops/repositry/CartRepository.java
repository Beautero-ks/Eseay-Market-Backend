package com.ninehub.dreamshops.repositry;

import com.ninehub.dreamshops.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository <Cart, Long> {
    void deleteAllByCartId(Long id);

    Cart findByUserId(Long userId);
}
