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

            // No właśnie, zanim pobierzesz zdekodujesz jwt, pobierzesz user_id z niego i sprawdzisz czy istnieje w bazie...to 
            // sprawdź WCZEŚNIEJ jwtService.verify()!!! któro nam sprawdza czy ISSUER jest poprawny i co najważniejsze - sprawdza czy JWT.REQUIRE(ALGORITHM)!!!
            // sprawdz czy jwt jest z poprawnym ISSUER i signed ALGORITHM
            if(authHeader != null && jwtService.verifyJWT(authHeader)) {
                DecodedJWT decodedJWT = JWT.decode(cleanJWT);
                Long user_id = Long.parseLong(decodedJWT.getSubject());
                Optional<LocalUser> opUser = repository.findById(user_id);
                
                // sprawdź czy USER_ID zawarty w jwt istnieje w bazie danych
                // !!! nie sprawdzamy czy klient sprawdza swój jwt!
                // co jeśli doda do jwt user_id kogoś innego???????
                if(opUser.isPresent() && opUser.get().isEmailVerified()) {
                    LocalUser user = opUser.get();
                     
                    // pozwól na zalogowanie niejawne za pom. jwt gdy jest zweryfikowany (zawsze true ale w tescie sprawdzilismy tą opcje)
                    // dodajemy użytkownika do SecurityContext jako ten który ma dostęp.
    
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, 
                    null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
                    // System.out.println("111.");

                    //setting details
                    WebAuthenticationDetailsSource source = new WebAuthenticationDetailsSource();
                    WebAuthenticationDetails details = source.buildDetails(request);
                    authentication.setDetails(details);

                    SecurityContext securityContext = SecurityContextHolder.getContext();
                    securityContext.setAuthentication(authentication);
                    
                    filterChain.doFilter(request, response);
                    // System.out.println("222.");
                } else {
                    filterChain.doFilter(request, response);
                }
        }
        else
            filterChain.doFilter(request, response);
    }
}
