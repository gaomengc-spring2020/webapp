package com.mengchen.webapp.security;

import com.mengchen.webapp.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SecurityUtils {

    private static BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public static String encode(String plaintTextPassword) {
        return encoder.encode(plaintTextPassword);
    }

    public static boolean match(String plaintTextPassword, String encodedPassword) {
        return encoder.matches(plaintTextPassword, encodedPassword);
    }

    private static final String SECRET = "Th1sS3creT";
    private static final String HEADER = "Authorization";
    private static final String PREFIX = "Bearer ";

    public static String getAuthToken(User user) {
        String token = Jwts
                .builder()
                .setSubject("user:" + user.getId())
                .claim("email", user.getEmail())
                .claim("firstName", user.getFirstName())
                .claim("lastName", user.getLastName())
                .claim("id", user.getId())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 360000000))
                .signWith(SignatureAlgorithm.HS512,
                        SECRET.getBytes()).compact();

        return "Bearer " + token;
    }

    public static String getUserEmailFromToken(String token) {
        String jwtToken = token.replace(PREFIX, "");
        return Jwts.parser().setSigningKey(SECRET.getBytes()).parseClaimsJws(jwtToken).getBody().get("email").toString();
    }

    public static String getUserIdFromToken(String token) {
        String jwtToken = token.replace(PREFIX, "");
        return Jwts.parser().setSigningKey(SECRET.getBytes()).parseClaimsJws(jwtToken).getBody().get("id").toString();
    }

    public static boolean checkAuthTokenExist(HttpServletRequest request, HttpServletResponse res) {
        String authenticationHeader = request.getHeader(HEADER);
        if (authenticationHeader == null || !authenticationHeader.startsWith(PREFIX)) {
            return false;
        }
        return true;
    }

    public static Claims validateToken(HttpServletRequest request) {
        String jwtToken = request.getHeader(HEADER).replace(PREFIX, "");
        return Jwts.parser().setSigningKey(SECRET.getBytes()).parseClaimsJws(jwtToken).getBody();
    }

    public static void setUpSpringAuthentication(Claims claims) {
        //assume all user is with "user" level for now since we haven't design any
        //role based access control in this assignment
        List<GrantedAuthority> roles = new LinkedList<>();
        roles.add(new SimpleGrantedAuthority("user"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(claims.getSubject(),
                null,
                roles);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    public static boolean passwordPatternCorrect(String password) {
        Pattern p = Pattern.compile("(?=^.{8,30}$)((?=.*\\d)(?=.*[A-Z])(?=.*[a-z])|(?=.*\\d)(?=.*[^A-Za-z0-9])(?=.*[a-z])|(?=.*[^A-Za-z0-9])(?=.*[A-Z])(?=.*[a-z])|(?=.*\\d)(?=.*[A-Z])(?=.*[^A-Za-z0-9]))^.*");
        Matcher m = p.matcher(password);
        return m.matches();
    }
}
