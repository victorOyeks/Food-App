package com.example.foodapp.service;

import com.example.foodapp.payloads.response.*;

import java.util.List;

public interface OrderService {
    List<FoodDataResponse> viewAllItemMenus();
    String addFoodToCartForIndividual(String vendorId, String menuId);
    //String selectItemForCompany(String vendorId, String menuId);
    String addSupplementToCartForIndividual(String vendorId, String supplementId);
    OrderViewResponse viewCompanyCart();
    UserOrderDetailsResponse viewOrderByOrderIdForUser(String orderId);
    UserOrderDetailsResponse viewOrderByOrderIdForCompany(String orderId);
    //OrderViewResponse viewAllOrdersByUser();
    UserOrderViewResponse viewSimplifiedOrdersByUser();
//    UserOrderDetailsResponse viewOrderById(String orderId);
    OrderViewResponse viewUserCart();
    String deleteItem(String orderId, String foodItemId);
    String deleteOrder(String orderId);
}
