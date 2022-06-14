package com.example.multikart.repo;

import com.example.multikart.domain.dto.ItemProductDTO;
import com.example.multikart.domain.model.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {
    List<Product> findAllByStatus(Integer status);

    List<Product> findAllByCategoryIdAndStatus(Long categoryId, Integer status);

    Product findByProductIdAndStatus(Long productId, Integer status);

    Product findBySlugAndStatus(String slug, Integer status);

    int countByNameAndStatus(String name, Integer status);

    int countBySlugAndStatus(String slug, Integer status);

    @Query("SELECT new com.example.multikart.domain.dto.ItemProductDTO(p, c, u ,s, pi)\n" +
            "FROM Product p\n" +
            "LEFT JOIN Category c on p.categoryId = c.categoryId\n" +
            "LEFT JOIN Unit u on p.unitId = u.unitId\n" +
            "LEFT JOIN Supplier s on p.supplierId = s.supplierId\n" +
            "LEFT JOIN ProductImage pi on p.productId = pi.productId\n" +
            "WHERE p.status = :status\n" +
            "  and p.slug = :slug" +
            "  and pi.position = (Select min(position) from ProductImage where productId = p.productId)")
    ItemProductDTO findItemProductBySlugAndStatus(@Param("slug") String slug, @Param("status") Integer status);

    @Query("SELECT p\n" +
            "FROM Product p\n" +
            "WHERE p.status = :status\n" +
            "  and p.categoryId = :categoryId\n" +
            "  and p.productId <> :productId")
    List<Product> findRelatedByProductIdAndStatus(@Param("productId") Long productId, @Param("categoryId") Long categoryId, @Param("status") Integer status);
}
