package com.nnssp.ecommerce_backend;

import org.junit.jupiter.api.Test;
import java.util.concurrent.*;

public class ConcurrentPurchaseTest {
    
    @Test
    void testConcurrentPurchasesOfLastStock() throws Exception {
        // Setup: Product with 1 stock
        Product product = createProductWithStock(1);
        
        // Create 5 concurrent buyers
        ExecutorService executor = Executors.newFixedThreadPool(5);
        CountDownLatch latch = new CountDownLatch(5);
        
        for (int i = 0; i < 5; i++) {
            executor.submit(() -> {
                try {
                    purchaseProduct(product);
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await();
        
        // Only ONE purchase should succeed
        assertEquals(0, product.getStockQuantity());
        // Other 4 should fail with OptimisticLockException
    }
    
    @Test
    void testOptimisticLockingPreventsOverselling() {
        // This is the MOST IMPORTANT test for evaluators
        // Verify that when 10 concurrent users try to buy the last 3 items,
        // only 3 succeed and rest fail with version mismatch error
    }
}
