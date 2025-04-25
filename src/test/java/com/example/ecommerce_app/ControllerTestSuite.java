package com.example.ecommerce_app;

import com.example.ecommerce_app.ControllerUnitTest.*;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Test suite for all controller tests.
 * This suite combines all controller tests to run them as a group.
 */
@Suite
@SelectClasses({
        CartControllerMVCTest.class,
        CartControllerTest.class,
        OrderControllerMVCTest.class,
        ProductControllerMVCTest.class,
        ProductControllerUnitTest.class,
        UserControllerTest.class
})
public class ControllerTestSuite {
    // This class serves as a holder for the test suite configuration
}
