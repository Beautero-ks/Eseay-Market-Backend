package com.ninehub.dreamshops.repositry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ninehub.dreamshops.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository <Category, Long>{

    Category findByName(String name);

    boolean existsByName(String name);

}
