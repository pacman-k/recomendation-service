package com.crypto.investment.recommendationservice.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@Component
@Order(1)
public class BlacklistIPFilter implements Filter {
    private static Logger LOGGER = LogManager.getLogger(BlacklistIPFilter.class);

    @Value("${blacklist.ips}")
    private Set<String> ips;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        var httpServletResponse = (HttpServletResponse) response;
        if (ips.contains(request.getRemoteAddr())) {
            LOGGER.warn("Unauthorized access by ip from blacklist(" + request.getRemoteAddr() + ")");
            httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        } else {
            chain.doFilter(request, response);
        }
    }
}
