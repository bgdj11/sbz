package sbnz.integracija.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sbnz.integracija.example.dto.DTOMapper;
import sbnz.integracija.example.dto.UserDTO;
import sbnz.integracija.example.entity.User;
import sbnz.integracija.example.service.UserService;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> test() {
        return ResponseEntity.ok(Map.of("message", "API is working", "status", "success"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData, 
                                  HttpSession session) {
        
        String email = loginData.get("email");
        String password = loginData.get("password");
        
        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email and password are required"));
        }
        
        Optional<User> userOpt = userService.authenticateUser(email, password);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            session.setAttribute("currentUser", user);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Login successful");
            response.put("user", DTOMapper.toUserDTO(user));
            
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid email or password"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> registerData) {
        String firstName = registerData.get("firstName");
        String lastName = registerData.get("lastName");
        String email = registerData.get("email");
        String password = registerData.get("password");
        String city = registerData.get("city");
        
        if (firstName == null || lastName == null || email == null || password == null || city == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "All fields are required"));
        }
        
        try {
            User user = userService.registerUser(firstName, lastName, email, password, city);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Registration successful! Please log in.");
            response.put("user", DTOMapper.toUserDTO(user));
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("success", true, "message", "Logged out successfully"));
    }
}