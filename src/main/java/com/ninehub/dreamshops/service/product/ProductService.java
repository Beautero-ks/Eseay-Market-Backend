package com.ninehub.dreamshops.service.product;

import java.util.List;
import java.util.Optional;

import com.ninehub.dreamshops.dto.ImageDto;
import com.ninehub.dreamshops.execptions.AlreadyExistsException;
import com.ninehub.dreamshops.model.Image;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ninehub.dreamshops.model.Product;
import com.ninehub.dreamshops.dto.ProductDto;
import com.ninehub.dreamshops.execptions.ProductNotFoundExecption;
import com.ninehub.dreamshops.model.Category;
import com.ninehub.dreamshops.repositry.ProductRepository;
import com.ninehub.dreamshops.repositry.CategoryRepository;
import com.ninehub.dreamshops.repositry.ImageRepository;
import com.ninehub.dreamshops.request.AddProductRequest;
import com.ninehub.dreamshops.dto.auth.UpdateProductRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {

    @Autowired
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ImageRepository imageRepository;
    private final ModelMapper modelMapper;
    
    @Override
    public Product addProduct(AddProductRequest request) {
        // cheeck if the category is found in the DB
        // if Yes, set it as the new product category
        // if No, then save it as new category
        // Then set it as new product category.

        if (productExist(request.getName(), request.getBrand())) {
            throw new AlreadyExistsException(request.getBrand()+ " "+ request.getName()+" already exist! You may update this product instead.");
        }

        Category category = Optional.ofNullable(categoryRepository.findByName(request.getCategory().getName()))
            .orElseGet(()-> {
                Category newCategory = new Category(request.getCategory().getName());
                return categoryRepository.save(newCategory);
            });
            request.setCategory(category);
            return productRepository.save(createProduct(request, category));
    }

    private boolean productExist(String name, String brand){
        return productRepository.existsByNameAndBrand(name, brand);
    }

    private Product createProduct (AddProductRequest request, Category category){
        return new Product(
            request.getName(),
            request.getBrand(),
            request.getPrice(),
            request.getInventory(),
            request.getDescription(),
            category
            );
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(()-> new ProductNotFoundExecption("Product not found!"));
    }

    @Override
    public void deleteProductById(Long id) {
        productRepository.findById(id).ifPresentOrElse(productRepository::delete,
            ()-> {throw new ProductNotFoundExecption("Product not found!");});
    }

    @Override
    public Product updateProduct(UpdateProductRequest request, Long productId) {
       return productRepository.findById(productId)
                .map(existingProduct -> uppdateExistingProduct(existingProduct, request))
                .map(productRepository :: save)
                .orElseThrow(()-> new ProductNotFoundExecption("Product not found!"));
    }

    private Product uppdateExistingProduct(Product existingProduct, UpdateProductRequest request){
        existingProduct.setName(request.getName());
        existingProduct.setBrand(request.getBrand());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setInventory(request.getInventory());
        existingProduct.setDescription(request.getDescription());

        Category category = categoryRepository.findByName(request.getCategory().getName());
        existingProduct.setCategory(category);

        return existingProduct;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategoryName(category);
    }

    @Override
    public List<Product> getProductsByBrand(String brand) {
        return productRepository.findByBrand(brand);
    }

    @Override
    public List<Product> getProductsByCategoryAndBrand(String category, String brand) {
        return productRepository.findByCategoryNameAndBrand(category, brand);
    }

    @Override
    public List<Product> getProductsByName(String name) {
        return productRepository.findByName(name);
    }

    @Override
    public List<Product> getProductsByBrandAndName(String brand, String name) {
        return productRepository.findByBrandAndName(brand, name);
    }

    @Override
    public Long countProductsByBrandAndName(String brand, String name) {
        return productRepository.countByBrandAndName(brand, name);
    }

    @Override
    public ProductDto convertToDto(Product product) {
        ProductDto productDto = modelMapper.map(product, ProductDto.class);
        List<Image> images = imageRepository.findProductById(product.getId());
        List<ImageDto> imageDtos = images.stream()
                .map(image -> modelMapper.map(image, ImageDto.class))
                .toList();
        productDto.setImages(imageDtos);
        System.out.println("Nombre d'images récupérées : " + images.size());
        images.forEach(img -> System.out.println("Image récupérée : " + img.getFileName() + " (ID: " + img.getId() + ")"));

        return productDto;
    }
}
