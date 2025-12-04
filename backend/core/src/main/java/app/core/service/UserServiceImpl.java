package app.core.service;

import app.core.errorhandling.exceptions.UserAlreadyExistsException;
import app.core.model.User;
import app.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        return user.orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }

    public void createUser(String username, String rawPassword, String email) throws UserAlreadyExistsException {
        if (userRepository.existsByUsername(username)) {
            throw new UserAlreadyExistsException("Пользователь с username '" + username + "' уже существует");
        }

        User newUser = User.builder()
                .username(username)
                .password(bCryptPasswordEncoder.encode(rawPassword))
                .email(email)
                .build();

        userRepository.save(newUser);
        log.debug("User {} successfully created", newUser.getUsername());
    }
}
