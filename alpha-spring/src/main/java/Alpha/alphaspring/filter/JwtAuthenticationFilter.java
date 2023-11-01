package Alpha.alphaspring.filter;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
    final String AUTH_HEADER_NAME = "Authorization";
    final String BEARER_NAME = "Bearer";
    private final AuthenticationManager authenticationManager;
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        System.out.println("Enter Jwt filter");
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String authHeader = request.getHeader(AUTH_HEADER_NAME);
        if (authHeader == null || authHeader.isEmpty()) {
            throw new AuthenticationCredentialsNotFoundException("Auth header does not exists");
        }

        String[] authHeaderValue = authHeader.split(" ");
        if (authHeaderValue.length != 2){
            throw new AuthenticationCredentialsNotFoundException("invalid auth header, require token");
        }
        if(!BEARER_NAME.equals(authHeaderValue[0])) {
            throw new BadCredentialsException("invalid auth header, require bearer");
        }
        String jwtToken = authHeaderValue[1];

        Authentication authToken = this.authenticationManager.authenticate(new JwtAuthenticationToken(jwtToken));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        chain.doFilter(request, response);
    }
}
