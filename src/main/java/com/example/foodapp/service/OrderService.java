package com.example.foodapp.service;

import com.example.foodapp.payloads.request.CartItemWithSupplements;
import com.example.foodapp.payloads.request.SupplementItem;
import com.example.foodapp.payloads.response.*;

import java.util.List;

public interface OrderService {
//    List<FoodDataResponse> viewAllItemMenus();
    List<FoodDataResponse> viewFoodItemsByVendorAndCategory(String vendorId, String categoryId);
    List<SupplementResponse> viewAllSupplements(String vendorId);
    OrderViewResponse addToCart(String vendorId, List<CartItemWithSupplements> cartItemsWithSupplements);
//    OrderViewResponse addToCart(String vendorId, List<CartItemWithSupplements> cartItemWithSupplements, List<SupplementItem> supplementItems);
    String submitCart (String orderId);
    UserOrderDetailsResponse viewOrderByOrderIdForUser(String orderId);
    //OrderViewResponse viewAllOrdersByUser();
    UserOrderViewResponse viewSimplifiedOrdersByUser();
//    UserOrderDetailsResponse viewOrderById(String orderId);
//    OrderViewResponse viewUserCart();
//    OrderViewResponse deleteItem(String orderId, String foodItemId);
//    OrderViewResponse deleteSupplement (String orderId, String supplementId);
    String deleteOrder(String orderId);
}
