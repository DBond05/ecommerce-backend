package com.electratech.ecommerce.dto;

import java.util.Set;

import com.electratech.ecommerce.entity.Address;
import com.electratech.ecommerce.entity.Customer;
import com.electratech.ecommerce.entity.Order;
import com.electratech.ecommerce.entity.OrderItem;

import lombok.Data;

@Data
public class Purchase {
private Customer customer;
private Address shippingAddress;
private Address billingAddress;
private Order order;
private Set<OrderItem> orderItems;
}
