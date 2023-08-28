package com.example.foodapp.service;

import com.example.foodapp.payloads.response.*;

import java.util.List;

public interface OrderService {
    List<FoodDataResponse> viewAllItemMenus();
    String selectItemForIndividual (String vendorId, String menuId);
    String selectSupplementsForItemForIndividual(String vendorId, String menuId, List<String>  supplementIds);
    String selectItemForCompany(String vendorId, String menuId);
    OrderViewResponse viewCompanyCart();
    UserOrderDetailsResponse viewOrderByOrderIdForUser(String orderId);
    UserOrderDetailsResponse viewOrderByOrderIdForCompany(String orderId);
    OrderViewResponse viewAllOrdersByUser();
    UserOrderViewResponse viewSimplifiedOrdersByUser();
//    UserOrderDetailsResponse viewOrderById(String orderId);
    OrderViewResponse viewUserCart();
    String deleteItem(String orderId, String foodItemId);
    String deleteOrder(String orderId);
}
