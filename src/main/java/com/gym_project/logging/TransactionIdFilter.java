package com.gym_project.logging;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;


@Component("transactionIdFilter")
public class TransactionIdFilter extends OncePerRequestFilter {

    public static final String TRANSACTION_ID_KEY    = "transactionId";
    public static final String TRANSACTION_ID_HEADER = "X-Transaction-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String transactionId = request.getHeader(TRANSACTION_ID_HEADER);
        if (transactionId == null || transactionId.isBlank()) {
            transactionId = UUID.randomUUID().toString();
        }

        MDC.put(TRANSACTION_ID_KEY, transactionId);

        response.setHeader(TRANSACTION_ID_HEADER, transactionId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(TRANSACTION_ID_KEY);
        }
    }
}