package com.backend.security.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.backend.security.service.SecuritySerivce;
import com.backend.security.user.dtos.UserInfoResponse;
import com.backend.security.user.entity.User;

import lombok.Setter;

@Service
@Setter
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    private SecuritySerivce securitySerivce;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }


    public UserInfoResponse getUserInfo() {
        var user = securitySerivce.getUserDetails();
        return UserInfoResponse.from((User) user);
    }

}
    

