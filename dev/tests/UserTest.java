package domain.hr;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User Domain Class Tests")
public class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(100000001, "password123");
    }

    @Nested
    @DisplayName("1. Constructor & Basic Properties")
    class CreationTests {

        @Test
        @DisplayName("Should successfully create a user and return the correct ID")
        void testUserCreatedWithCorrectID() {
            assertEquals(100000001, user.getId());
        }

        @Test
        @DisplayName("Should not throw any exceptions when given a valid ID and password")
        void testValidUserCreatedSuccessfully() {
            assertDoesNotThrow(() -> new User(123456789, "validPass"));
        }
    }

    @Nested
    @DisplayName("2. ID Validation Constraints")
    class IdValidationTests {

        @Test
        @DisplayName("Should throw exception if ID is less than 9 digits")
        void testIDTooShortThrows() {
            assertThrows(IllegalArgumentException.class, () ->
                new User(12345678, "password123")); 
        }

        @Test
        @DisplayName("Should throw exception if ID is more than 9 digits")
        void testIDTooLongThrows() {
            assertThrows(IllegalArgumentException.class, () ->
                new User(1000000000, "password123")); 
        }

        @Test
        @DisplayName("Should successfully create user with the valid 9-digit ID")
        void testIDMinBoundarySucceeds() {
            assertDoesNotThrow(() -> new User(100000000, "password123"));
        }
    }

    @Nested
    @DisplayName("3. Password Validation Constraints")
    class PasswordValidationTests {

        @Test
        @DisplayName("Should throw exception if password is less than 6 characters")
        void testPasswordTooShortThrows() {
            assertThrows(IllegalArgumentException.class, () ->
                new User(100000001, "abc"));
        }

        @Test
        @DisplayName("Should successfully create user with exactly 6 characters")
        void testPasswordExactly6DigitsSucceeds() {
            assertDoesNotThrow(() -> new User(100000001, "abcdef"));
        }

        @Test
        @DisplayName("Should throw exception if password is null")
        void testNullPasswordThrows() {
            assertThrows(IllegalArgumentException.class, () ->
                new User(100000001, null));
        }

        @Test
        @DisplayName("Should throw exception if password is an empty string")
        void testEmptyPasswordThrows() {
            assertThrows(IllegalArgumentException.class, () ->
                new User(100000001, ""));
        }
    }

    @Nested
    @DisplayName("4. Login")
    class LoginTests {

        @Test
        @DisplayName("Login should return true for the exact matching password")
        void testLoginWithCorrectPasswordSucceeds() {
            assertTrue(user.login("password123"));
        }

        @Test
        @DisplayName("Login should return false for an incorrect password")
        void testLoginWithWrongPasswordFails() {
            assertFalse(user.login("wrongpassword"));
        }

        @Test
        @DisplayName("Login should return false for an empty string")
        void testLoginWithEmptyPasswordFails() {
            assertFalse(user.login(""));
        }

        @Test
        @DisplayName("Login should safely return false when passed a null value")
        void testLoginWithNullPasswordReturnsFalse() {
            assertFalse(user.login(null));
        }

        @Test
        @DisplayName("Login should be strictly case-sensitive")
        void testLoginIsCaseSensitive() {
            assertFalse(user.login("Password123"));
        }
    }
}
