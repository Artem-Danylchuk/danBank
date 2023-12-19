package Bank.backBank.services;

import Bank.backBank.repository.ClientsLoginRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class SecurityServiceImpl implements SecurityService{

    private static final Logger logger = LoggerFactory.getLogger(SecurityServiceImpl.class);


    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;
    private UserDetailsServiceImp userDetailsServiceImp;
    private ClientsLoginRepository clientsLoginRepository;

    @Override
    public void autoLogin(String username, String password) {
         UserDetails userDetails = userDetailsService.loadUserByUsername(username);
         UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());


        if (authenticationToken.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            logger.debug(String.format("Successfully %s auto logged in", username));
        }
    }

}
