package com.example.demo.domain.user.query;

import com.example.demo.core.generic.AbstractQueryService;
import com.example.demo.domain.user.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserQueryService extends UserDetailsService, AbstractQueryService<User> {
    User findByEmail(String name);
}
