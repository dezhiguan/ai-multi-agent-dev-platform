package com.example.ordermanagement.service;

import com.example.ordermanagement.dto.OrderCreateRequest;
import com.example.ordermanagement.dto.OrderDTO;

import java.util.List;

public interface OrderService {
    OrderDTO createOrder(OrderCreateRequest request);
    List<OrderDTO> getAllOrders();
    OrderDTO getOrderById(Long id);
}