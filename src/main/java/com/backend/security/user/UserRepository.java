package com.backend.security.user;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.backend.security.user.entity.User;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, ObjectId> {
    
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);
}
