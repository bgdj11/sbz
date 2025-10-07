package sbnz.integracija.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sbnz.integracija.example.entity.User;
import sbnz.integracija.example.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(String firstName, String lastName, String email, String password, String city) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setCity(city);
        user.setAdmin(false);

        return userRepository.save(user);
    }

    public Optional<User> authenticateUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) {
            return userOpt;
        }
        return Optional.empty();
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> searchUsers(String searchTerm) {
        return userRepository.searchUsers(searchTerm);
    }

    public void addFriend(User user, User friend) {
        user.getFriends().add(friend);
        friend.getFriends().add(user);
        userRepository.save(user);
        userRepository.save(friend);
    }

    public void blockUser(User user, User userToBlock) {
        user.getBlockedUsers().add(userToBlock);
        // Remove from friends if they were friends
        user.getFriends().remove(userToBlock);
        userToBlock.getFriends().remove(user);
        userRepository.save(user);
        userRepository.save(userToBlock);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getAllRegularUsers() {
        return userRepository.findAllRegularUsers();
    }

    public List<String> getFriendIds(String userId) {
        Optional<User> userOpt = userRepository.findById(Long.valueOf(userId));
        if (userOpt.isPresent()) {
            return userOpt.get().getFriends().stream()
                .map(u -> u.getId().toString())
                .collect(java.util.stream.Collectors.toList());
        }
        return java.util.Collections.emptyList();
    }

    public List<String> getBlockedIds(String userId) {
        Optional<User> userOpt = userRepository.findById(Long.valueOf(userId));
        if (userOpt.isPresent()) {
            return userOpt.get().getBlockedUsers().stream()
                .map(u -> u.getId().toString())
                .collect(java.util.stream.Collectors.toList());
        }
        return java.util.Collections.emptyList();
    }
}