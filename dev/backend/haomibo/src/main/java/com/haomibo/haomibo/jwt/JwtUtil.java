package com.haomibo.haomibo.jwt;

import com.haomibo.haomibo.config.Constants;
import com.haomibo.haomibo.models.db.QForbiddenToken;
import com.haomibo.haomibo.models.db.SysUser;
import com.haomibo.haomibo.repositories.ForbiddenTokenRepository;
import com.haomibo.haomibo.repositories.SysUserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;


@Component
public class JwtUtil implements Serializable {


    @Value("${jwt.secret}")
    private String secret;


    @Autowired
    ForbiddenTokenRepository forbiddenTokenRepository;

    @Autowired
    SysUserRepository sysUserRepository;

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateTokenForSysUser(SysUser sysUser) {

        Claims claims = Jwts.claims().setSubject(String.valueOf(sysUser.getId()));

        claims.put("id", sysUser.getId());
        claims.put("role", Constants.Roles.SYS_USER);

        return Constants.TOKEN_PREFIX + Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + Constants.JWT_VALIDITY_SECONDS * 1000))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public Optional<String> getRoleFromToken(String token) {
        try {
            return Optional.of(this.getClaimFromToken(token, claims -> (String) claims.get("role")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<Integer> getIdFromToken(String token) {
        try {
            return Optional.of(this.getClaimFromToken(token, claims -> (Integer) claims.get("id")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public String getTokenStatus(String token) {

        try {
            getAllClaimsFromToken(token);
        } catch (ExpiredJwtException e) {
            return Constants.ResponseMessages.TOKEN_EXPIRED;
        } catch (Exception e) {
            return Constants.ResponseMessages.INVALID_TOKEN;
        }

        final Date expiration = getExpirationDateFromToken(token);
        if (expiration.before(new Date())) {
            return Constants.ResponseMessages.TOKEN_EXPIRED;
        }

        QForbiddenToken qForbiddenToken = QForbiddenToken.forbiddenToken;
        boolean isForbidden = forbiddenTokenRepository.count(qForbiddenToken.token.eq(token)) > 0;

        if (isForbidden) {
            return Constants.ResponseMessages.INVALID_TOKEN;
        }

        return Constants.ResponseMessages.OK;
    }
}
