package com.ninehub.dreamshops.dto;

import com.ninehub.dreamshops.enums.OrderStatus;
import com.ninehub.dreamshops.model.OrderItem;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
public class OrderDto {
    private Long orderId;
    private Long userId;
    private LocalDate orderDate;
    private BigDecimal totalAmount;
    private OrderStatus orderStatus;
    private List<OrderItemDto> orderItems;
}
