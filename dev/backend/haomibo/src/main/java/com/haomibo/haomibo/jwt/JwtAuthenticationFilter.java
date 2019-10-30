package com.haomibo.haomibo.jwt;

import com.haomibo.haomibo.config.Constants;
import com.haomibo.haomibo.enums.ResponseMessage;
import com.haomibo.haomibo.enums.Role;
import com.haomibo.haomibo.models.db.QForbiddenToken;
import com.haomibo.haomibo.models.db.QSysUser;
import com.haomibo.haomibo.models.db.SysResource;
import com.haomibo.haomibo.models.db.SysUser;
import com.haomibo.haomibo.repositories.ForbiddenTokenRepository;
import com.haomibo.haomibo.repositories.SysUserRepository;
import com.haomibo.haomibo.utils.Utils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class JwtAuthenticationFilter extends OncePerRequestFilter {


    @Autowired
    private Utils utils;

    @Autowired
    private SysUserRepository sysUserRepository;

    @Autowired
    ForbiddenTokenRepository forbiddenTokenRepository;

    @Autowired
    private PathMatcher pathMatcher;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        // get token from header
        String token = request.getHeader(Constants.REQUEST_HEADER_AUTH_TOKEN_KEY);

        if (token == null) {
            // if token does not exist, this token is invalid
            utils.writeResponse(response, ResponseMessage.INVALID_TOKEN);
            return;
        }

        // if token is forbidden, this token is invalid
        if (forbiddenTokenRepository.exists(QForbiddenToken.forbiddenToken.token.eq(token))) {
            utils.writeResponse(response, ResponseMessage.INVALID_TOKEN);
            return;
        }

        Claims claims;
        try {

            // try to get claims from token
            claims = Jwts.parser()
                    .setSigningKey(utils.jwtSecret)
                    .parseClaimsJws(token)
                    .getBody();

        } catch (ExpiredJwtException e) {
            // token expired
            utils.writeResponse(response, ResponseMessage.TOKEN_EXPIRED);
            return;
        } catch (Exception e) {
            // token is invalid
            utils.writeResponse(response, ResponseMessage.INVALID_TOKEN);
            return;
        }

        if (!claims.containsKey("userId")) {
            // if claims has not key "userId", this token is invalid
            utils.writeResponse(response, ResponseMessage.INVALID_TOKEN);
            return;
        }

        Long userId;

        try {
            // if conversion from String to Long fails, this token is invalid
            userId = Long.parseLong(claims.get("userId", String.class));
        } catch (Exception e) {
            e.printStackTrace();
            utils.writeResponse(response, ResponseMessage.INVALID_TOKEN);
            return;
        }

        Optional<SysUser> optionalSysUser = sysUserRepository.findOne(QSysUser.sysUser.userId.eq(userId));

        if (!optionalSysUser.isPresent()) {
            // if we can't get user from the token, this token is invalid
            utils.writeResponse(response, ResponseMessage.INVALID_TOKEN);
            return;
        }

        SysUser sysUser = optionalSysUser.get();

        // get all available resources for user
        List<SysResource> availableSysResourceList = new ArrayList<>();
        sysUser.getRoles().forEach(sysRole -> {
            availableSysResourceList.addAll(sysRole.getResources());
        });


        // generate roles for this user
        List<GrantedAuthority> roles = availableSysResourceList
                .stream()
                .map(sysResource -> Role.Authority.ROLE_PREFIX + sysResource.getResourceName())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());


        // generate authentication
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(sysUser, null, roles);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // set as authenticated
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // continue to next process
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        // we don't need to check the request which are inside EXCLUDE_URL_PATTERNS

        return Arrays.stream(Constants.EXCLUDE_URL_PATTERNS).anyMatch(p -> pathMatcher.match(p, request.getServletPath()));

    }
}