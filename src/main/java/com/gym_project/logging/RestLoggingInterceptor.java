package com.gym_project.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Component
public class RestLoggingInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(RestLoggingInterceptor.class);
    private static final String START_TIME_ATTR = "requestStartTime";

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {

        request.setAttribute(START_TIME_ATTR, System.currentTimeMillis());

        String transactionId = MDC.get(TransactionIdFilter.TRANSACTION_ID_KEY);
        String query = request.getQueryString() != null
                ? "?" + request.getQueryString() : "";

        log.info("[{}] --> {} {}{}",
                transactionId,
                request.getMethod(),
                request.getRequestURI(),
                query);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) {

        long duration = getDuration(request);
        String transactionId = MDC.get(TransactionIdFilter.TRANSACTION_ID_KEY);

        log.info("[{}] <-- {} {} | status={} | {}ms",
                transactionId,
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus(),
                duration);
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {

        if (ex != null) {
            long duration = getDuration(request);
            String transactionId = MDC.get(TransactionIdFilter.TRANSACTION_ID_KEY);

            log.error("[{}] <-- {} {} | status={} | {}ms | error={}: {}",
                    transactionId,
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    duration,
                    ex.getClass().getSimpleName(),
                    ex.getMessage());
        }
    }

    private long getDuration(HttpServletRequest request) {
        Long start = (Long) request.getAttribute(START_TIME_ATTR);
        return start != null ? System.currentTimeMillis() - start : -1;
    }
}