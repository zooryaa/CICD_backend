package com.example.demo.domain.user.query;

import com.example.demo.core.generic.AbstractQueryServiceImpl;
import com.example.demo.domain.user.User;
import com.example.demo.domain.user.UserDetailsImpl;
import com.example.demo.domain.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class UserQueryServiceImpl extends AbstractQueryServiceImpl<User> implements UserQueryService {

    @Autowired
    public UserQueryServiceImpl(UserRepository userRepository) {
        super(userRepository);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return ((UserRepository) repository).findByEmail(email)
                .map(UserDetailsImpl::new)
                .orElseThrow(() -> new UsernameNotFoundException(email));
    }

    @Override
    public User findByEmail(String name) {
        return ((UserRepository) repository).findByEmail(name)
                .orElseThrow(() -> new NoSuchElementException("Unable to find provided email."));
    }
}
