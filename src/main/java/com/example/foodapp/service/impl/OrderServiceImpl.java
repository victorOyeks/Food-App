package com.example.foodapp.service.impl;

import com.example.foodapp.constant.DeliveryStatus;
import com.example.foodapp.constant.PaymentStatus;
import com.example.foodapp.payloads.response.*;
import com.example.foodapp.entities.*;
import com.example.foodapp.entities.Order;
import com.example.foodapp.exception.CustomException;
import com.example.foodapp.repository.*;
import com.example.foodapp.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final VendorRepository vendorRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final SupplementRepository supplementRepository;
    private final ItemMenuRepository itemMenuRepository;

    public List<FoodDataResponse> viewAllItemMenus() {
        List<FoodDataResponse> foodDataResponse = new ArrayList<>();
        List<Vendor> vendors = vendorRepository.findAll();

        for (Vendor vendor : vendors) {
            for (ItemCategory itemCategory : vendor.getItemCategory()) {
                for (ItemMenu itemMenu : itemCategory.getItemMenus()) {
                    foodDataResponse.add(FoodDataResponse.builder()
                            .itemId(itemMenu.getItemId())
                            .itemName(itemMenu.getItemName())
                            .price(itemMenu.getItemPrice())
                            .imageUri(itemMenu.getImageUrl())
                            .vendorName(vendor.getBusinessName())
                            .build());
                }
            }
        }
        return foodDataResponse;
    }

   /* public String addFoodToCartForIndividual(String vendorId, String menuId) {

        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new CustomException("Vendor not found!!!"));
        User user = getAuthenticatedUser();

        ItemMenu selectedItemMenu = findFoodMenuById(vendor.getId(), menuId);

        if (selectedItemMenu != null) {
            Order existingOpenOrder = orderRepository.findOpenOrderByUser(user.getId());

            if (existingOpenOrder != null) {
                List<ItemMenu> selectedItemMenus = existingOpenOrder.getItemMenus();
                selectedItemMenus.add(selectedItemMenu);
                existingOpenOrder.setItemMenus(selectedItemMenus);

                BigDecimal totalAmount = existingOpenOrder.getTotalAmount().add(selectedItemMenu.getItemPrice());
                existingOpenOrder.setTotalAmount(totalAmount);

                orderRepository.save(existingOpenOrder);
            } else {
                Order newOrder = new Order();
                newOrder.setUser(user);

                List<ItemMenu> selectedItemMenus = new ArrayList<>();
                selectedItemMenus.add(selectedItemMenu);
                newOrder.setItemMenus(selectedItemMenus);

                BigDecimal totalAmount = selectedItemMenu.getItemPrice();
                newOrder.setTotalAmount(totalAmount);
                newOrder.setDeliveryStatus(DeliveryStatus.PENDING);
                newOrder.setPaymentStatus(PaymentStatus.PENDING);

                orderRepository.save(newOrder);
            }
            if (user.getOrderList() == null) {
                user.setOrderList(new ArrayList<>());
            }

            return "Food selected successfully!!!";
        } else {
            throw new CustomException("Item menu not found!!!");
        }
    }

    */

    public String addFoodToCartForIndividual(String vendorId, String menuId) {

        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new CustomException("Vendor not found!!!"));
        User user = getAuthenticatedUser();

        ItemMenu selectedItemMenu = findFoodMenuById(vendor.getId(), menuId);

        if (selectedItemMenu != null) {
            Order existingOpenOrder = orderRepository.findOpenOrderByUser(user.getId());

            if (existingOpenOrder != null) {
                // Check if the item is already in the cart
                Map<String, Integer> cartItems = existingOpenOrder.getItemMenus();
                if (cartItems.containsKey(menuId)) {
                    int quantity = cartItems.get(menuId);
                    cartItems.put(menuId, quantity + 1);
                } else {
                    // If not in cart, add it with quantity 1
                    cartItems.put(menuId, 1);
                }

                // Update total amount based on item price
                BigDecimal totalAmount = existingOpenOrder.getTotalAmount().add(selectedItemMenu.getItemPrice());
                existingOpenOrder.setTotalAmount(totalAmount);

                orderRepository.save(existingOpenOrder);
            } else {
                Order newOrder = new Order();
                newOrder.setUser(user);

                // Initialize cart with the selected item and quantity 1
                Map<String, Integer> cartItems = new HashMap<>();
                cartItems.put(menuId, 1);
                newOrder.setItemMenus(cartItems);

                // Set total amount to item price
                BigDecimal totalAmount = selectedItemMenu.getItemPrice();
                newOrder.setTotalAmount(totalAmount);
                newOrder.setDeliveryStatus(DeliveryStatus.PENDING);
                newOrder.setPaymentStatus(PaymentStatus.PENDING);

                orderRepository.save(newOrder);
            }
            if (user.getOrderList() == null) {
                user.setOrderList(new ArrayList<>());
            }

            return "Food selected successfully!!!";
        } else {
            throw new CustomException("Item menu not found!!!");
        }
    }


    public String addSupplementToCartForIndividual(String vendorId, String supplementId) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new CustomException("Vendor not found!!!"));
        User user = getAuthenticatedUser();

        Supplement selectedSupplement = supplementRepository.findById(supplementId)
                .orElseThrow(() -> new CustomException("Supplement not found!!!"));

        if (selectedSupplement != null) {
            Order existingOpenOrder = orderRepository.findOpenOrderByUser(user.getId());

            if (existingOpenOrder != null) {
                // Check if the supplement is already in the cart
                Map<String, Integer> cartItems = existingOpenOrder.getSupplements();
                if (cartItems.containsKey(supplementId)) {
                    int quantity = cartItems.get(supplementId);
                    cartItems.put(supplementId, quantity + 1);
                } else {
                    // If not in cart, add it with quantity 1
                    cartItems.put(supplementId, 1);
                }

                // Update total amount based on supplement price
                BigDecimal totalAmount = existingOpenOrder.getTotalAmount().add(selectedSupplement.getSupplementPrice());
                existingOpenOrder.setTotalAmount(totalAmount);

                orderRepository.save(existingOpenOrder);
            } else {
                Order newOrder = new Order();
                newOrder.setUser(user);

                // Initialize cart with the selected supplement and quantity 1
                Map<String, Integer> cartItems = new HashMap<>();
                cartItems.put(supplementId, 1);
                newOrder.setSupplements(cartItems);

                // Set total amount to supplement price
                BigDecimal totalAmount = selectedSupplement.getSupplementPrice();
                newOrder.setTotalAmount(totalAmount);
                newOrder.setDeliveryStatus(DeliveryStatus.PENDING);
                newOrder.setPaymentStatus(PaymentStatus.PENDING);

                orderRepository.save(newOrder);
            }
            if (user.getOrderList() == null) {
                user.setOrderList(new ArrayList<>());
            }

            return "Supplement selected successfully!!!";
        } else {
            throw new CustomException("Supplement not found!!!");
        }
    }


    /* public String selectItemForCompany(String vendorId, String menuId) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new CustomException("Vendor not found!!!"));
        Company company = getAuthenticatedCompany();
        ItemMenu itemMenu = findFoodMenuById(vendor.getId(), menuId);

        if (itemMenu != null) {
            Order existingOpenOrder = orderRepository.findOpenOrderByCompany(company.getId());

            if (existingOpenOrder != null) {
                List<ItemMenu> selectedItemMenus = existingOpenOrder.getItemMenus();
                selectedItemMenus.add(itemMenu);
                existingOpenOrder.setItemMenus(selectedItemMenus);

                BigDecimal totalAmount = existingOpenOrder.getTotalAmount().add(itemMenu.getItemPrice());
                existingOpenOrder.setTotalAmount(totalAmount);

                orderRepository.save(existingOpenOrder);
            } else {
                Order newOrder = new Order();
                newOrder.setCompany(company);

                List<ItemMenu> selectedItemMenus = new ArrayList<>();
                selectedItemMenus.add(itemMenu);
                newOrder.setItemMenus(selectedItemMenus);

                BigDecimal totalAmount = itemMenu.getItemPrice();
                newOrder.setTotalAmount(totalAmount);
                newOrder.setDeliveryStatus(DeliveryStatus.PENDING);
                newOrder.setPaymentStatus(PaymentStatus.PENDING);

                orderRepository.save(newOrder);
            }
            if (company.getOrderList() == null) {
                company.setOrderList(new ArrayList<>());
            }
            return "Food selected successfully!!!";
        } else {
            throw new CustomException("Item menu not found!!!");
        }
    }

     */

    private ItemMenu findFoodMenuById(String vendorId, String foodMenuId) {
        Vendor vendor = vendorRepository.findById(vendorId).orElseThrow(()-> new CustomException("Vendor nor found!!!"));
        for (ItemCategory itemCategory : vendor.getItemCategory()) {
            for (ItemMenu itemMenu : itemCategory.getItemMenus()) {
                if (itemMenu.getItemId().equals(foodMenuId)) {
                    return itemMenu;
                }
            }
        }
        return null;
    }

    public UserOrderDetailsResponse viewOrderByOrderIdForUser(String orderId) {
        String userId = getAuthenticatedUser().getId();
        Optional<Order> orderOptional = orderRepository.findByOrderIdAndUserId(orderId, userId);

        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            return buildOrderResponse(order);
        } else {
            throw new CustomException("Order not found with orderId: " + orderId);
        }
    }

    public UserOrderDetailsResponse viewOrderByOrderIdForCompany(String orderId) {
        String companyId = getAuthenticatedCompany().getId();
        Optional<Order> orderOptional = orderRepository.findByOrderIdAndCompanyId(orderId, companyId);

        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            return buildOrderResponse(order);
        } else {
            throw new CustomException("Order not found with orderId: " + orderId);
        }
    }

    /*
    public OrderViewResponse viewAllOrdersByUser() {
        String userId = getAuthenticatedUser().getId();
        return viewAllOrdersInternal(orderRepository.findOrdersByUserId(userId));
    }
     */

    public UserOrderViewResponse viewSimplifiedOrdersByUser() {
        String userId = getAuthenticatedUser().getId();
        List<Order> userOrders = orderRepository.findOrdersByUserId(userId);
        List<SimplifiedOrderResponse> simplifiedOrders = viewSimplifiedOrdersInternal(userOrders);
        return new UserOrderViewResponse(simplifiedOrders, null);
    }

    public OrderViewResponse viewUserCart() {
        String userId = getAuthenticatedUser().getId();
        List<Order> pendingOrders = orderRepository.findPendingOrdersByUserId(userId);
        return viewAllOrdersInternal(pendingOrders);
    }

    public OrderViewResponse viewCompanyCart() {
        String companyId = getAuthenticatedCompany().getId();
        List<Order> pendingOrders = orderRepository.findPendingOrdersByCompanyId(companyId);
        return viewAllOrdersInternal(pendingOrders);
    }

    /* public String deleteItem(String orderId, String foodItemId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException("Order not found!!!"));
        List<ItemMenu> foodItems = order.getItemMenus();
        for (ItemMenu itemMenu : foodItems) {
            if (itemMenu.getItemId().equals(foodItemId)) {
                foodItems.remove(itemMenu);
                order.setItemMenus(foodItems);
                order.setTotalAmount(calculateTotalAmount(order));
                orderRepository.save(order);
                return itemMenu.getItemName() + " deleted successfully!!!";
            }
        }
        throw new CustomException("Food item not found in the order!!!");
    }

     */

    public String deleteItem(String orderId, String foodItemId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException("Order not found!!!"));

        Map<String, Integer> cartItems = order.getItemMenus();

        if (cartItems.containsKey(foodItemId)) {
            int quantity = cartItems.get(foodItemId);

            // Decrease the quantity by 1, or remove if it's 1
            if (quantity > 1) {
                cartItems.put(foodItemId, quantity - 1);
            } else {
                cartItems.remove(foodItemId);
            }

            // Recalculate the total amount based on the updated cartItems
            order.setTotalAmount(calculateTotalAmount(order));
            orderRepository.save(order);

            // Retrieve the item name from your data source (e.g., database) if needed
            // String itemName = getItemNameById(foodItemId);

            return "Item deleted successfully!!!"; // Return a success message
        } else {
            throw new CustomException("Food item not found in the cart!!!");
        }
    }

    public String deleteOrder(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException("Order not found!!!"));
        orderRepository.delete(order);
        return "Order deleted successfully!!!";
    }

    /*********************** HELPER METHODS ************************/

    /* private BigDecimal calculateTotalAmount(Order order) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (ItemMenu itemMenu : order.getItemMenus()) {
            totalAmount = totalAmount.add(itemMenu.getItemPrice());
        }
        return totalAmount;
    }

     */

    private BigDecimal calculateTotalAmount(Order order) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        Map<String, Integer> cartItems = order.getItemMenus();

        for (Map.Entry<String, Integer> entry : cartItems.entrySet()) {
            String itemId = entry.getKey();
            int quantity = entry.getValue();

            // Retrieve the ItemMenu object from your data source using itemId
            ItemMenu itemMenu = itemMenuRepository.findByItemId(itemId);

            // Calculate the total amount for this item based on its price and quantity
            BigDecimal itemTotal = itemMenu.getItemPrice().multiply(BigDecimal.valueOf(quantity));
            totalAmount = totalAmount.add(itemTotal);
        }

        return totalAmount;
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new CustomException("User not found");
        }
        return user;
    }

    private Company getAuthenticatedCompany() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        Company company = companyRepository.findByCompanyEmail(userEmail);
        if (company == null) {
            throw new CustomException("Company not found");
        }
        return company;
    }


    /* private OrderViewResponse viewAllOrdersInternal(List<Order> orderList) {
        List<OrderResponse> orderResponses = new ArrayList<>();
        int totalFoodItems = 0;
        BigDecimal totalSum = BigDecimal.ZERO;

        for (Order order : orderList) {
            List<FoodDataResponse> foodDataResponses = new ArrayList<>();

            for (ItemMenu itemMenu : order.getItemMenus()) {
                Vendor vendor = itemMenu.getItemCategory().getVendor();
                foodDataResponses.add(FoodDataResponse.builder()
                        .itemId(itemMenu.getItemId())
//                        .recipient(order.getUser().getFirstName())
                        .itemName(itemMenu.getItemName())
                        .price(itemMenu.getItemPrice())
                        .imageUri(itemMenu.getImageUrl())
                        .vendorName(vendor.getBusinessName())
                        .build());
                totalFoodItems++;
            }
            orderResponses.add(OrderResponse.builder()
                    .orderId(order.getOrderId())
                    .items(foodDataResponses)
                    .totalAmount(order.getTotalAmount())
                    .build());

            totalSum = totalSum.add(order.getTotalAmount());
        }

        OrderSummary orderSummary = OrderSummary.builder()
                .totalItems(totalFoodItems)
                .totalSum(totalSum)
                .build();

        return new OrderViewResponse(orderResponses, orderSummary);
    }

     */

    private OrderViewResponse viewAllOrdersInternal(List<Order> orderList) {
        List<OrderResponse> orderResponses = new ArrayList<>();
        int totalFoodItems = 0;
        BigDecimal totalSum = BigDecimal.ZERO;

        for (Order order : orderList) {
            List<FoodDataResponse> foodDataResponses = new ArrayList<>();

            // Iterate through the cartItems map
            for (Map.Entry<String, Integer> entry : order.getItemMenus().entrySet()) {
                String itemId = entry.getKey();
                int quantity = entry.getValue();

                // Retrieve the ItemMenu object from your data source using itemId
                ItemMenu itemMenu = itemMenuRepository.findByItemId(itemId);

                totalFoodItems += quantity;

                Vendor vendor = itemMenu.getItemCategory().getVendor();
                foodDataResponses.add(FoodDataResponse.builder()
                        .itemId(itemId)
                        .itemName(itemMenu.getItemName())
                        .price(itemMenu.getItemPrice())
                        .imageUri(itemMenu.getImageUrl())
                                .quantity(quantity)
                        .vendorName(vendor.getBusinessName())
                        .build());


            }
            orderResponses.add(OrderResponse.builder()
                    .orderId(order.getOrderId())
                    .items(foodDataResponses)
                    .totalAmount(order.getTotalAmount())
                    .build());

            totalSum = totalSum.add(order.getTotalAmount());
        }
        OrderSummary orderSummary = OrderSummary.builder()
                .totalItems(totalFoodItems)
                .totalSum(totalSum)
                .build();

        return new OrderViewResponse(orderResponses, orderSummary);
    }


    private List<SimplifiedOrderResponse> viewSimplifiedOrdersInternal(List<Order> orderList) {
        List<SimplifiedOrderResponse> simplifiedOrderResponses = new ArrayList<>();

        for (Order order : orderList) {
            simplifiedOrderResponses.add(new SimplifiedOrderResponse(
                    order.getOrderId(),
                    order.getTotalAmount(),
                    order.getDeliveryStatus(),
                    order.getPaymentStatus()
            ));
        }
        return simplifiedOrderResponses;
    }

/*    private UserOrderDetailsResponse buildOrderResponse(Order order) {
        List<FoodDataResponse> foodDataResponses = new ArrayList<>();

        for (ItemMenu itemMenu : order.getItemMenus()) {
            Vendor vendor = itemMenu.getItemCategory().getVendor();
            foodDataResponses.add(FoodDataResponse.builder()
                    .itemId(itemMenu.getItemId())
                    .itemName(itemMenu.getItemName())
                    .price(itemMenu.getItemPrice())
                    .imageUri(itemMenu.getImageUrl())
                    .vendorName(vendor.getBusinessName())
                    .build());
        }

        return UserOrderDetailsResponse.builder()
                .orderId(order.getOrderId())
                .itemMenu(order.getItemMenus())
                .totalAmount(order.getTotalAmount())
                .paymentStatus(order.getPaymentStatus())
                .deliveryStatus(order.getDeliveryStatus())
                .build();
    }

 */

    private UserOrderDetailsResponse buildOrderResponse(Order order) {
        List<FoodDataResponse> foodDataResponses = new ArrayList<>();

        // Iterate through the cartItems map
        for (Map.Entry<String, Integer> entry : order.getItemMenus().entrySet()) {
            String itemId = entry.getKey();
            int quantity = entry.getValue();

            // Retrieve the ItemMenu object from your data source using itemId
            ItemMenu itemMenu = itemMenuRepository.findByItemId(itemId);
            Vendor vendor = itemMenu.getItemCategory().getVendor();

            // Create multiple FoodDataResponse objects based on the quantity
            for (int i = 0; i < quantity; i++) {
                foodDataResponses.add(FoodDataResponse.builder()
                        .itemId(itemId)
                        .itemName(itemMenu.getItemName())
                        .price(itemMenu.getItemPrice())
                        .imageUri(itemMenu.getImageUrl())
                        .vendorName(vendor.getBusinessName())
                        .build());
            }
        }

        return UserOrderDetailsResponse.builder()
                .orderId(order.getOrderId())
                .itemMenu(foodDataResponses) // Use foodDataResponses instead of order.getItemMenus()
                .totalAmount(order.getTotalAmount())
                .paymentStatus(order.getPaymentStatus())
                .deliveryStatus(order.getDeliveryStatus())
                .build();
    }

}