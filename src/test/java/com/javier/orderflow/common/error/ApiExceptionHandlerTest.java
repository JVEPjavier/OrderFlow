package com.javier.orderflow.common.error;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;

class ApiExceptionHandlerTest {
    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    void translatesBusinessConflictToProblemDetail() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/orders");
        ProblemDetail problem = handler.handleIllegalState(new IllegalStateException("Insufficient stock"), new ServletWebRequest(request));

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(problem.getTitle()).isEqualTo("Business rule conflict");
        assertThat(problem.getDetail()).isEqualTo("Insufficient stock");
        assertThat(problem.getInstance().getPath()).isEqualTo("/api/v1/orders");
        assertThat(problem.getProperties()).containsKey("timestamp");
    }
}
