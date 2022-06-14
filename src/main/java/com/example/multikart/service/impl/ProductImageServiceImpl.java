package com.example.multikart.service.impl;

import com.example.multikart.common.Const.DefaultStatus;
import com.example.multikart.common.DataUtils;
import com.example.multikart.domain.model.ProductImage;
import com.example.multikart.repo.ProductImageRepository;
import com.example.multikart.repo.ProductRepository;
import com.example.multikart.service.ProductImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
@Slf4j
public class ProductImageServiceImpl implements ProductImageService {
    @Autowired
    private ProductImageRepository productImageRepository;
    @Autowired
    private ProductRepository productRepository;

    private String productDirectory = "uploads/images/products";

    @Override
    public String findAllProductImages(Long id, Model model, RedirectAttributes redirect) {
        var product = productRepository.findByProductIdAndStatus(id, DefaultStatus.ACTIVE);
        if (DataUtils.isNullOrEmpty(product)) {
            redirect.addFlashAttribute("error", "Sản phẩm không tồn tại");

            return "redirect:/dashboard/products";
        }
        model.addAttribute("product", product);

        var images = productImageRepository.findAllByProductIdAndStatus(id, DefaultStatus.ACTIVE);
        model.addAttribute("images", images);

        return "backend/product_image/index";
    }

    @Override
    public String upload(Long id, MultipartFile file, RedirectAttributes redirect) throws IOException {
        var product = productRepository.findByProductIdAndStatus(id, DefaultStatus.ACTIVE);
        if (DataUtils.isNullOrEmpty(product)) {
            redirect.addFlashAttribute("error", "Sản phẩm không tồn tại");

            return "redirect:/dashboard/products";
        }

        if (DataUtils.isNullOrEmpty(file)) {
            redirect.addFlashAttribute("error", "Vui lòng chọn file");

            return "redirect:/dashboard/products/" + id + "/images";
        }

        Path uploadPath = Paths.get(productDirectory);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String uploadDir = productDirectory + "/" + id;

        try {
            saveFile(uploadDir, fileName, file);

            var count = productImageRepository.countByProductIdAndStatus(id, DefaultStatus.ACTIVE);
            var productImage = ProductImage.builder()
                    .productId(id)
                    .url(uploadDir + "/" + fileName)
                    .position(count)
                    .status(DefaultStatus.ACTIVE)
                    .build();
            productImageRepository.save(productImage);

            redirect.addFlashAttribute("success", "Thêm thành công");
        } catch (IOException e) {
            e.printStackTrace();
            redirect.addFlashAttribute("error", "Thêm không thành công");
        }

        return "redirect:/dashboard/products/" + id + "/images";
    }

    private void saveFile(String uploadDir, String filename, MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(filename);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioe) {
            throw new IOException("Could not save image file: " + filename, ioe);
        }
    }
}
