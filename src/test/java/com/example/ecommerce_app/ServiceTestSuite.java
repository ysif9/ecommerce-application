package com.example.ecommerce_app;

import com.example.ecommerce_app.Services.*;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Test suite for all service tests.
 * This suite combines all service tests to run them as a group.
 */

@Suite
@SelectClasses({
        AuthServiceTest.class,
        CartServiceTest.class,
        OrderServiceTest.class,
        ProductServiceTest.class,
        UserServiceTest.class
})
public class ServiceTestSuite {
    // This class serves as a holder for the test suite configuration
}
