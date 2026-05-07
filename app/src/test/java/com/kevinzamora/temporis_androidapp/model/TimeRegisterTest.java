package com.kevinzamora.temporis_androidapp.model;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.Map;

public class TimeRegisterTest {

    private TimeRegister timeRegister;
    private final String ID = "test_id";
    private final long DATE = 1625097600000L;
    private final int DURATION = 3600;
    private final String CATEGORY = "Trabajo";
    private final String DESCRIPTION = "Desarrollo de tests";
    private final String COUNTER_ID = "counter_123";

    @Before
    public void setUp() {
        timeRegister = new TimeRegister(ID, DATE, DURATION, CATEGORY, DESCRIPTION, COUNTER_ID);
    }

    @Test
    public void testGetters() {
        assertEquals(ID, timeRegister.getId());
        assertEquals(DATE, timeRegister.getDate());
        assertEquals(DURATION, timeRegister.getDuration());
        assertEquals(CATEGORY, timeRegister.getCategory());
        assertEquals(DESCRIPTION, timeRegister.getDescription());
        assertEquals(COUNTER_ID, timeRegister.getCounterId());
    }

    @Test
    public void testSetters() {
        timeRegister.setId("new_id");
        assertEquals("new_id", timeRegister.getId());

        timeRegister.setDuration(500);
        assertEquals(500, timeRegister.getDuration());
    }

    @Test
    public void testIsValid() {
        // Caso válido
        assertTrue(timeRegister.isValid());

        // Caso inválido: ID vacío
        timeRegister.setId("");
        assertFalse(timeRegister.isValid());

        // Caso inválido: Fecha cero
        timeRegister.setId(ID);
        timeRegister.setDate(0L);
        assertFalse(timeRegister.isValid());
    }

    @Test
    public void testToMap() {
        Map<String, Object> map = timeRegister.toMap();

        assertEquals(ID, map.get("id"));
        assertEquals(DATE, map.get("date"));
        assertEquals(DURATION, map.get("duration"));
        assertEquals(CATEGORY, map.get("category"));
        assertEquals(DESCRIPTION, map.get("description"));
        assertEquals(COUNTER_ID, map.get("counterId"));
    }
}