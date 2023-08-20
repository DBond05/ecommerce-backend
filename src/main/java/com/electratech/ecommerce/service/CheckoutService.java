package com.electratech.ecommerce.service;

import com.electratech.ecommerce.dto.Purchase;
import com.electratech.ecommerce.dto.PurchaseResponse;

public interface CheckoutService {

	PurchaseResponse placeOrder(Purchase purchase);
}
