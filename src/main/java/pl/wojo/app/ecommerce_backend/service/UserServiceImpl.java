package pl.wojo.app.ecommerce_backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import pl.wojo.app.ecommerce_backend.api_model.LoginBody;
import pl.wojo.app.ecommerce_backend.api_model.LoginResponse;
import pl.wojo.app.ecommerce_backend.api_model.RegistrationBody;
import pl.wojo.app.ecommerce_backend.exeption.EmailAlreadyExistsException;
import pl.wojo.app.ecommerce_backend.exeption.IncorrectCredentialsException;
import pl.wojo.app.ecommerce_backend.exeption.UsernameAlreadyExistsException;
import pl.wojo.app.ecommerce_backend.model.LocalUser;
import pl.wojo.app.ecommerce_backend.repository.LocalUserRepository;

@Service
public class UserServiceImpl implements UserService {
    private LocalUserRepository repository;
    private EncryptionService encryptionService;
    private JWTService jwtService;

    public UserServiceImpl(LocalUserRepository repository, 
        EncryptionService encryptionService,
        JWTService jwtService) {
        this.repository = repository;
        this.encryptionService = encryptionService;
        this.jwtService = jwtService;
    }

    @Override
    public LocalUser register(RegistrationBody registrationBody) {
        String email = registrationBody.getEmail();
        String username = registrationBody.getUsername();
        String password = registrationBody.getPassword();
        boolean emailExists = repository.existsByEmailIgnoreCase(email);
        boolean usernameExists = repository.existsByUsernameIgnoreCase(username);

        if(emailExists) {
            throw new EmailAlreadyExistsException(email);
        } else if(usernameExists) {
            throw new UsernameAlreadyExistsException(username);
        }

        // TODO: Encode password
        String encodedPassword = encryptionService.encode(password);
        registrationBody.setPassword(encodedPassword);

        LocalUser user = registrationBodyToLocalUser(registrationBody);
        
        return repository.save(user);
    }

    @Override
    public boolean checkEmailAlreadyExists(String email) {
        return repository.existsByEmailIgnoreCase(email);
    }

    @Override
    public boolean checkUsernameAlreadyExists(String username) {
        return repository.existsByUsernameIgnoreCase(username);
    }

    static LocalUser registrationBodyToLocalUser(RegistrationBody registrationBody) {
        return LocalUser.builder()
        .username(registrationBody.getUsername())
        //TODO: Encrypt password!
        .password(registrationBody.getPassword())
        .email(registrationBody.getEmail())
        .firstName(registrationBody.getFirstName())
        .lastName(registrationBody.getLastName())
        .build();
    }

    @Override
    public LoginResponse login(LoginBody loginBody, String jwtFromHeader) throws IncorrectCredentialsException {
        if(jwtFromHeader != null && jwtService.verifyJWT(jwtFromHeader)) {
            // nie sprawdzaj email i haslo, tylko wydaj nowy jwt który potrzebuje jedynie userId
            // zakładać możemy że email i haslo są nieobecne
            
            Optional<Long> opUser_id = repository.findUserIdByEmailIgnoreCase(loginBody.getEmail());
            if(opUser_id.isPresent()) {
                // utworz nowy jwt
                Long id = opUser_id.get();
                String newJwt = jwtService.generateJWT(id);

                return LoginResponse.builder()
                    .user_id(id)
                    .jwt(newJwt)
                    .build();
            } else {
                throw new IncorrectCredentialsException("JWT is not correct!");
            }
            // String newJwt = jwtService.generateJWT(null)
        } else {
            LocalUser user = null;
            String email = loginBody.getEmail();
            String rawPassword = loginBody.getPassword();  //raw password
            Optional<LocalUser> opUser = repository.findByEmailIgnoreCase(email);
            if(opUser.isPresent()) {
                if(encryptionService.verify(rawPassword, opUser.get().getPassword())) {
                    // password is correct, dołącz JWT do response
                    user = opUser.get();
                    String jwt = jwtService.generateJWT(user.getId());
                    LoginResponse loginResponse = new LoginResponse(user.getId(), jwt);
    
                    // dodajemy do SecurityCOntext
                    SecurityContext securityContext = SecurityContextHolder.getContext();
                    // przesyłamy 'null' bo jwt jest weryfikowany na każdym niepublicznym endpoincie i to on jest dowodem tożsamości
                    // securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(user, null));
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

                    System.out.println("USER: " + user + " został dodany do SecurityContext, " + securityContext.getAuthentication());


                    return loginResponse;
    
                } else {
                    throw new IncorrectCredentialsException("Credentials are not correct!");
                }
            } else {
                throw new IncorrectCredentialsException("Credentials are not correct");
            }
        }
    }
}
