package com.gym_report.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(1)
@Slf4j
public class TransactionIdFilter extends OncePerRequestFilter {

    private static final String TX_ID_HEADER = "X-Transaction-Id";
    private static final String MDC_KEY = "transactionId";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String txId = request.getHeader(TX_ID_HEADER);
        if (txId == null || txId.isBlank()) {
            txId = UUID.randomUUID().toString();
        }

        MDC.put(MDC_KEY, txId);
        response.setHeader(TX_ID_HEADER, txId);

        log.info("[{}] --> {} {}", txId, request.getMethod(), request.getRequestURI());
        try {
            filterChain.doFilter(request, response);
        } finally {
            log.info("[{}] <-- {} {}", txId, request.getMethod(), request.getRequestURI());
            MDC.remove(MDC_KEY);
        }
    }
}
