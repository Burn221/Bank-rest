package com.example.bankcards.security.jwt;

import com.example.bankcards.dto.JwtDTO.JwtAuthDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.exception.exceptions.DisabledException;
import com.example.bankcards.exception.exceptions.NotFoundException;
import com.example.bankcards.repository.UserRepository;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtService {

    @Autowired
    private UserRepository repository;

    private static final Logger LOGGER= LogManager.getLogger(JwtService.class);

    @Value("580b56f78bf8a923c94180d283bec4b4cfe8e498316a498683e518b469b3433a")
    private String jwtSecret;

    public JwtAuthDto generateAuthToken(String username){


        User user= repository.findByUsername(username)
                        .orElseThrow(()-> new NotFoundException("User not found"));
        if (!user.isEnabled()) throw new DisabledException("User is disabled");

        JwtAuthDto jwtDto= new JwtAuthDto();

        jwtDto.setToken(generateJwtToken(user));
        jwtDto.setRefreshToken(generateRefreshToken(user));

        return jwtDto;


    }

    public JwtAuthDto refreshBaseToken(String username, String refreshToken){
        User user= repository.findByUsername(username)
                .orElseThrow(()-> new NotFoundException("User not found"));
        if (!user.isEnabled()) throw new DisabledException("User is disabled");

        JwtAuthDto jwtDto= new JwtAuthDto();
        jwtDto.setToken(generateJwtToken(user));
        jwtDto.setRefreshToken(refreshToken);

        return jwtDto;


    }


    public String getUsernameFromToken(String token){
        Claims claims= Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    public Long getUserIdFromToken(String token){
        Number num= Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("uid", Number.class);

        if(num==null) return null;

        return num.longValue();
    }

    public Role getRoleFromToken(String token){
        Claims claims = Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        String roleStr= claims.get("role", String.class);

        return Role.valueOf(roleStr);


    }


    public boolean validateJwtToken(String token){
        try{
            Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return true;
        }
        catch (ExpiredJwtException e){
            LOGGER.error("Expired jwt exception ", e);
        }
        catch (UnsupportedJwtException e){
            LOGGER.error("Unsupported jwt exception ",e );
        }
        catch (MalformedJwtException e){
            LOGGER.error("malformed jwt exception ",e);
        }
        catch (SecurityException e){
            LOGGER.error("Security exception ", e);
        }
        catch (Exception e){
            LOGGER.error("Invalid token ", e);
        }
        return false;
    }


    private String generateJwtToken(User user){
        Date date= Date.from(LocalDateTime.now().plusHours(1).atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("uid", user.getId())
                .claim("role", user.getRole().name())
                .setExpiration(date)
                .signWith(getSignInKey())
                .compact();

    }

    private SecretKey getSignInKey(){
        byte[] keyBytes= Decoders.BASE64.decode(jwtSecret);

        return Keys.hmacShaKeyFor(keyBytes);

    }

    private String generateRefreshToken(User user){
        Date date= Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("uid", user.getId())
                .claim("roles", user.getRole())
                .setExpiration(date)
                .signWith(getSignInKey())
                .compact();

    }




}
