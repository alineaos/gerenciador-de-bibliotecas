package io.github.alineaos.librarymanager.security.service;

import io.github.alineaos.librarymanager.exception.NotFoundException;
import io.github.alineaos.librarymanager.repository.UserRepository;
import io.github.alineaos.librarymanager.security.domain.UserAuthenticated;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(UserAuthenticated::new)
                .orElseThrow(() -> new NotFoundException("User not found."));
    }
}
