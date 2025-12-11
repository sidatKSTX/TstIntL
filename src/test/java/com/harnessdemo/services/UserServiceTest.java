package com.harnessdemo.services;

import com.harnessdemo.models.User;
import com.harnessdemo.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setActive(true);
    }

    // ==================== Create User Tests (10 tests) ====================

    @Test
    void testCreateUserSuccess() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.createUser("Test User", "test@example.com");

        assertNotNull(result);
        assertEquals("Test User", result.getName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testCreateUserWithNullName() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userService.createUser(null, "test@example.com");

        assertNull(result.getName());
    }

    @Test
    void testCreateUserWithEmptyName() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userService.createUser("", "test@example.com");

        assertEquals("", result.getName());
    }

    @Test
    void testCreateUserWithDuplicateEmail() {
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThrows(RuntimeException.class, () ->
            userService.createUser("Test", "existing@example.com"));
    }

    @Test
    void testCreateUserSetsActiveTrue() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userService.createUser("Test", "test@example.com");

        assertTrue(result.isActive());
    }

    @Test
    void testCreateUserWithLongName() {
        String longName = "A".repeat(100);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userService.createUser(longName, "test@example.com");

        assertEquals(100, result.getName().length());
    }

    @Test
    void testCreateUserWithSpecialCharacters() {
        String specialName = "Test User-O'Brien";
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userService.createUser(specialName, "test@example.com");

        assertEquals(specialName, result.getName());
    }

    @Test
    void testCreateUserRepositorySaveCalled() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.createUser("Test", "test@example.com");

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUserWithUnicodeCharacters() {
        String unicodeName = "用户名";
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userService.createUser(unicodeName, "test@example.com");

        assertEquals(unicodeName, result.getName());
    }

    @Test
    void testCreateUserEmailStoredCorrectly() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userService.createUser("Test", "specific@email.com");

        assertEquals("specific@email.com", result.getEmail());
    }

    // ==================== Get User Tests (10 tests) ====================

    @Test
    void testGetUserByIdSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void testGetUserByIdNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetUserByIdNull() {
        when(userRepository.findById(null)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(null);

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetUserByEmailSuccess() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.getUserByEmail("test@example.com");

        assertTrue(result.isPresent());
    }

    @Test
    void testGetUserByEmailNotFound() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserByEmail("notfound@example.com");

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAllUsersEmpty() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<User> result = userService.getAllUsers();

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAllUsersMultiple() {
        User user2 = new User();
        user2.setId(2L);
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser, user2));

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
    }

    @Test
    void testGetActiveUsers() {
        when(userRepository.findByActive(true)).thenReturn(Arrays.asList(testUser));

        List<User> result = userService.getActiveUsers();

        assertEquals(1, result.size());
        assertTrue(result.get(0).isActive());
    }

    @Test
    void testGetUserReturnsCorrectFields() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals("Test User", result.get().getName());
        assertEquals("test@example.com", result.get().getEmail());
    }

    @Test
    void testSearchUsersByName() {
        when(userRepository.findByNameContainingIgnoreCase("Test")).thenReturn(Arrays.asList(testUser));

        List<User> result = userService.searchUsersByName("Test");

        assertEquals(1, result.size());
    }

    // ==================== Update User Tests (15 tests) ====================

    @Test
    void testUpdateUserSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userService.updateUser(1L, "Updated Name", "test@example.com");

        assertEquals("Updated Name", result.getName());
    }

    @Test
    void testUpdateUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
            userService.updateUser(999L, "Name", "email@test.com"));
    }

    @Test
    void testUpdateUserNameOnly() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userService.updateUser(1L, "New Name", "test@example.com");

        assertEquals("New Name", result.getName());
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void testUpdateUserEmailOnly() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("newemail@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userService.updateUser(1L, "Test User", "newemail@example.com");

        assertEquals("newemail@example.com", result.getEmail());
    }

    @Test
    void testUpdateUserDuplicateEmail() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThrows(RuntimeException.class, () ->
            userService.updateUser(1L, "Test", "existing@example.com"));
    }

    @Test
    void testUpdateUserPreservesId() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userService.updateUser(1L, "New Name", "test@example.com");

        assertEquals(1L, result.getId());
    }

    @Test
    void testUpdateUserPreservesActive() {
        testUser.setActive(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userService.updateUser(1L, "New Name", "test@example.com");

        assertTrue(result.isActive());
    }

    @Test
    void testUpdateUserWithSameEmail() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userService.updateUser(1L, "New Name", "test@example.com");

        assertNotNull(result);
    }

    @Test
    void testUpdateUserWithLongName() {
        String longName = "A".repeat(100);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userService.updateUser(1L, longName, "test@example.com");

        assertEquals(100, result.getName().length());
    }

    @Test
    void testUpdateUserSaveCalled() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.updateUser(1L, "New Name", "test@example.com");

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUserFindByIdCalled() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.updateUser(1L, "New Name", "test@example.com");

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateUserWithEmptyName() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userService.updateUser(1L, "", "test@example.com");

        assertEquals("", result.getName());
    }

    @Test
    void testUpdateUserWithNullName() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userService.updateUser(1L, null, "test@example.com");

        assertNull(result.getName());
    }

    @Test
    void testUpdateUserWithSpecialCharacters() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userService.updateUser(1L, "O'Brien-Smith", "test@example.com");

        assertEquals("O'Brien-Smith", result.getName());
    }

    @Test
    void testUpdateUserWithUnicodeCharacters() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userService.updateUser(1L, "用户名", "test@example.com");

        assertEquals("用户名", result.getName());
    }

    // ==================== Delete User Tests (10 tests) ====================

    @Test
    void testDeleteUserSuccess() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        assertDoesNotThrow(() -> userService.deleteUser(1L));
        verify(userRepository).deleteById(1L);
    }

    @Test
    void testDeleteUserNotFound() {
        when(userRepository.existsById(999L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> userService.deleteUser(999L));
    }

    @Test
    void testDeleteUserDeleteByIdCalled() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeactivateUserSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userService.deactivateUser(1L);

        assertFalse(result.isActive());
    }

    @Test
    void testDeactivateUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.deactivateUser(999L));
    }

    @Test
    void testActivateUserSuccess() {
        testUser.setActive(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userService.activateUser(1L);

        assertTrue(result.isActive());
    }

    @Test
    void testActivateUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.activateUser(999L));
    }

    @Test
    void testDeleteUserVerifiesExistence() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository).existsById(1L);
    }

    @Test
    void testDeactivateUserSaveCalled() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.deactivateUser(1L);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void testActivateUserSaveCalled() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.activateUser(1L);

        verify(userRepository).save(any(User.class));
    }

    // ==================== Validation Tests (5 tests) - These will be selected by TI ====================
    // These tests will be run when validateEmail method is added

    @Test
    void testValidateEmailPlaceholder1() {
        // Placeholder for email validation test
        assertTrue(true);
    }

    @Test
    void testValidateEmailPlaceholder2() {
        // Placeholder for email validation test
        assertTrue(true);
    }

    @Test
    void testValidateEmailPlaceholder3() {
        // Placeholder for email validation test
        assertTrue(true);
    }

    @Test
    void testValidateEmailPlaceholder4() {
        // Placeholder for email validation test
        assertTrue(true);
    }

    @Test
    void testValidateEmailPlaceholder5() {
        // Placeholder for email validation test
        assertTrue(true);
    }
}
