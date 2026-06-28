package com.lockify.phase1.coreauth.security;

import com.lockify.shared.domain.entity.User;
import com.lockify.shared.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Security ko User load karne ka tareeka batata hai.
 * Login flow me password verify hone ke baad yeh call hota hai.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        User user = userRepository.findByIdentifierWithCredential(identifier)
                .orElseThrow(() -> new UsernameNotFoundException("User nahi mila: " + identifier));

        return new LockifyUserDetails(user);
    }
}
