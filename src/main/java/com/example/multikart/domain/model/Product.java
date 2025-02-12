package com.example.multikart.domain.model;

import com.example.multikart.domain.dto.ProductRequestDTO;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products", indexes = @Index(columnList = "category_id, unit_id, supplier_id, slug, status"))
public class Product extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "unit_id", nullable = false)
    private Long unitId;

    @Column(name = "supplier_id", nullable = false)
    private Long supplierId;

    @NotBlank
    private String name;

    @NotBlank
    private String slug;

    @Column(columnDefinition = "integer default 0")
    private Integer amount;

    @Column(name = "import_price", columnDefinition = "float default 0")
    private Float importPrice;

    @Column(name = "export_price", columnDefinition = "float default 0")
    private Float exportPrice;

    // Mô tả, ghi chú
    @Column(columnDefinition = "text")
    private String description;

    // Trạng thái
    @Column(name = "status", columnDefinition = "integer default 1", nullable = false)
    private Integer status;

    public Product(ProductRequestDTO input) {
        name = input.getName();
        slug = input.getSlug();
        categoryId = input.getCategoryId();
        unitId = input.getUnitId();
        supplierId = input.getSupplierId();
        amount = input.getAmount();
        importPrice = input.getImportPrice();
        exportPrice = input.getExportPrice();
        description = input.getDescription();
        status = input.getStatus();
    }
}
