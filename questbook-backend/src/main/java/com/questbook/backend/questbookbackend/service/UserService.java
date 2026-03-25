package com.questbook.backend.questbookbackend.service;
import com.questbook.backend.questbookbackend.model.User;
import com.questbook.backend.questbookbackend.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {
private final UserRepository userRepository;

public UserService(UserRepository userRepository){
    this.userRepository=userRepository;

}
// register user
public User registerUser(User user){
    if(userRepository.findByUsername(user.getUsername()).isPresent()){
        throw new RuntimeException("Username already exists");//check if username already exists
    }
    if(userRepository.findByEmail(user.getEmail()).isPresent()){
        throw new RuntimeException("Email already exists");//check if email already exists
    }
    return userRepository.save(user);
}

public User loginUser(String username, String password){
    Optional<User>optionalUser = userRepository.findByUsername(username);

    if(optionalUser.isEmpty()){
        throw new RuntimeException("User not found");
    }
    User user = optionalUser.get();

    if(!user.getPassword().equals(password)){
        throw new RuntimeException("Invalid password");

    }
    return user;
}
    public User getUserById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

}
