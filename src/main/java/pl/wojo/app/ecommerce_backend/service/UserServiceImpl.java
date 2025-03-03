package pl.wojo.app.ecommerce_backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.mail.MailSendException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import pl.wojo.app.ecommerce_backend.api_model.LoginBody;
import pl.wojo.app.ecommerce_backend.api_model.LoginResponse;
import pl.wojo.app.ecommerce_backend.api_model.RegistrationBody;
import pl.wojo.app.ecommerce_backend.exeption.EmailAlreadyExistsException;
import pl.wojo.app.ecommerce_backend.exeption.IncorrectCredentialsException;
import pl.wojo.app.ecommerce_backend.exeption.TokenNotFoundException;
import pl.wojo.app.ecommerce_backend.exeption.UserAlreadyVerifiedException;
import pl.wojo.app.ecommerce_backend.exeption.UserNotVerifiedException;
import pl.wojo.app.ecommerce_backend.exeption.UsernameAlreadyExistsException;
import pl.wojo.app.ecommerce_backend.model.LocalUser;
import pl.wojo.app.ecommerce_backend.model.VerificationToken;
import pl.wojo.app.ecommerce_backend.repository.LocalUserRepository;
import pl.wojo.app.ecommerce_backend.repository.VerificationTokenRepository;

@Service
public class UserServiceImpl implements UserService {
    private LocalUserRepository userRepository;
    private EncryptionService encryptionService;
    private JWTService jwtService;
    private EmailService emailService;
    private VerificationTokenRepository tokenRepository;
    private VerificationService verificationService;

    public UserServiceImpl(LocalUserRepository userRepository, 
        EncryptionService encryptionService,
        JWTService jwtService,
        EmailService emailService,
        VerificationTokenRepository tokenRepository,
        VerificationService verificationService) {
        this.userRepository = userRepository;
        this.encryptionService = encryptionService;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.tokenRepository = tokenRepository;
        this.verificationService = verificationService;
    }

    @Override
    public LocalUser register(RegistrationBody registrationBody) throws MessagingException, MailSendException {
        String email = registrationBody.getEmail();
        String username = registrationBody.getUsername();
        String password = registrationBody.getPassword();
        boolean emailExists = userRepository.existsByEmailIgnoreCase(email);
        boolean usernameExists = userRepository.existsByUsernameIgnoreCase(username);

        if(emailExists) {
            throw new EmailAlreadyExistsException(email);
        } else if(usernameExists) {
            throw new UsernameAlreadyExistsException(username);
        }

        String encodedPassword = encryptionService.encode(password);
        registrationBody.setPassword(encodedPassword);
        
        // Zapisz w bazie 'VerificationToken', wtedy gdy email jest faktycznie poprawny.
        LocalUser user = registrationBodyToLocalUser(registrationBody);
        userRepository.save(user);

        VerificationToken verificationToken = verificationService.createVerificationToken(user);
        try {
            emailService.makeAndSendVerificationMail(verificationToken);
        } catch (MailSendException e) {
            throw new MailSendException("An email cannot be sent.", e);
        } catch(MessagingException e) {
            throw new MessagingException("Error while composing email.", e);
        }
        tokenRepository.save(verificationToken);

        return user;
    }
    @Override
    public LoginResponse login(LoginBody loginBody) throws IncorrectCredentialsException, UserNotVerifiedException, MailSendException, MessagingException {
            
        String email = loginBody.getEmail();
        String rawPassword = loginBody.getPassword();  //raw password
        LocalUser user = userRepository.findByEmailIgnoreCase(email).orElseThrow(() -> new IncorrectCredentialsException("Credentials are not correct!"));
            if(encryptionService.verify(rawPassword, user.getPassword())) {
                // password is correct, dołącz JWT do response
                // Utwórz jwt tylko gdy User zweryfikował konto
                if(user.isEmailVerified()) {
                    String jwt = jwtService.generateJWT(user.getId());
                    LoginResponse loginResponse = LoginResponse.success(user.getId(), jwt, "Login process went perfectly.");
    
                    SecurityContext securityContext = SecurityContextHolder.getContext();
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
                    securityContext.setAuthentication(authentication);

                    return loginResponse;
                } else {
                    //1.  wcale nie wysłany lub link nieaktywny 
                    List<VerificationToken> tokens = user.getVerificationTokens();
                    boolean resend = tokens.size() == 0 || tokens.get(0).isExpired();//.getCreatedTimestamp().isBefore(LocalDateTime.now().minusHours(1))
                    if(resend) {
                        VerificationToken newToken = verificationService.createVerificationToken(user);
                        tokenRepository.save(newToken);

                        emailService.makeAndSendVerificationMail(newToken);
                    }
                    //TODO: Uspraw te łapanie wyjątków, aby przekazywały 'cause' wyżej
                    throw new UserNotVerifiedException("Please check your mailbox and verify your account.", !resend);
                }
            } else {
                throw new IncorrectCredentialsException("Credentials are not correct!");
            }
    }

    @Override
    public boolean checkEmailAlreadyExists(String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    }

    @Override
    public boolean checkUsernameAlreadyExists(String username) {
        return userRepository.existsByUsernameIgnoreCase(username);
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
    @Transactional  // metody zmieniające coś w bazie danych powinny być oznaczone @Transactional
    public boolean verifyUser(String token) {
        System.out.println("METODA verifyUser() uruchomiona!");
        Optional<VerificationToken> opToken = tokenRepository.findByToken(token);
        if(opToken.isPresent()) {
            VerificationToken verificationToken = opToken.get();
            LocalUser user = verificationToken.getUser();

            // sprawdz czy juz sie nie zweryfikował
            if(!user.isEmailVerified()) {
                //ok
                user.setEmailVerified(true);
                userRepository.save(user);
                tokenRepository.deleteByUser(user);
                return true;
            } else {
                throw new UserAlreadyVerifiedException("You are verified. Doing it again is unnecessary.");
            }
        } else 
            throw new TokenNotFoundException("Invalid or expired token.");
    }
}
