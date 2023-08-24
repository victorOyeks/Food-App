package com.example.foodapp.service;

import com.example.foodapp.payloads.response.FoodDataResponse;
import com.example.foodapp.payloads.response.OrderViewResponse;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {
    List<FoodDataResponse> viewAllItemMenus();
    String selectItemForIndividual (String vendorId, String menuId);
    String selectItemWithSupplementForIndividual (String vendorId, String menuId, String supplementName, BigDecimal supplementPrice);
    String selectItemForCompany(String vendorId, String menuId);
    OrderViewResponse viewAllOrdersByCompany();
    OrderViewResponse viewAllOrdersByUser();
    String deleteItem(String orderId, String foodItemId);
    String deleteOrder(String orderId);
}
