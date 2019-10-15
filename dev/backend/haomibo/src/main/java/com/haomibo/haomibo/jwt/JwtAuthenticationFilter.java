package com.haomibo.haomibo.jwt;

import com.haomibo.haomibo.config.Constants;
import com.haomibo.haomibo.models.db.ForbiddenToken;
import com.haomibo.haomibo.models.db.QForbiddenToken;
import com.haomibo.haomibo.repositories.ForbiddenTokenRepository;
import com.haomibo.haomibo.repositories.SysUserRepository;
import com.haomibo.haomibo.utils.Utils;
import com.querydsl.core.types.Predicate;
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
import java.util.Optional;


public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private SysUserRepository sysUserRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        Utils.getTokenString(request.getHeader(Constants.REQUEST_HEADER_AUTH_TOKEN_KEY)).ifPresent(token -> {

            String tokenStatus = jwtUtil.getTokenStatus(token);
            if (Constants.ResponseMessages.TOKEN_EXPIRED.equals(tokenStatus) || Constants.ResponseMessages.INVALID_TOKEN.equals(tokenStatus)) {
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

        filterChain.doFilter(request, response);
    }


}