package com.ninehub.dreamshops.request;

import java.math.BigDecimal;

import com.ninehub.dreamshops.model.Category;

import lombok.Data;

@Data
public class AddProductRequest {
    private Long id;
    private String name;
    private String brand;
    private BigDecimal price;
    private int inventory;
    private String description;
    private Category category;
}
