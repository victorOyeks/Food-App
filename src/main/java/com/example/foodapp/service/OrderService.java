package com.example.foodapp.service;

import com.example.foodapp.payloads.response.*;

import java.util.List;

public interface OrderService {
    List<FoodDataResponse> viewAllItemMenus();
    List<SupplementResponse> viewAllSupplements(String vendorId);
    String addFoodToCartForIndividual(String vendorId, String menuId);
//    String addFoodToCartForCompany(String vendorId, String menuId);
//    //String selectItemForCompany(String vendorId, String menuId);
    String addSupplementToCartForIndividual(String vendorId, String supplementId);
//    String addSupplementToCartForCompany (String vendorId, String supplementId);
//    OrderViewResponse viewCompanyCart();
    UserOrderDetailsResponse viewOrderByOrderIdForUser(String orderId);
    UserOrderDetailsResponse viewOrderByOrderIdForCompany(String orderId);
    //OrderViewResponse viewAllOrdersByUser();
    UserOrderViewResponse viewSimplifiedOrdersByUser();
    UserOrderViewResponse viewSimplifiedOrdersByCompany();
//    UserOrderDetailsResponse viewOrderById(String orderId);
    OrderViewResponse viewUserCart();
    String deleteItem(String orderId, String foodItemId);
    String deleteOrder(String orderId);
}
