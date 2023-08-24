package com.example.foodapp.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.foodapp.payloads.request.CategoryRequest;
import com.example.foodapp.payloads.response.CategoryResponse;
import com.example.foodapp.payloads.response.ItemMenuResponse;
import com.example.foodapp.entities.*;
import com.example.foodapp.exception.CustomException;
import com.example.foodapp.repository.ItemCategoryRepository;
import com.example.foodapp.repository.ItemMenuRepository;
import com.example.foodapp.repository.VendorRepository;
import com.example.foodapp.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final VendorRepository vendorRepository;
    private final ItemCategoryRepository itemCategoryRepository;
    private final ItemMenuRepository itemMenuRepository;
    private final Cloudinary cloudinary;

    public CategoryResponse addItemCategory(CategoryRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String vendorEmail = authentication.getName();
        Vendor vendor = vendorRepository.findByEmail(vendorEmail);
        ItemCategory itemCategory = mapItemCategoryRequestToEntity(request);
        itemCategory.setVendor(vendor);
        vendor.getItemCategory().add(itemCategory);
        vendorRepository.save(vendor);
        return CategoryResponse.builder()
                .categoryId(itemCategory.getCategoryId())
                .categoryName(itemCategory.getCategoryName())
                .itemMenus(itemCategory.getItemMenus())
                .build();
    }
    public ItemMenuResponse addItemMenu(String itemName, BigDecimal itemPrice, String categoryId,
                                        MultipartFile file) throws IOException {

        Vendor vendor = getAuthenticatedVendor();
        ItemCategory itemCategory = itemCategoryRepository.findByVendorIdAndCategoryId(vendor.getId(), categoryId)
                .orElseThrow(() -> new CustomException("Food category not found for the vendor"));
        ItemMenu itemMenu = new ItemMenu();

        if (file != null && !file.isEmpty()) {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "public_id", itemName,
                            "folder", "images",
                            "overwrite", true,
                            "resource_type", "auto"
                    ));
            String imageUrl = uploadResult.get("secure_url").toString();

            itemMenu.setItemName(itemName);
            itemMenu.setItemPrice(itemPrice);
            itemMenu.setImageUrl(imageUrl);
            itemMenu.setItemCategory(itemCategory);

            itemCategory.getItemMenus().add(itemMenu);
            itemCategoryRepository.save(itemCategory);

            return ItemMenuResponse.builder()
                    .itemName(itemMenu.getItemName())
                    .itemPrice(itemMenu.getItemPrice())
                    .imageUrl(imageUrl)
                    .categoryName(itemCategory.getCategoryName())
                    .build();
        } else {
            throw new CustomException("Image file is required");
        }
    }

    public ItemMenuResponse editItemMenu(String itemId, String itemName, BigDecimal itemPrice, Boolean breakfast, Boolean lunch, Boolean dinner, String categoryId, MultipartFile file) throws IOException {

        Vendor vendor = getAuthenticatedVendor();

        ItemMenu itemMenu = itemMenuRepository.findByItemIdAndVendorId(itemId, vendor.getId())
                .orElseThrow(() -> new CustomException("Item not found for the vendor"));

        ItemCategory itemCategory = itemCategoryRepository.findByVendorIdAndCategoryId(vendor.getId(), categoryId)
                .orElseThrow(() -> new CustomException("Food category not found for the vendor"));

        itemMenu.setItemName(itemName);
        itemMenu.setItemPrice(itemPrice);

        if (file != null && !file.isEmpty()) {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "public_id", itemName,
                            "folder", "images",
                            "overwrite", true,
                            "resource_type", "auto"
                    ));
            String imageUrl = uploadResult.get("secure_url").toString();
            itemMenu.setImageUrl(imageUrl);
        }

        itemMenu.setItemCategory(itemCategory);

        itemMenuRepository.save(itemMenu);

        return ItemMenuResponse.builder()
                .itemName(itemMenu.getItemName())
                .itemPrice(itemMenu.getItemPrice())
                .imageUrl(itemMenu.getImageUrl())
                .categoryName(itemCategory.getCategoryName())
                .build();
    }

    public CategoryResponse editItemCategory(String categoryId, CategoryRequest request) {
        Vendor vendor = getAuthenticatedVendor();
        ItemCategory itemCategory = itemCategoryRepository.findByVendorIdAndCategoryId(vendor.getId(), categoryId)
                .orElseThrow(() -> new CustomException("Food category not found for the vendor"));

        itemCategory.setCategoryName(request.getCategoryName());
        itemCategoryRepository.save(itemCategory);

        return CategoryResponse.builder()
                .categoryId(itemCategory.getCategoryId())
                .categoryName(itemCategory.getCategoryName())
                .itemMenus(itemCategory.getItemMenus())
                .build();
    }

    public CategoryResponse getItemCategory(String categoryId) {
        Vendor vendor = getAuthenticatedVendor();
        ItemCategory itemCategory = itemCategoryRepository.findByVendorIdAndCategoryId(vendor.getId(), categoryId)
                .orElseThrow(() -> new CustomException("Food category not found for the vendor"));

        return CategoryResponse.builder()
                .categoryId(itemCategory.getCategoryId())
                .categoryName(itemCategory.getCategoryName())
                .itemMenus(itemCategory.getItemMenus())
                .build();
    }

    public void deleteItemMenu(String itemId, String categoryId) {
        Vendor vendor = getAuthenticatedVendor();
        ItemCategory itemCategory = itemCategoryRepository.findByVendorIdAndCategoryId(vendor.getId(), categoryId)
                .orElseThrow(() -> new CustomException("Food category not found for the vendor"));

        ItemMenu itemMenu = itemCategory.getItemMenus().stream()
                .filter(menu -> menu.getItemId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new CustomException("Item not found in the category"));

        itemCategory.getItemMenus().remove(itemMenu);
        itemCategoryRepository.save(itemCategory);

        itemMenu.setItemCategory(null);
        itemMenuRepository.delete(itemMenu);
    }

    public List<CategoryResponse> getAllItemCategory() {
        Vendor vendor = getAuthenticatedVendor();
        List<ItemCategory> foodCategories = itemCategoryRepository.findByVendorId(vendor.getId());

        return foodCategories.stream()
                .map(foodCategory -> CategoryResponse.builder()
                        .categoryId(foodCategory.getCategoryId())
                        .categoryName(foodCategory.getCategoryName())
                        .itemMenus(foodCategory.getItemMenus())
                        .build())
                .collect(Collectors.toList());
    }

    public void deleteItemCategory(String categoryId) {
        Vendor vendor = getAuthenticatedVendor();
        ItemCategory itemCategory = itemCategoryRepository.findByVendorIdAndCategoryId(vendor.getId(), categoryId)
                .orElseThrow(() -> new CustomException("Food category not found for the vendor"));

        itemCategoryRepository.delete(itemCategory);
    }

    public ItemMenu getFile(String id) {
        return itemMenuRepository.findByItemId(id);
    }

    // Other methods **************************************************************

    private ItemCategory mapItemCategoryRequestToEntity(CategoryRequest categoryRequest) {
        ItemCategory itemCategory = new ItemCategory();
        itemCategory.setCategoryName(categoryRequest.getCategoryName());
        return itemCategory;
    }

    private Vendor getAuthenticatedVendor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String vendorEmail = authentication.getName();
        Vendor vendor = vendorRepository.findByEmail(vendorEmail);
        if (vendor == null) {
            throw new CustomException("Vendor not found");
        }
        return vendor;
    }
}
