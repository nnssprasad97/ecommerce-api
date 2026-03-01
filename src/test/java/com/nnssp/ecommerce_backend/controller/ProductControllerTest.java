package com.nnssp.ecommerce_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnssp.ecommerce_backend.dto.ProductRequest;
import com.nnssp.ecommerce_backend.entity.Product;
import com.nnssp.ecommerce_backend.service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void testGetAllProducts_Success() throws Exception {
        Product p1 = new Product();
        p1.setId(1L);
        p1.setName("Test Product");
        p1.setPrice(new BigDecimal("10.00"));
        p1.setStockQuantity(100);
        p1.setCategory("Electronics");

        Mockito.when(productService.getAllProducts("id", "asc")).thenReturn(Arrays.asList(p1));

        mockMvc.perform(get("/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Product"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateProduct_Success() throws Exception {
        Product p1 = new Product();
        p1.setId(1L);
        p1.setName("New Product");
        p1.setPrice(new BigDecimal("15.00"));
        p1.setStockQuantity(50);
        p1.setCategory("Books");

        ProductRequest request = new ProductRequest();
        request.setName("New Product");
        request.setPrice(new BigDecimal("15.00"));
        request.setStockQuantity(50);
        request.setCategory("Books");

        Mockito.when(productService.saveProduct(any(Product.class))).thenReturn(p1);

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Product"));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void testCreateProduct_ForbiddenForCustomer() throws Exception {
        ProductRequest request = new ProductRequest();
        request.setName("New Product");
        request.setPrice(new BigDecimal("15.00"));
        request.setStockQuantity(50);
        request.setCategory("Books");

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}
