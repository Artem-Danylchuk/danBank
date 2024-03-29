package Bank.backBank.services;
import Bank.backBank.entity.ClientsLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class UserDetailsServiceImp implements UserDetailsService {
    private final UserService userService;
    public UserDetailsServiceImp(UserService userService) {
        this.userService = userService;
    }

    @Override
    @Transactional (readOnly = true)
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        ClientsLogin customUser = userService.findByLogin(login);
        if (customUser == null)
            throw new UsernameNotFoundException(login + " not found");

        List<GrantedAuthority> roles = Arrays.asList(
                new SimpleGrantedAuthority(customUser.getRole().toString())
        );

        return new User(customUser.getLogin(), customUser.getPassword(), roles);
    }

}