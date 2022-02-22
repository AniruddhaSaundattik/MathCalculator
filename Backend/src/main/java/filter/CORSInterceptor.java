package filter;

import config.CalcConfig;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(asyncSupported = true, urlPatterns = {"/*"})
public class CORSInterceptor implements Filter {
    public static final String ORIGIN = "origin";
    public static final String OPTIONS = "options";
    public static final String allowOrigin = "Access-Control-Allow-Origin";
    public static final String allowHeader = "Access-Control-Allow-Headers";
    public static final String allowMethods = "Access-Control-Allow-Methods";

    private final CalcConfig config;

    public CORSInterceptor(){
        super();
        this.config = new CalcConfig();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        String requestOrigin = request.getHeader(ORIGIN);
        if (isAllowedOrigin(requestOrigin)) {
            ((HttpServletResponse) servletResponse).addHeader(allowOrigin, requestOrigin);
            ((HttpServletResponse) servletResponse).addHeader(allowHeader, "*");
            ((HttpServletResponse) servletResponse).addHeader(allowMethods, "POST");

            HttpServletResponse resp = (HttpServletResponse) servletResponse;

            // CORS handshake (pre-flight request)
            if (request.getMethod().equals(OPTIONS)) {
                resp.setStatus(HttpServletResponse.SC_ACCEPTED);
                return;
            }
        }
        filterChain.doFilter(request, servletResponse);
    }

    private boolean isAllowedOrigin(String origin) {
        String[] allowedOrigins = config.getPropertyValue("client.whitelist").split(",");
        for (String allowedOrigin : allowedOrigins) {
            if (origin.equals(allowedOrigin)) return true;
        }
        return false;
    }
}
