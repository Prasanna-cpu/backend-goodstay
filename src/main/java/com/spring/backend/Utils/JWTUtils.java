package com.spring.backend.Utils;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;


@Service
public class JWTUtils {


    private static final long EXPIRATION_TIME=1000*60*24*7;

    private final SecretKey Key;

    public JWTUtils(){
        String secretString=Secrets.JWT_SECRET;
        byte[] keyBytes= Base64.getDecoder().decode(secretString.getBytes(StandardCharsets.UTF_8));
        this.Key=new SecretKeySpec(keyBytes,"HmacSHA256");
    }

    public String generateToken(UserDetails userDetails){
        return Jwts.builder()
               .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+EXPIRATION_TIME))
                .signWith(Key)
                .compact();
    }

    private <T> T extractClaims(String token, Function<Claims,T> claimsTFunction){
        return claimsTFunction.apply(Jwts.parser().verifyWith(Key).build().parseSignedClaims(token).getPayload());
    }

    private Boolean isTokenExpired(String token){
        return extractClaims(token,Claims::getExpiration).before(new Date());
    }

    public String getUserName(String token){
        return extractClaims(token,Claims::getSubject);
    }


    public Boolean isValidToken(String token,UserDetails userDetails){
        final String username=getUserName(token);
        return (
                username.equals(userDetails.getUsername()) && !isTokenExpired(token)
                );
    }


}
