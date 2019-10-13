package com.haomibo.haomibo.security;

import com.haomibo.haomibo.config.Constants;
import com.haomibo.haomibo.models.db.SysUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;


@Component
public class JwtTokenUtil implements Serializable {


    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public Integer getUserIdFromToken(String token) {
        return this.getClaimFromToken(token, claims -> (Integer) claims.get("id"));
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(Constants.SIGNING_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(SysUser sysUser) {

        Claims claims = Jwts.claims().setSubject(String.valueOf(sysUser.getId()));

        claims.put("id", sysUser.getId());

        return Constants.TOKEN_PREFIX + Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + Constants.ACCESS_TOKEN_VALIDITY_SECONDS * 1000))
                .signWith(SignatureAlgorithm.HS256, Constants.SIGNING_KEY)
                .compact();
    }

    public Boolean validateToken(String token, SysUser sysUser) {
        final Integer sysUserIdFromToken = getUserIdFromToken(token);
        return (sysUserIdFromToken.equals(sysUser.getId()) && !isTokenExpired(token));
    }

}
