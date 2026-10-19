package com.javier.orderflow;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.Test;

class OrderFlowApplicationTests {
    @Test
    void mainEntryPointIsAvailable() {
        assertDoesNotThrow(() -> Class.forName("com.javier.orderflow.OrderFlowApplication"));
    }
}
