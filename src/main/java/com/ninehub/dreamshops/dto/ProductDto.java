package com.ninehub.dreamshops.dto;

import java.math.BigDecimal;
import java.util.List;

import com.ninehub.dreamshops.model.Category;

import lombok.Data;

@Data
public class ProductDto {
    private Long id;
    private String name;
    private String brand;
    private BigDecimal price;
    private int inventory;
    private String description;

    //The category is not depend on the product and so other. When the product is deleted, the category remane.
    private Category category;

    // delete all the images associate to this product when the product is deleted
    private List<ImageDto> images;

}
