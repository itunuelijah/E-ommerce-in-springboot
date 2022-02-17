package com.phoenix.service.cart;

import com.phoenix.data.dto.CartRequestDto;
import com.phoenix.data.dto.CartResponseDto;
import com.phoenix.data.models.AppUser;
import com.phoenix.data.models.Cart;
import com.phoenix.data.models.Item;
import com.phoenix.data.models.Product;
import com.phoenix.data.repository.AppUserRepository;
import com.phoenix.data.repository.CartRepository;
import com.phoenix.data.repository.ProductRepository;
import com.phoenix.web.exceptions.BusinessLogicException;
import com.phoenix.web.exceptions.ProductDoesNotExistException;
import com.phoenix.web.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class CartServiceImpl implements CartService {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    AppUserRepository appUserRepository;

    @Autowired
    ProductRepository productRepository;

    @Override
    public CartResponseDto addItemToCart(CartRequestDto cartItemDto) throws UserNotFoundException, ProductDoesNotExistException, BusinessLogicException {

//        check if user exist
        Optional<AppUser> query = appUserRepository.
                findById(cartItemDto.getUserId());
        if (query.isEmpty()) {
            throw new UserNotFoundException("User with ID" + cartItemDto.getUserId() + "not found");
        }
        AppUser existingUser = query.get();

//        get user cart
        Cart myCart = existingUser.getMyCart();

//        check product exist

        Product product = productRepository.findById(13L).orElse(null);
        if (product == null) {
            throw new ProductDoesNotExistException("Product with ID " + cartItemDto.getProductId());
        }

        if (!quantityIsValid(product, cartItemDto.getQuantity())) {
            throw new BusinessLogicException("Quantity too large");
        }
//        add product to cart
        Item cartItem = new Item(product, cartItemDto.getQuantity());
        myCart.addItem(cartItem);
        myCart.setTotalPrice(myCart.getTotalPrice() + calculateItemTotalPrice(cartItem));
//        save cart
        cartRepository.save(myCart);

        return  buildCartResponse(myCart);
    }

    private Double calculateItemTotalPrice(Item item ){
        return item.getProduct().getPrice() * item.getQuantityAddedToCart();
    }
    private CartResponseDto buildCartResponse(Cart cart){
        return CartResponseDto.builder()
                .cartItems(cart.getItemList())
                .totalPrice(cart.getTotalPrice())
                .build();
    }
    private boolean quantityIsValid(Product product , int quantity){
        return product.getQuantity() >= quantity;
    }

    @Override
    public Cart viewCart() {
        return null;
    }
}

