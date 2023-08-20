package com.electratech.ecommerce.dto;

import lombok.Data;

@Data
public class PurchaseResponse {
	// could use @NonNull instead of making field final
	// look into www.projectlombok.org
 private final String orderTrackingNumber;
 
}
