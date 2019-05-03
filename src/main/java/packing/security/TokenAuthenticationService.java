package packing.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

import org.springframework.http.ResponseEntity;
import org.springframework.security
            .authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

import static java.util.Collections.emptyList;

class TokenAuthenticationService 
{
    static final long EXPIRATIONTIME = 1800000; //(30 Mins)   [864_000_000; // 10 days]
    static final String SECRET = "ThisIsASecret";
    static final String TOKEN_PREFIX = "Bearer";
    static final String HEADER_STRING = "Authorization";

    static void addAuthentication(HttpServletResponse res, String username) 
    {
        String JWT = Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
        res.addHeader(HEADER_STRING, TOKEN_PREFIX + " " + JWT);
    }

    static Authentication getAuthentication(HttpServletRequest request) 
    {
        String token = request.getHeader(HEADER_STRING);
        if (token != null) 
        {
        	/*
        	 * parse the token.
        	 */
            try 
            {
				String user = Jwts.parser()
				        .setSigningKey(SECRET)
				        .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
				        .getBody()
				        .getSubject();

				return  user != null ?
				        new UsernamePasswordAuthenticationToken(user, null, emptyList()) :
				        null;
			} 
            catch (ExpiredJwtException | MalformedJwtException | SignatureException | UnsupportedJwtException | IllegalArgumentException e) 
            {
				System.out.println("Your token has expired! Please login again");
		        System.out.println(e.getMessage());
		        return null;
		    }
        }
        return null;
    }
}