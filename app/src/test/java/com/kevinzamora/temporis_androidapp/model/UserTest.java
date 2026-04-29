package com.kevinzamora.temporis_androidapp.model;

import junit.framework.TestCase;

public class UserTest extends TestCase {

    private User user;
    private final String uid = "12345";
    private final String username = "kz_temporis";
    private final String email = "kevin@example.com";
    private final String displayName = "Kevin Zamora";
    private final String photo = "url_to_photo";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // Inicializamos un usuario base para las pruebas
        user = new User(uid, username, email, displayName, photo);
    }

    // Test del Constructor y Getters
    public void testUserConstructorAndGetters() {
        assertNotNull("El objeto User no debería ser nulo", user);
        assertEquals(uid, user.getUid());
        assertEquals(username, user.getUsername());
        assertEquals(email, user.getEmail());
        assertEquals(displayName, user.getDisplayName());
        assertEquals(photo, user.getProfilePhotoUrl());
    }

    public void testUserEquality() {
        // Creamos dos instancias con el mismo UID
        User user1 = new User("1", "test", "test@test.com", "Test User", "");
        User user2 = new User("1", "test", "test@test.com", "Test User", "");

        // Seteamos el mismo rol manualmente por si acaso el constructor no lo hace
        user1.setRol(1);
        user2.setRol(1);

        // Verificamos igualdad
        assertTrue("Los usuarios con el mismo UID deberían ser iguales", user1.equals(user2));
        assertEquals("Los HashCodes deben coincidir", user1.hashCode(), user2.hashCode());
    }

    // Test de Setters
    public void testSetters() {
        user.setUid("new_uid");
        assertEquals("new_uid", user.getUid());

        user.setUsername("new_username");
        assertEquals("new_username", user.getUsername());

        user.setEmail("new@email.com");
        assertEquals("new@email.com", user.getEmail());

        user.setDisplayName("New Name");
        assertEquals("New Name", user.getDisplayName());

        user.setProfilePhotoUrl("new_url");
        assertEquals("new_url", user.getProfilePhotoUrl());

        user.setPassword("secret123");
        assertEquals("secret123", user.getPassword());

        user.setQrCode("qr_data");
        assertEquals("qr_data", user.getQrCode());

        user.setRol(0);
        assertEquals(0, user.getRol());
    }

    // Test de ToString
    public void testToString() {
        String userString = user.toString();
        assertNotNull(userString);
        assertTrue(userString.contains(username));
        assertTrue(userString.contains(email));
    }
}