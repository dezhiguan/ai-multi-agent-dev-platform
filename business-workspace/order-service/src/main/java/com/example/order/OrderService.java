package com.example.order;

import java.util.List;

public interface OrderService {
    OrderDTO createOrder(OrderCreateRequest request);
    List<OrderDTO> getAllOrders();
    OrderDTO getOrderById(Long id);
}