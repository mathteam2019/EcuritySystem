package com.haomibo.haomibo.jwt;

import com.haomibo.haomibo.config.Constants;
import com.haomibo.haomibo.models.response.CommonResponseBody;
import com.haomibo.haomibo.repositories.SysUserRepository;
import com.haomibo.haomibo.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;


public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private SysUserRepository sysUserRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        AtomicReference<String> tokenStatus = new AtomicReference<>(Constants.ResponseMessages.OK);

        Utils.getTokenString(request.getHeader(Constants.REQUEST_HEADER_AUTH_TOKEN_KEY)).ifPresent(token -> {

            tokenStatus.set(jwtUtil.getTokenStatus(token));
            if (!Constants.ResponseMessages.OK.equals(tokenStatus.get())) {
                return;
            }

            jwtUtil.getIdFromToken(token).ifPresent(id -> {
                jwtUtil.getRoleFromToken(token).ifPresent(role -> {

                    if (Constants.Roles.SYS_USER.equals(role)) {
                        sysUserRepository.findById(id).ifPresent(sysUser -> {
                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(sysUser, null, Arrays.asList(new SimpleGrantedAuthority(Constants.Roles.SYS_USER)));
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                            SecurityContextHolder.getContext().setAuthentication(authentication);

                        });
                    }

                });
            });
        });

        if (Constants.ResponseMessages.OK.equals(tokenStatus.get())) {
            filterChain.doFilter(request, response);
        } else {
            try {
                response.getWriter().write(Utils.convertObjectToJson(new CommonResponseBody(tokenStatus.get())));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}