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
        // sprawdz czy user wysyła w żądaniu nagłówek Authorization z tokenem JWT
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        System.out.println("TUUUUUUU: " + authHeader);
        // TODO: po udanym /login, niech user(Authentication) zostanie dodany do SecurityCOntext
        if(authHeader != null && jwtService.verifyJWT(authHeader)) {
            // dodajemy użytkownika do SecurityContext jako ten który ma dostęp.
            String cleanJWT;
            if(authHeader.startsWith("Bearer ")) 
                cleanJWT = authHeader.substring(7);
            else
                cleanJWT = authHeader;

            DecodedJWT decodedJWT = JWT.decode(cleanJWT);
            Long user_id = Long.parseLong(decodedJWT.getSubject());

            System.out.println("user_id=====" + user_id);
            Optional<LocalUser> opUser = repository.findById(user_id);
            LocalUser user = null;
            
            List<LocalUser> all = repository.findAll();
            System.out.println("All======" + all);

            System.out.println("User ====" + user);
            if(opUser.isPresent()) {
                user = opUser.get();
            }
            // TODO: do /profile zmien na tworzenie 'authorities', dodaj pole w LocalUser LIST<GRATUEDROLES>'ROLE' i tu podłącz i dodaj w bazie danych
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
            //setting details
            WebAuthenticationDetailsSource source = new WebAuthenticationDetailsSource();
            WebAuthenticationDetails details = source.buildDetails(request);
            authentication.setDetails(details);

            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(authentication);

            System.out.println("SecurityContext, authentication: " + securityContext.getAuthentication());
            
            filterChain.doFilter(request, response);
        } else {
            // nie dodajemy
            filterChain.doFilter(request, response);
        }
    }
    
}
