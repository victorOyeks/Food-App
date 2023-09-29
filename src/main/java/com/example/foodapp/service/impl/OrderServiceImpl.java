package com.example.foodapp.service.impl;

import com.example.foodapp.constant.DeliveryStatus;
import com.example.foodapp.constant.SubmitStatus;
import com.example.foodapp.payloads.request.CartItem;
import com.example.foodapp.payloads.request.SupplementItem;
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

        User user = getAuthenticatedUser();
        List<FoodDataResponse> foodDataResponse = new ArrayList<>();
        List<Vendor> vendors = user.getCompany().getVendors(); //vendorRepository.findAll();

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

    public List<SupplementResponse> viewAllSupplements(String vendorId) {

        User user = getAuthenticatedUser();
        Company userCompany = user.getCompany();
        if (userCompany == null){
            throw new CustomException("User is not associated with this company");
        }

        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new CustomException("Vendor not found"));

        if (!userCompany.getVendors().contains(vendor)){
            throw new CustomException("Vendor not associated with this user's company");
        }

        List<Supplement> supplements = supplementRepository.findByVendorId(vendorId);
        List<SupplementResponse> supplementResponses = supplements.stream()
                .map(supplement -> SupplementResponse.builder()
                        .supplementId(supplement.getSupplementId())
                        .supplementName(supplement.getSupplementName())
                        .supplementPrice(supplement.getSupplementPrice())
                        .supplementCategory(supplement.getSupplementCategory())
                        .build())
                .collect(Collectors.toList());

        return supplementResponses;
    }

    public OrderViewResponse addToCart(String vendorId, List<CartItem> cartItems, List<SupplementItem> supplementItems) {
        User user = getAuthenticatedUser();
        Company userCompany = user.getCompany();
        if (userCompany == null) {
            throw new CustomException("User is not associated with this company");
        }
        Vendor vendor = vendorRepository.findById(vendorId).orElseThrow(()-> new CustomException("Vendor not found"));
        if(!userCompany.getVendors().contains(vendor)){
            throw new CustomException("Vendor is not associated to user's company");
        }
        Order existingOpenOrder = orderRepository.findOpenOrderByUser(user.getId());
        if (existingOpenOrder != null) {

            // Update existing open order with cart items and supplements
            Map<String, Integer> orderItems = existingOpenOrder.getItemMenus();
            Map<String, Integer> orderSupplements = existingOpenOrder.getSupplements();
            for (CartItem cartItem : cartItems) {
                String itemId = cartItem.getItemId();
                int quantity = cartItem.getQuantity();
                ItemMenu selectedItemMenu = findFoodMenuById(vendor.getId(), itemId);
                if (selectedItemMenu != null) {
                    // Check if the item is already in the cart
                    if (orderItems.containsKey(itemId)) {
                        int existingQuantity = orderItems.get(itemId);
                        orderItems.put(itemId, existingQuantity + quantity);
                    } else {
                        orderItems.put(itemId, quantity);
                    }
                } else {
                    throw new CustomException("Item menu not found!!!");
                }
            }

            for (SupplementItem supplementItem : supplementItems) {
                String supplementId = supplementItem.getSupplementId();
                int quantity = supplementItem.getQuantity();
                Supplement selectedSupplement = supplementRepository.findById(supplementId)
                        .orElseThrow(() -> new CustomException("Supplement not found!!!"));
                if (selectedSupplement != null) {
                    // Check if the supplement is already in the cart
                    if (orderSupplements.containsKey(supplementId)) {
                        int existingQuantity = orderSupplements.get(supplementId);
                        orderSupplements.put(supplementId, existingQuantity + quantity);
                    } else {
                        orderSupplements.put(supplementId, quantity);
                    }
                } else {
                    throw new CustomException("Supplement not found");
                }
            }

            // Update total amount based on cart items and supplements
            BigDecimal totalAmount = calculateTotalAmount(existingOpenOrder);
            existingOpenOrder.setTotalAmount(totalAmount);
//            existingOpenOrder.setVendor(vendor);
            orderRepository.save(existingOpenOrder);
        } else {
            // Create a new order with cart items and supplements
            Order newOrder = new Order();
            newOrder.setUser(user);

            Map<String, Integer> orderItems = new HashMap<>();
            Map<String, Integer> orderSupplements = new HashMap<>();

            for (CartItem cartItem : cartItems) {
                String itemId = cartItem.getItemId();
                int quantity = cartItem.getQuantity();
                ItemMenu selectedItemMenu = findFoodMenuById(vendor.getId(), itemId);
                if (selectedItemMenu != null) {
                    orderItems.put(itemId, quantity);
                } else {
                    throw new CustomException("Item menu not found!!!");
                }
            }
            for (SupplementItem supplementItem : supplementItems) {
                String supplementId = supplementItem.getSupplementId();
                int quantity = supplementItem.getQuantity();

                Supplement selectedSupplement = supplementRepository.findById(supplementId)
                        .orElseThrow(() -> new CustomException("Supplement not found!!!"));
                orderSupplements.put(selectedSupplement.getSupplementId(), quantity);
            }
            newOrder.setItemMenus(orderItems);
            newOrder.setSupplements(orderSupplements);

            // Set total amount based on cart items and supplements
            BigDecimal totalAmount = calculateTotalAmount(newOrder);
            newOrder.setTotalAmount(totalAmount);
            newOrder.setDeliveryStatus(DeliveryStatus.PENDING);
            newOrder.setSubmitStatus(SubmitStatus.PENDING);
            newOrder.setVendor(vendor);

            if (totalAmount.compareTo(userCompany.getPriceLimit()) > 0) {
                throw new CustomException("You cannot add items worth more than " + userCompany.getPriceLimit() + " to cart");
            }
            orderRepository.save(newOrder);
        }
        if (user.getOrderList() == null) {
            user.setOrderList(new ArrayList<>());
        }
        return viewUserCart();
    }

    public OrderViewResponse submitCart (String orderId){
        User user = getAuthenticatedUser();
        Order order = orderRepository.findOrderByOrderIdAndUserId(orderId, user.getId());

//        if(!user.getOrderList().contains(order)){
//            throw new CustomException("Order does not belong to user");
//        }

        if(order.getSubmitStatus().equals(SubmitStatus.SUBMITTED)){
            throw new CustomException("Order already submitted!!!");
        }
        order.setSubmitStatus(SubmitStatus.SUBMITTED);
        orderRepository.save(order);

        return viewUserCart();
    }

    /*
    public String addFoodToCartForIndividual(String vendorId, String menuId) {

        User user = getAuthenticatedUser();
        Company userCompany = user.getCompany();
        if(userCompany == null){
            throw new CustomException("User not found for this company");
        }

        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new CustomException("Vendor not found"));

        if (!userCompany.getVendors().contains(vendor)){
            throw new CustomException("Vendor not associated with this user's company");
        }

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
                    cartItems.put(menuId, 1);
                }
                BigDecimal totalAmount = existingOpenOrder.getTotalAmount().add(selectedItemMenu.getItemPrice());
                if(totalAmount.compareTo(userCompany.getPriceLimit()) > 0){
                    throw new CustomException("You can not add items worth more than " + userCompany.getPriceLimit() + " to cart");
                }
                existingOpenOrder.setTotalAmount(totalAmount);

                orderRepository.save(existingOpenOrder);
            } else {
                Order newOrder = new Order();
                newOrder.setUser(user);
                newOrder.setVendor(vendor);

                // Initialize cart with the selected item and quantity 1
                Map<String, Integer> cartItems = new HashMap<>();
                cartItems.put(menuId, 1);
                newOrder.setItemMenus(cartItems);

                // Set total amount to item price
                BigDecimal totalAmount = selectedItemMenu.getItemPrice();
                newOrder.setTotalAmount(totalAmount);
                newOrder.setDeliveryStatus(DeliveryStatus.PENDING);
                newOrder.setSubmitStatus(SubmitStatus.PENDING);

                if(totalAmount.compareTo(userCompany.getPriceLimit()) > 0){
                    throw new CustomException("You can not add items worth more than " + userCompany.getPriceLimit() + " to cart");
                }
                orderRepository.save(newOrder);
            }
            if (user.getOrderList() == null) {
                user.setOrderList(new ArrayList<>());
            }

            return "Food selected successfully!!!";
        } else {
            throw new CustomException("Item menu not found!!!");
        }
    }*/

    /*public String addFoodToCartForCompany(String vendorId, String menuId) {

        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new CustomException("Vendor not found!!!"));
        Company company = getAuthenticatedCompany();

        ItemMenu selectedItemMenu = findFoodMenuById(vendor.getId(), menuId);

        if (selectedItemMenu != null) {
            Order existingOpenOrder = orderRepository.findOpenOrderByCompany(company.getId());

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
                newOrder.setCompany(company);
                newOrder.setVendor(vendor);

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
            if (company.getOrderList() == null) {
                company.setOrderList(new ArrayList<>());
            }

            return "Food selected successfully!!!";
        } else {
            throw new CustomException("Item menu not found!!!");
        }
    }
     */


    /*
    public String addSupplementToCartForIndividual(String vendorId, String supplementId) {

        User user = getAuthenticatedUser();
        Company userCompany = user.getCompany();
        if(userCompany == null){
            throw new CustomException("User not found for this company");
        }

        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new CustomException("Vendor not found"));

        if (!userCompany.getVendors().contains(vendor)){
            throw new CustomException("Vendor not associated with this user's company");
        }

        Supplement selectedSupplement = supplementRepository.findByVendorIdAndAndSupplementId(vendor.getId(), supplementId);

        if (selectedSupplement == null) {
            throw new CustomException("Supplement not found!!!");
        }
        Order existingOpenOrder = orderRepository.findOpenOrderByUser(user.getId());

        if (existingOpenOrder != null) {
            // Check if the supplement is already in the cart
            Map<String, Integer> cartSupplements = existingOpenOrder.getSupplements();
            if (cartSupplements.containsKey(supplementId)) {
                int quantity = cartSupplements.get(supplementId);
                cartSupplements.put(supplementId, quantity + 1);
            } else {
                // If not in cart, add it with quantity 1
                cartSupplements.put(supplementId, 1);
            }
            // Update total amount based on supplement price
            BigDecimal totalAmount = existingOpenOrder.getTotalAmount().add(selectedSupplement.getSupplementPrice());
            existingOpenOrder.setTotalAmount(totalAmount);

            if(totalAmount.compareTo(userCompany.getPriceLimit()) > 0){
                throw new CustomException("You can not add items worth more than " + userCompany.getPriceLimit() + " to cart");
            }
            orderRepository.save(existingOpenOrder);
        } else {
            Order newOrder = new Order();
            newOrder.setUser(user);
            newOrder.setVendor(vendor);

            // Initialize cart with the selected supplement and quantity 1
            Map<String, Integer> cartSupplements = new HashMap<>();
            cartSupplements.put(supplementId, 1);
            newOrder.setSupplements(cartSupplements);

            // Set total amount to supplement price
            BigDecimal totalAmount = selectedSupplement.getSupplementPrice();
            newOrder.setTotalAmount(totalAmount);
            newOrder.setDeliveryStatus(DeliveryStatus.PENDING);
            newOrder.setSubmitStatus(SubmitStatus.PENDING);

            if(totalAmount.compareTo(userCompany.getPriceLimit()) > 0){
                throw new CustomException("You can not add items worth more than " + userCompany.getPriceLimit() + " to cart");
            }
            orderRepository.save(newOrder);
        }
        if (user.getOrderList() == null) {
            user.setOrderList(new ArrayList<>());
        }

        return "Supplement selected successfully!!!";
    }

     */

    /* public String addSupplementToCartForCompany (String vendorId, String supplementId) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new CustomException("Vendor not found!!!"));
        Company company = getAuthenticatedCompany();

        Supplement selectedSupplement = supplementRepository.findByVendorIdAndAndSupplementId(vendor.getId(), supplementId);

        if (selectedSupplement == null) {
            throw new CustomException("Supplement not found!!!");
        }
        Order existingOpenOrder = orderRepository.findOpenOrderByCompany(company.getId());

        if (existingOpenOrder != null) {
            // Check if the supplement is already in the cart
            Map<String, Integer> cartSupplements = existingOpenOrder.getSupplements();
            if (cartSupplements.containsKey(supplementId)) {
                int quantity = cartSupplements.get(supplementId);
                cartSupplements.put(supplementId, quantity + 1);
            } else {
                // If not in cart, add it with quantity 1
                cartSupplements.put(supplementId, 1);
            }

            // Update total amount based on supplement price
            BigDecimal totalAmount = existingOpenOrder.getTotalAmount().add(selectedSupplement.getSupplementPrice());
            existingOpenOrder.setTotalAmount(totalAmount);

            orderRepository.save(existingOpenOrder);
        } else {
            Order newOrder = new Order();
            newOrder.setCompany(company);
            newOrder.setVendor(vendor);

            // Initialize cart with the selected supplement and quantity 1
            Map<String, Integer> cartSupplements = new HashMap<>();
            cartSupplements.put(supplementId, 1);
            newOrder.setSupplements(cartSupplements);

            // Set total amount to supplement price
            BigDecimal totalAmount = selectedSupplement.getSupplementPrice();
            newOrder.setTotalAmount(totalAmount);
            newOrder.setDeliveryStatus(DeliveryStatus.PENDING);
            newOrder.setPaymentStatus(PaymentStatus.PENDING);

            orderRepository.save(newOrder);
        }
        if (company.getOrderList() == null) {
            company.setOrderList(new ArrayList<>());
        }

        return "Supplement selected successfully!!!";
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

    public UserOrderViewResponse viewSimplifiedOrdersByUser() {
        String userId = getAuthenticatedUser().getId();
        List<Order> userOrders = orderRepository.findOrdersByUserId(userId);
        List<SimplifiedOrderResponse> simplifiedOrders = viewSimplifiedOrdersInternal(userOrders);
        return new UserOrderViewResponse(simplifiedOrders, null);
    }

    public UserOrderViewResponse viewSimplifiedOrdersByCompany() {
        String companyId = getAuthenticatedCompany().getId();
        List<Order> userOrders = orderRepository.findOrdersByCompanyId(companyId);
        List<SimplifiedOrderResponse> simplifiedOrders = viewSimplifiedOrdersInternal(userOrders);
        return new UserOrderViewResponse(simplifiedOrders, null);
    }

    public OrderViewResponse viewUserCart() {
        String userId = getAuthenticatedUser().getId();
        List<Order> pendingOrders = orderRepository.findPendingOrdersByUserId(userId);
        return viewAllOrdersInternal(pendingOrders);
    }

   /* public OrderViewResponse viewCompanyCart() {
        String companyId = getAuthenticatedCompany().getId();
        List<Order> pendingOrders = orderRepository.findPendingOrdersByCompanyId(companyId);
        return viewAllOrdersInternal(pendingOrders);
    }

     public String deleteItem(String orderId, String foodItemId) {
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

    public OrderViewResponse deleteItem(String orderId, String foodItemId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException("Order not found!!!"));

        Map<String, Integer> cartItems = order.getItemMenus();

        if (cartItems.containsKey(foodItemId)) {
            int quantity = cartItems.get(foodItemId);

            if (quantity > 1) {
                cartItems.put(foodItemId, quantity - 1);
            } else {
                cartItems.remove(foodItemId);
            }
            order.setTotalAmount(calculateTotalAmount(order));
            orderRepository.save(order);

            return viewUserCart();
        } else {
            throw new CustomException("Food item not found in the cart!!!");
        }
    }

    public OrderViewResponse deleteSupplement (String orderId, String supplementId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException("Order not found!!!"));

        Map<String, Integer> supplements = order.getSupplements();

        if (supplements.containsKey(supplementId)) {
            int quantity = supplements.get(supplementId);

            if (quantity > 1) {
                supplements.put(supplementId, quantity - 1);
            } else {
                supplements.remove(supplementId);
            }
            order.setTotalAmount(calculateTotalAmount(order));
            orderRepository.save(order);

            return viewUserCart();
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

    private BigDecimal calculateTotalAmount(Order order) {
        BigDecimal totalAmount = BigDecimal.ZERO;

        Map<String, Integer> cartItems = order.getItemMenus();
        for (Map.Entry<String, Integer> entry : cartItems.entrySet()) {
            String itemId = entry.getKey();
            int quantity = entry.getValue();

            ItemMenu itemMenu = itemMenuRepository.findByItemId(itemId);

            BigDecimal itemTotal = itemMenu.getItemPrice().multiply(BigDecimal.valueOf(quantity));
            totalAmount = totalAmount.add(itemTotal);
        }

        Map<String, Integer> supplements = order.getSupplements();
        for (Map.Entry<String, Integer> entry : supplements.entrySet()) {
            String supplementId = entry.getKey();
            int quantity = entry.getValue();

            // Retrieve the Supplement object from your data source using supplementId
            Supplement supplement = supplementRepository.findById(supplementId)
                    .orElseThrow(() -> new CustomException("Supplement not found!!!"));

            // Calculate the total amount for this supplement based on its price and quantity
            BigDecimal supplementTotal = supplement.getSupplementPrice().multiply(BigDecimal.valueOf(quantity));
            totalAmount = totalAmount.add(supplementTotal);
        }

        return totalAmount;
    }

    /* private BigDecimal calculateTotalAmount(Order order) {
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
    }*/

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

    private OrderViewResponse viewAllOrdersInternal(List<Order> orderList) {
        List<OrderResponse> orderResponses = new ArrayList<>();
        int totalItems = 0;
        BigDecimal totalSum = BigDecimal.ZERO;

        for (Order order : orderList) {
            List<FoodDataResponse> foodDataResponses = new ArrayList<>();

            // Iterate through the cart items
            for (Map.Entry<String, Integer> entry : order.getItemMenus().entrySet()) {
                String itemId = entry.getKey();
                int quantity = entry.getValue();

                // Retrieve the ItemMenu object from your data source using itemId
                ItemMenu itemMenu = itemMenuRepository.findByItemId(itemId);

                totalItems += quantity;

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

            // Iterate through the cart supplements
            for (Map.Entry<String, Integer> entry : order.getSupplements().entrySet()) {
                String supplementId = entry.getKey();
                int quantity = entry.getValue();

                // Retrieve the Supplement object from your data source using supplementId
                Supplement supplement = supplementRepository.findById(supplementId)
                        .orElseThrow(() -> new CustomException("Supplement not found!!!"));

                totalItems += quantity;

                foodDataResponses.add(FoodDataResponse.builder()
                        .itemId(supplementId)
                        .itemName(supplement.getSupplementName())
                        .price(supplement.getSupplementPrice())
                        .quantity(quantity)
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
                .totalItems(totalItems)
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
                    order.getSubmitStatus()
            ));
        }
        return simplifiedOrderResponses;
    }

    private UserOrderDetailsResponse buildOrderResponse(Order order) {
        List<FoodDataResponse> foodDataResponses = new ArrayList<>();

        // Accumulate quantities for each item menu
        Map<String, Integer> accumulatedQuantities = new HashMap<>();

        // Iterate through the itemMenus map
        for (Map.Entry<String, Integer> entry : order.getItemMenus().entrySet()) {
            String itemId = entry.getKey();
            int quantity = entry.getValue();

            // Accumulate quantities for each item menu
            accumulatedQuantities.put(itemId, accumulatedQuantities.getOrDefault(itemId, 0) + quantity);
        }

        // Iterate through the accumulated quantities map
        for (Map.Entry<String, Integer> entry : accumulatedQuantities.entrySet()) {
            String itemId = entry.getKey();
            int quantity = entry.getValue();

            // Retrieve the ItemMenu object from your data source using itemId
            ItemMenu itemMenu = itemMenuRepository.findByItemId(itemId);
            Vendor vendor = itemMenu.getItemCategory().getVendor();

            // Create a FoodDataResponse object for each item menu
            foodDataResponses.add(FoodDataResponse.builder()
                    .itemId(itemId)
                    .itemName(itemMenu.getItemName())
                    .price(itemMenu.getItemPrice())
                    .quantity(quantity)
                    .imageUri(itemMenu.getImageUrl())
                    .vendorName(vendor.getBusinessName())
                    .build());
        }

        // Iterate through the supplements map
        for (Map.Entry<String, Integer> entry : order.getSupplements().entrySet()) {
            String supplementId = entry.getKey();
            int quantity = entry.getValue();

            // Retrieve the Supplement object from your data source using supplementId
            Supplement supplement = supplementRepository.findBySupplementId(supplementId);

            // Create a FoodDataResponse object for each supplement
            foodDataResponses.add(FoodDataResponse.builder()
                    .itemId(supplement.getSupplementId())
                    .itemName(supplement.getSupplementName())
                    .price(supplement.getSupplementPrice())
                    .quantity(quantity)
                    .imageUri(null)
                    .vendorName(supplement.getVendor().getBusinessName())
                    .build());
        }

        return UserOrderDetailsResponse.builder()
                .orderId(order.getOrderId())
                .itemMenu(foodDataResponses)
                .totalAmount(order.getTotalAmount())
                .submitStatus(order.getSubmitStatus())
                .deliveryStatus(order.getDeliveryStatus())
                .build();
    }

}