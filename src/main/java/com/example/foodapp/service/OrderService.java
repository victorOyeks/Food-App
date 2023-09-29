package com.example.foodapp.service;

import com.example.foodapp.payloads.request.CartItem;
import com.example.foodapp.payloads.request.SupplementItem;
import com.example.foodapp.payloads.response.*;

import java.util.List;

public interface OrderService {
    List<FoodDataResponse> viewAllItemMenus();
    List<SupplementResponse> viewAllSupplements(String vendorId);
//    String addFoodToCartForIndividual(String vendorId, String menuId);
//    String addFoodToCartForCompany(String vendorId, String menuId);
//    //String selectItemForCompany(String vendorId, String menuId);
//    String addSupplementToCartForIndividual(String vendorId, String supplementId);
    OrderViewResponse addToCart(String vendorId, List<CartItem> cartItems, List<SupplementItem> supplementItems);
//    String addSupplementToCartForCompany (String vendorId, String supplementId);
//    OrderViewResponse viewCompanyCart();
    String submitCart (String orderId);
    UserOrderDetailsResponse viewOrderByOrderIdForUser(String orderId);
    UserOrderDetailsResponse viewOrderByOrderIdForCompany(String orderId);
    //OrderViewResponse viewAllOrdersByUser();
    UserOrderViewResponse viewSimplifiedOrdersByUser();
    UserOrderViewResponse viewSimplifiedOrdersByCompany();
//    UserOrderDetailsResponse viewOrderById(String orderId);
    OrderViewResponse viewUserCart();
    OrderViewResponse deleteItem(String orderId, String foodItemId);
    OrderViewResponse deleteSupplement (String orderId, String supplementId);
    String deleteOrder(String orderId);
}
