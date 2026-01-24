public class TransactionRollbackTest {
    
    @Test
    void testOrderCreationRollsBackOnError() {
        // Simulate database error after stock deduction
        // Verify: stock is restored, order is NOT created
        
        Product product = createProduct(stock: 10);
        
        try {
            createOrderWithSimulatedError(product);
        } catch (DatabaseException e) {
            // Expected
        }
        
        // Stock should still be 10 (transaction rolled back)
        assertEquals(10, product.getStockQuantity());
        
        // Order should NOT exist
        assertFalse(orderExists());
    }
}
