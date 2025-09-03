package com.ninehub.dreamshops.repositry;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import com.ninehub.dreamshops.dto.ImageDto;
import com.ninehub.dreamshops.model.Image;

@Repository
public interface ImageRepository extends JpaRepository <Image, Long>{
    @Query("SELECT i FROM Image i WHERE i.product.id = :id")
    List<Image> findProductById(@Param("id") Long id);

}
