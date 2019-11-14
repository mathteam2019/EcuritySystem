package com.nuctech.ecuritycheckitem.jwt;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.enums.Role;
import com.nuctech.ecuritycheckitem.models.db.QForbiddenToken;
import com.nuctech.ecuritycheckitem.models.db.QSysUser;
import com.nuctech.ecuritycheckitem.models.db.SysResource;
import com.nuctech.ecuritycheckitem.models.db.SysUser;
import com.nuctech.ecuritycheckitem.repositories.ForbiddenTokenRepository;
import com.nuctech.ecuritycheckitem.repositories.SysUserRepository;
import com.nuctech.ecuritycheckitem.utils.Utils;
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

/**
 * The filter class which will check all the requests.
 * This will be added before UsernamePasswordAuthenticationFilter.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    @Autowired
    private Utils utils;

    @Autowired
    private SysUserRepository sysUserRepository;

    @Autowired
    ForbiddenTokenRepository forbiddenTokenRepository;

    @Autowired
    private PathMatcher pathMatcher;


    /**
     * Overridden method for filtering request.
     *
     * @param request     Request object.
     * @param response    Response object.
     * @param filterChain FilterChain object.
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        // Get token from header.
        String token = request.getHeader(Constants.REQUEST_HEADER_AUTH_TOKEN_KEY);

        if (token == null) {
            // Ff token does not exist, this token is invalid.
            utils.writeResponse(response, ResponseMessage.INVALID_TOKEN);
            return;
        }

        // If token is forbidden, this token is invalid.
        if (forbiddenTokenRepository.exists(QForbiddenToken.forbiddenToken.token.eq(token))) {
            utils.writeResponse(response, ResponseMessage.INVALID_TOKEN);
            return;
        }

        Claims claims;
        try {

            // Try to get claims from token.
            claims = Jwts.parser()
                    .setSigningKey(utils.jwtSecret)
                    .parseClaimsJws(token)
                    .getBody();

        } catch (ExpiredJwtException e) {
            // Token expired.
            utils.writeResponse(response, ResponseMessage.TOKEN_EXPIRED);
            return;
        } catch (Exception e) {
            // Token is invalid.
            utils.writeResponse(response, ResponseMessage.INVALID_TOKEN);
            return;
        }

        if (!claims.containsKey("userId")) {
            // If claims have not key "userId", this token is invalid.
            utils.writeResponse(response, ResponseMessage.INVALID_TOKEN);
            return;
        }

        Long userId;

        try {
            // If conversion from String to Long fails, this token is invalid.
            userId = Long.parseLong(claims.get("userId", String.class));
        } catch (Exception e) {
            e.printStackTrace();
            utils.writeResponse(response, ResponseMessage.INVALID_TOKEN);
            return;
        }

        // Find user from the database.
        Optional<SysUser> optionalSysUser = sysUserRepository.findOne(QSysUser.sysUser.userId.eq(userId));

        if (!optionalSysUser.isPresent()) {
            // If we can't get user from the token, this token is invalid.
            utils.writeResponse(response, ResponseMessage.INVALID_TOKEN);
            return;
        }

        SysUser sysUser = optionalSysUser.get();

        // Get all available resources for user.
        List<SysResource> availableSysResourceList = new ArrayList<>();
        sysUser.getRoles().forEach(sysRole -> {
            availableSysResourceList.addAll(sysRole.getResources());
        });


        // Generate roles for this user.
        List<GrantedAuthority> roles = availableSysResourceList
                .stream()
                .map(sysResource -> Role.Authority.ROLE_PREFIX + sysResource.getResourceName())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());


        // Generate authentication.
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(sysUser, null, roles);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // Set as authenticated.
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Continue to next filter.
        filterChain.doFilter(request, response);
    }

    /**
     * Overridden method for excluding urls which don't need token checking
     *
     * @param request
     * @return
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        // We don't need to check the request which are inside EXCLUDE_URL_PATTERNS.
        return Arrays.stream(Constants.EXCLUDE_URL_PATTERNS).anyMatch(p -> pathMatcher.match(p, request.getServletPath()));

    }
}