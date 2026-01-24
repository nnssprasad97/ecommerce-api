package com.nnssp.ecommerce_backend;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RoleBasedAccessControlTest {
    
    @Test
    void testAdminCanCreateProduct() {
        // Admin should be able to create products
        assertTrue(adminUser.canCreateProduct());
    }
    
    @Test
    void testCustomerCannotCreateProduct() {
        // Customer should NOT be able to create products
        assertFalse(customerUser.canCreateProduct());
    }
    
    @Test
    void testCustomerCanViewProducts() {
        // Customer should be able to view products
        assertTrue(customerUser.canViewProducts());
    }
    
    @Test
    void testAdminCanDeleteProducts() {
        // Admin should be able to delete products
        assertTrue(adminUser.canDeleteProduct());
    }
    
    @Test
    void testCustomerCannotDeleteProducts() {
        // Customer should NOT be able to delete products
        assertFalse(customerUser.canDeleteProduct());
    }
    
    @Test
    void testCustomerCanPlaceOrders() {
        // Customer should be able to place orders
        assertTrue(customerUser.canPlaceOrder());
    }
    
    @Test
    void testAdminCannotPlaceOrders() {
        // Admin should NOT be able to place orders (only customers)
        assertFalse(adminUser.canPlaceOrder());
    }
}
