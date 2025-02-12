package com.example.multikart.domain.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders", indexes = @Index(columnList = "customer_id, transport_id, payment_id, status"))
public class Order extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "transport_id", nullable = false)
    private Long transportId;

    @Column(name = "payment_id", nullable = false)
    private Long paymentId;

    @NotBlank
    private String name;

    private String address;

    private String provinceId;

    private String districtId;

    private String wardId;

    // Ngày giao hàng
    @Column(name = "delivery_date")
    private LocalDateTime deliveryDate;

    private Float totalPrice;

    // Trạng thái
    @Column(name = "status", columnDefinition = "integer default 1", nullable = false)
    private Integer status;
}
