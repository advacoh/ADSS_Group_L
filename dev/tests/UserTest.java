import domain.hr.User;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(100000001, "password123");
    }

    // Creation Tests

    @Test
    void testUserCreatedWithCorrectID() {
        assertEquals(100000001, user.getId());
    }

    @Test
    void testValidUserCreatedSuccessfully() {
        assertDoesNotThrow(() -> new User(123456789, "validPass"));
    }

    //  ID Validation Tests 

    @Test
    void testIDTooShortThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new User(12345678, "password123")); // 8 digits
    }

    @Test
    void testIDTooLongThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new User(1000000000, "password123")); // 10 digits
    }

    @Test
    void testIDMinBoundarySucceeds() {
        assertDoesNotThrow(() -> new User(100000000, "password123"));
    }

    @Test
    void testIDMaxBoundarySucceeds() {
        assertDoesNotThrow(() -> new User(999999999, "password123"));
    }

    @Test
    void testIDZeroThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new User(0, "password123"));
    }

    // Password Validation Tests 

    @Test
    void testPasswordTooShortThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new User(100000001, "abc")); // less than 6 digits
    }

    @Test
    void testPasswordExactly6DigitsSucceeds() {
        assertDoesNotThrow(() -> new User(100000001, "abcdef"));
    }

    @Test
    void testNullPasswordThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new User(100000001, null));
    }

    @Test
    void testEmptyPasswordThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new User(100000001, ""));
    }

    // --- Login Tests ---

    @Test
    void testLoginWithCorrectPasswordSucceeds() {
        assertTrue(user.login("password123"));
    }

    @Test
    void testLoginWithWrongPasswordFails() {
        assertFalse(user.login("wrongpassword"));
    }

    @Test
    void testLoginWithEmptyPasswordFails() {
        assertFalse(user.login(""));
    }

    @Test
    void testLoginWithNullPasswordReturnsFalse() {
        assertFalse(user.login(null));
    }

    @Test
    void testLoginIsCaseSensitive() {
        assertFalse(user.login("Password123")); // capital P
    }
}

