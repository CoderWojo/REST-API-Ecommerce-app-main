package pl.wojo.app.ecommerce_backend.security;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pl.wojo.app.ecommerce_backend.model.LocalUser;
import pl.wojo.app.ecommerce_backend.repository.LocalUserRepository;
import pl.wojo.app.ecommerce_backend.service.JWTService;

@Component
public class JWTRequestFilter extends OncePerRequestFilter {

    private JWTService jwtService;
    private LocalUserRepository repository;

    public JWTRequestFilter(JWTService jwtService, LocalUserRepository repository) {
        this.jwtService = jwtService;
        this.repository = repository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        
        String cleanJWT;
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(authHeader != null && authHeader.startsWith("Bearer ")) 
            cleanJWT = authHeader.substring(7);
        else
            cleanJWT = authHeader;

            // sprawdzaj poprawność jwt tylko wtedy gdy user jest zweryfikowany
            if(authHeader != null && jwtService.verifyJWT(authHeader)) {    // jeśli poprawny jwt
                DecodedJWT decodedJWT = JWT.decode(cleanJWT);
                Long user_id = Long.parseLong(decodedJWT.getSubject());
                Optional<LocalUser> opUser = repository.findById(user_id);

                if(opUser.isPresent() && opUser.get().isEmailVerified()) {
                    LocalUser user = opUser.get();

                    if(opUser.isPresent()) 
                    {
                        user = opUser.get();
                        System.out.println("user to: " + user);
                    }    
                    // pozwól na zalogowanie niejawne za pom. jwt gdy jest zweryfikowany (zawsze true ale w tescie sprawdzilismy tą opcje)
                    // dodajemy użytkownika do SecurityContext jako ten który ma dostęp.
    
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, 
                    null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
                    System.out.println("111.");

                    //setting details
                    WebAuthenticationDetailsSource source = new WebAuthenticationDetailsSource();
                    WebAuthenticationDetails details = source.buildDetails(request);
                    authentication.setDetails(details);

                    SecurityContext securityContext = SecurityContextHolder.getContext();
                    securityContext.setAuthentication(authentication);
                    
                    filterChain.doFilter(request, response);
                    System.out.println("222.");
                } else {
                    filterChain.doFilter(request, response);
                }
        }
        else
            filterChain.doFilter(request, response);
    }
}
