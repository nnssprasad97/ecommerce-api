package com.nnssp.ecommerce_backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@org.springframework.security.test.context.support.WithMockUser(roles = { "ADMIN", "CUSTOMER" })
class ECommerceApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
