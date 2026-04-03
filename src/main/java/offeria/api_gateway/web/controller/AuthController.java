package offeria.api_gateway.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import offeria.api_gateway.domain.dto.LoginRequest;
import offeria.api_gateway.domain.dto.UserRegistrationDto;
import offeria.api_gateway.domain.dto.UserResponseDto;
import offeria.api_gateway.service.AuthService;
import offeria.api_gateway.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Controller for authentication and registration.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/register")
    public Mono<ResponseEntity<UserResponseDto>> register(@Valid @RequestBody UserRegistrationDto registrationDto) {
        // Since JPA is blocking, we wrap it in Mono.fromCallable and run on a dedicated scheduler
        // In a real production reactive app, we should use R2DBC.
        return Mono.fromCallable(() -> userService.registerUser(registrationDto))
                .map(ResponseEntity::ok);
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<Map<String, String>>> login(@Valid @RequestBody LoginRequest loginRequest) {
        return Mono.fromCallable(() -> authService.authenticate(loginRequest))
                .map(token -> ResponseEntity.ok(Map.of("token", token)));
    }
}
