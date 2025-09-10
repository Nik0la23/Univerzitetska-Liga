package mk.ukim.finki.wp.liga.SimpleUser;

import mk.ukim.finki.wp.liga.model.User;
import mk.ukim.finki.wp.liga.repository.UserRepository;
import mk.ukim.finki.wp.liga.service.impl.SimpleUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SimpleUserServiceTest {

    @Mock
    private UserRepository userRepository;

    private SimpleUserService userService;

    // We will use a real encoder in our test to create valid hashes
    private final BCryptPasswordEncoder testEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        // Because the service creates its own encoder, we can't use @InjectMocks.
        // Instead, we manually create the service instance and pass in our mock repository.
        userService = new SimpleUserService(userRepository);
    }

    @Test
    void testRegister_Success() {
        // Arrange
        String name = "testuser";
        String email = "test@example.com";
        String rawPassword = "password123";

        when(userRepository.existsByName(name)).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(false);
        // When save is called, just return the object that was passed to it
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User result = userService.register(name, email, rawPassword);

        // Assert
        assertNotNull(result);
        assertEquals(name, result.getName());
        assertEquals(email, result.getEmail());

        // Capture the user object that was passed to the save method
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        // Verify that the password was correctly encoded
        assertTrue(testEncoder.matches(rawPassword, savedUser.getPasswordHash()));
    }

    @Test
    void testRegister_NameAlreadyTaken_ShouldThrowException() {
        // Arrange
        when(userRepository.existsByName("existinguser")).thenReturn(true);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> userService.register("existinguser", "test@example.com", "password123"));
        assertEquals("Name already taken", exception.getMessage());
    }

    @Test
    void testRegister_EmailAlreadyInUse_ShouldThrowException() {
        // Arrange
        when(userRepository.existsByName("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> userService.register("testuser", "existing@example.com", "password123"));
        assertEquals("Email already in use", exception.getMessage());
    }

    @Test
    void testLogin_Success() {
        // Arrange
        String name = "testuser";
        String rawPassword = "password123";
        String hashedPassword = testEncoder.encode(rawPassword);

        User user = new User();
        user.setName(name);
        user.setPasswordHash(hashedPassword);

        when(userRepository.findByName(name)).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = userService.login(name, rawPassword);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(name, result.get().getName());
    }

    @Test
    void testLogin_UserNotFound() {
        // Arrange
        when(userRepository.findByName("nonexistentuser")).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.login("nonexistentuser", "password123");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testLogin_IncorrectPassword() {
        // Arrange
        String name = "testuser";
        String correctPassword = "password123";
        String wrongPassword = "wrongpassword";
        String hashedPassword = testEncoder.encode(correctPassword);

        User user = new User();
        user.setName(name);
        user.setPasswordHash(hashedPassword);

        when(userRepository.findByName(name)).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = userService.login(name, wrongPassword);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByName() {
        // Arrange
        User user = new User();
        user.setName("testuser");
        when(userRepository.findByName("testuser")).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = userService.findByName("testuser");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getName());
    }
}
