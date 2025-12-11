package com.harnessdemo.controllers;

import com.harnessdemo.models.User;
import com.harnessdemo.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setActive(true);
    }

    // ==================== Create User Endpoint Tests (5 tests) ====================

    @Test
    void testCreateUserEndpoint() {
        when(userService.createUser(anyString(), anyString())).thenReturn(testUser);

        ResponseEntity<User> response = userController.createUser(
            new UserController.CreateUserRequest("Test User", "test@example.com"));

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testCreateUserReturnsCreatedStatus() {
        when(userService.createUser(anyString(), anyString())).thenReturn(testUser);

        ResponseEntity<User> response = userController.createUser(
            new UserController.CreateUserRequest("Test", "test@example.com"));

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void testCreateUserReturnsUser() {
        when(userService.createUser(anyString(), anyString())).thenReturn(testUser);

        ResponseEntity<User> response = userController.createUser(
            new UserController.CreateUserRequest("Test", "test@example.com"));

        assertEquals("Test User", response.getBody().getName());
    }

    @Test
    void testCreateUserCallsService() {
        when(userService.createUser(anyString(), anyString())).thenReturn(testUser);

        userController.createUser(new UserController.CreateUserRequest("Test", "test@example.com"));

        verify(userService).createUser("Test", "test@example.com");
    }

    @Test
    void testCreateUserWithProvidedData() {
        User newUser = new User();
        newUser.setName("New User");
        newUser.setEmail("new@example.com");
        when(userService.createUser("New User", "new@example.com")).thenReturn(newUser);

        ResponseEntity<User> response = userController.createUser(
            new UserController.CreateUserRequest("New User", "new@example.com"));

        assertEquals("New User", response.getBody().getName());
    }

    // ==================== Get User Endpoint Tests (5 tests) ====================

    @Test
    void testGetUserByIdSuccess() {
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));

        ResponseEntity<User> response = userController.getUserById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetUserByIdNotFound() {
        when(userService.getUserById(999L)).thenReturn(Optional.empty());

        ResponseEntity<User> response = userController.getUserById(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetUserByIdReturnsUser() {
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));

        ResponseEntity<User> response = userController.getUserById(1L);

        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void testGetAllUsersSuccess() {
        when(userService.getAllUsers()).thenReturn(Arrays.asList(testUser));

        ResponseEntity<List<User>> response = userController.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testGetAllUsersEmpty() {
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        ResponseEntity<List<User>> response = userController.getAllUsers();

        assertTrue(response.getBody().isEmpty());
    }

    // ==================== Update/Delete User Endpoint Tests (5 tests) ====================

    @Test
    void testUpdateUserSuccess() {
        when(userService.updateUser(anyLong(), anyString(), anyString())).thenReturn(testUser);

        ResponseEntity<User> response = userController.updateUser(1L,
            new UserController.UpdateUserRequest("Updated", "updated@example.com"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testDeleteUserSuccess() {
        doNothing().when(userService).deleteUser(1L);

        ResponseEntity<Void> response = userController.deleteUser(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testDeactivateUserSuccess() {
        testUser.setActive(false);
        when(userService.deactivateUser(1L)).thenReturn(testUser);

        ResponseEntity<User> response = userController.deactivateUser(1L);

        assertFalse(response.getBody().isActive());
    }

    @Test
    void testActivateUserSuccess() {
        when(userService.activateUser(1L)).thenReturn(testUser);

        ResponseEntity<User> response = userController.activateUser(1L);

        assertTrue(response.getBody().isActive());
    }

    @Test
    void testSearchUsersSuccess() {
        when(userService.searchUsersByName("Test")).thenReturn(Arrays.asList(testUser));

        ResponseEntity<List<User>> response = userController.searchUsers("Test");

        assertEquals(1, response.getBody().size());
    }

    // ==================== Additional Tests (5 tests) ====================

    @Test
    void testGetActiveUsersSuccess() {
        when(userService.getActiveUsers()).thenReturn(Arrays.asList(testUser));

        ResponseEntity<List<User>> response = userController.getActiveUsers();

        assertEquals(1, response.getBody().size());
    }

    @Test
    void testUpdateUserCallsService() {
        when(userService.updateUser(anyLong(), anyString(), anyString())).thenReturn(testUser);

        userController.updateUser(1L, new UserController.UpdateUserRequest("Name", "email@test.com"));

        verify(userService).updateUser(1L, "Name", "email@test.com");
    }

    @Test
    void testDeleteUserCallsService() {
        doNothing().when(userService).deleteUser(1L);

        userController.deleteUser(1L);

        verify(userService).deleteUser(1L);
    }

    @Test
    void testSearchUsersEmpty() {
        when(userService.searchUsersByName("NonExistent")).thenReturn(Collections.emptyList());

        ResponseEntity<List<User>> response = userController.searchUsers("NonExistent");

        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testGetActiveUsersEmpty() {
        when(userService.getActiveUsers()).thenReturn(Collections.emptyList());

        ResponseEntity<List<User>> response = userController.getActiveUsers();

        assertTrue(response.getBody().isEmpty());
    }
}
