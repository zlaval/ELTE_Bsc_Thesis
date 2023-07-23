package com.zlrx.thesis.gradeservice.filters

import com.zlrx.thesis.gradeservice.config.authInfo
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.web.filter.OncePerRequestFilter

class LoggingFilter : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        MDC.put("tenantId", authInfo()?.tenantId)

        filterChain.doFilter(request, response)

        MDC.remove("tenantId")
    }
}
