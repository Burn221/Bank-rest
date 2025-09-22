package com.example.bankcards.security.jwt;

import com.example.bankcards.dto.userdto.AuthUser;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.security.CustomUserDetailsService;
import com.example.bankcards.service.Impl.UserServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;


@Component
@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private JwtService jwtService;
    private CustomUserDetailsService service;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {


        String token= getTokenFromRequest(request);
        if(token != null && jwtService.validateJwtToken(token)){
            setCustomUserDetailsToSecurityContextHolder(request,token);
        }

        filterChain.doFilter(request,response);


    }


    private void setCustomUserDetailsToSecurityContextHolder(HttpServletRequest request,String token) {

        Long userId= jwtService.getUserIdFromToken(token);
        String username= jwtService.getUsernameFromToken(token);
        Role role= jwtService.getRoleFromToken(token);

        var authorities= List.of(new SimpleGrantedAuthority("ROLE_"+role.name()));
        var principal= new AuthUser(userId,username,authorities);

        var auth= new UsernamePasswordAuthenticationToken(principal,null,authorities);
        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(auth);

    }

    private String getTokenFromRequest(HttpServletRequest request){
        String bearerToken= request.getHeader(HttpHeaders.AUTHORIZATION);
        if(bearerToken!=null && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }
}
