/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（JwtAuthenticationEntryPoint）
 * 文件名：	JwtAuthenticationEntryPoint.java
 * 描述：	The filter class which will check all the requests.
 *          This will be added before UsernamePasswordAuthenticationFilter.
 * 作者名：	Sandy
 * 日期：	2019/10/25
 */


package com.nuctech.ecuritycheckitem.jwt;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.enums.Role;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.repositories.ForbiddenTokenRepository;
import com.nuctech.ecuritycheckitem.repositories.SerPlatformOtherParamRepository;
import com.nuctech.ecuritycheckitem.repositories.SysUserRepository;
import com.nuctech.ecuritycheckitem.repositories.SysUserSimpleRepository;
import com.nuctech.ecuritycheckitem.service.auth.AuthService;
import com.nuctech.ecuritycheckitem.service.permissionmanagement.UserService;
import com.nuctech.ecuritycheckitem.utils.Utils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * The filter class which will check all the requests.
 * This will be added before UsernamePasswordAuthenticationFilter.
 */
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private Utils utils;

    @Autowired
    private SysUserRepository sysUserRepository;

    @Autowired
    private SysUserSimpleRepository sysUserSimpleRepository;

    @Autowired
    ForbiddenTokenRepository forbiddenTokenRepository;

    @Autowired
    SerPlatformOtherParamRepository serPlatformOtherParamRepository;

    @Autowired
    private PathMatcher pathMatcher;

    @Autowired
    AuthService authService;


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

        Date startTime = new Date();
        // Get token from header.
        String token = request.getHeader(Constants.REQUEST_HEADER_AUTH_TOKEN_KEY);

        if (token == null) {
            // Ff token does not exist, this token is invalid.
            utils.writeResponse(response, ResponseMessage.INVALID_TOKEN);
            return;
        }

        // If token is forbidden, this token is invalid.
        Optional<ForbiddenToken> forbiddenTokenData = forbiddenTokenRepository.findOne(QForbiddenToken.forbiddenToken.token.eq(token));
        if (!forbiddenTokenData.isPresent()) {
            utils.writeResponse(response, ResponseMessage.INVALID_TOKEN);
            return;
        }
        ForbiddenToken forbiddenToken = forbiddenTokenData.get();

        Date endTime = new Date();
        long dif_fotbi_get = endTime.getTime() - startTime.getTime();

        Date curDate = new Date();
        long diffInMillies = Math.abs(curDate.getTime() - forbiddenToken.getEditedTime().getTime());
        long diff = diffInMillies / 1000;
        int jwt_validity_second;
        try {
            SerPlatformOtherParams serPlatformOtherParams = serPlatformOtherParamRepository
                    .findOne(QSerPlatformOtherParams.serPlatformOtherParams.id.isNotNull()).get();
            jwt_validity_second = serPlatformOtherParams.getOperatingTimeLimit() * 60;
        } catch(Exception ex) {
            jwt_validity_second = Constants.DEFAULT_JWT_VALIDITY_SECONDS;
        }
        Constants.token = forbiddenToken.getToken();


        if(jwt_validity_second > 0 && diff > jwt_validity_second) {
            forbiddenTokenRepository.delete(forbiddenToken);
            utils.writeResponse(response, ResponseMessage.TOKEN_EXPIRED);
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
        endTime = new Date();
        long dif_Claim = endTime.getTime() - startTime.getTime();

        // Find user from the database.
//        Optional<SysUser> optionalSysUser = sysUserRepository.findOne(QSysUser.sysUser.userId.eq(userId));
//
//        if (!optionalSysUser.isPresent()) {
//            // If we can't get user from the token, this token is invalid.
//            utils.writeResponse(response, ResponseMessage.INVALID_TOKEN);
//            return;
//        }
//        SysUser sysUser = optionalSysUser.get();
        forbiddenToken.setEditedTime(new Date());
        forbiddenTokenRepository.save(forbiddenToken);
        endTime = new Date();
        long dif_forbi_save = endTime.getTime() - startTime.getTime();



        // Get all available resources for user.
        List<SysResource> availableSysResourceList = new ArrayList<>();//authService.getAvailableSysResourceList(sysUser);

        // Generate roles for this user.
        List<GrantedAuthority> roles = availableSysResourceList
                .stream()
                .map(sysResource -> Role.Authority.ROLE_PREFIX + sysResource.getResourceName())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());


        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if(StringUtils.isEmpty(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }

        utils.ipAddress = ipAddress;


        // Generate authentication.
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId, null, roles);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // Set as authenticated.
        SecurityContextHolder.getContext().setAuthentication(authentication);
        endTime = new Date();
        long differennce = endTime.getTime() - startTime.getTime();
        //log.error("JWT time take " + differennce);
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
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if(StringUtils.isEmpty(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }

        utils.ipAddress = ipAddress;

        // We don't need to check the request which are inside EXCLUDE_URL_PATTERNS.
        return Arrays.stream(Constants.EXCLUDE_URL_PATTERNS).anyMatch(p -> pathMatcher.match(p, request.getServletPath()));

    }
}