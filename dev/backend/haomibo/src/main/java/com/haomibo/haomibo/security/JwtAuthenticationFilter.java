package com.haomibo.haomibo.security;

import com.haomibo.haomibo.config.Constants;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
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


public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

//    @Autowired
//    private UsuariosRepository usuariosRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader(Constants.HEADER_STRING);
        Integer usuarioCodigo = null;
        String authToken = null;

        //////////////////////

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(null, null, Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        chain.doFilter(req, res);

        //////////////////////

        /*
        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            authToken = header.replace(TOKEN_PREFIX, "");
            try {
                usuarioCodigo = jwtTokenUtil.getUsuarioCodigoFromToken(authToken);

            } catch (IllegalArgumentException e) {
                logger.error("an error occurred during getting username from token", e);
            } catch (ExpiredJwtException e) {
                logger.warn("the token is expired and not valid anymore", e);
            } catch (SignatureException e) {
                logger.error("Authentication Failed. Username or Password not valid.");
            }
        } else {
            logger.warn("couldn't find bearer string, will ignore the header");
        }
        if (usuarioCodigo != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            Usuarios usuarios = usuariosRepository.findByUsuarioCodigo(usuarioCodigo);

            if (jwtTokenUtil.validateToken(authToken, usuarios)) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(usuarios, null, Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        chain.doFilter(req, res);
        */
    }
}