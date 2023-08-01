package com.example.foodapp.service;

import com.example.foodapp.dto.request.BulkItemOrderRequest;
import com.example.foodapp.dto.request.ItemOrderRequest;
import com.example.foodapp.dto.response.FoodDataResponse;
import com.example.foodapp.dto.response.OrderViewResponse;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface OrderService {
    List<FoodDataResponse> viewAllItemMenus();
    String selectItem(String vendorId, ItemOrderRequest orderRequest);
    String createBulkOrder(String vendorId, List<BulkItemOrderRequest> bulkOrders);
    OrderViewResponse viewAllOrdersByCompany();
    OrderViewResponse viewAllOrdersByUser();
    String deleteItem(String orderId, String foodItemId);
    String deleteOrder(String orderId);
}
