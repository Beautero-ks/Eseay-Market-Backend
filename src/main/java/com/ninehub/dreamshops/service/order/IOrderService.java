package com.ninehub.dreamshops.service.order;

import com.ninehub.dreamshops.dto.OrderDto;
import com.ninehub.dreamshops.model.Order;

import java.util.List;

public interface IOrderService {
    Order placeOrder(Long userId);
    OrderDto getOrder(Long orderId);

    List<OrderDto> getUserOrders(Long userId);

    OrderDto convertOrderToDto(Order order);
}
