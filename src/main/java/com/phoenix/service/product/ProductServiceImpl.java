package com.phoenix.service.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.phoenix.data.dto.ProductDto;
import com.phoenix.data.models.Product;

import com.phoenix.data.repository.ProductRepository;
import com.phoenix.web.exceptions.BusinessLogicException;
import com.phoenix.web.exceptions.ProductDoesNotExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductRepository productRepository;

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product findProductById(Long productId) throws ProductDoesNotExistException {

        if (productId == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        Optional<Product> queryResult = productRepository.findById(productId);
        if (queryResult.isPresent()) {
            return queryResult.get();
        }

        throw new ProductDoesNotExistException("Product with ID:" + productId + ": does not exists");
    }

    @Override
    public Product createProduct(ProductDto productDto) throws BusinessLogicException {
//        PRODUCT DTO IS NOT NULL
        if (productDto == null) {
            throw new IllegalArgumentException("Argument cannot be null.");
        }

        Optional<Product> query = productRepository.findByName(productDto.getName());
        if(query.isPresent()){
            throw new BusinessLogicException("Product with name" + productDto.getName() + " already exist");
        }

            Product product = new Product();
            product.setName(productDto.getName());
            product.setPrice(productDto.getPrice());
            product.setQuantity(productDto.getQuantity());
            product.setDescription(productDto.getDescription());

            return productRepository.save(product);

        }

    private Product savedOrUpdate(Product product) throws BusinessLogicException {
        if(product == null){
         throw new BusinessLogicException("Product cannot be null");
        }
       return productRepository.save(product);
    }
    @Override
    public Product updateProductDetails(Long productId, JsonPatch productPatch) throws BusinessLogicException {
        Optional<Product> productQuery = productRepository.findById(productId);

        if(productQuery.isEmpty()){
           throw new BusinessLogicException("Product with ID" + productId + " Does not exist");
       }
       Product targetProduct = productQuery.get();
        try{
            targetProduct = applyPatchToProduct(productPatch, targetProduct);
            return savedOrUpdate(targetProduct);
        } catch (JsonPatchException | JsonProcessingException | BusinessLogicException je){
            throw new BusinessLogicException("Update Failed");
        }
    }

    private Product applyPatchToProduct(JsonPatch productPatch, Product targetProduct) throws JsonPatchException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode patched = productPatch.
                apply(objectMapper.convertValue(targetProduct, JsonNode.class));
        return  objectMapper.treeToValue(patched, Product.class);
    }
}

