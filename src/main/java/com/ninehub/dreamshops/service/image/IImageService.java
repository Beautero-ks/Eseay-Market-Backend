package com.ninehub.dreamshops.service.image;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ninehub.dreamshops.dto.ImageDto;
import com.ninehub.dreamshops.model.Image;

public interface IImageService {
    Image getImageById(Long id);
    void deleteImageById(Long id);
    List<ImageDto> saveImages(List<MultipartFile> files, Long productId);
    void updateImage(MultipartFile file, Long imageId);
}
