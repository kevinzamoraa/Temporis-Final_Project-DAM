package com.kevinzamora.temporis_androidapp.model;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.Map;

public class CounterTest {

    public void testGetId() {
    }

    public void testGetTitle() {
    }

    public void testGetType() {
    }

    public void testGetActualValue() {
    }

    public void testGetDailyObjective() {
    }

    public void testIsActive() {
    }

    public void testSetId() {
    }

    public void testSetTitle() {
    }

    public void testSetType() {
    }

    public void testSetActualValue() {
    }

    public void testSetDailyObjective() {
    }

    public void testSetActive() {
    }

    @Test
    public void testSettersAndGetters() {
        Counter counter = new Counter();
        counter.setTitle("Ejercicio");
        assertEquals("Ejercicio", counter.getTitle());

        counter.setActive(false);
        assertFalse(counter.isActive());
    }

    @Test
    public void testGettersSettersAndConstructors() {
        Counter counter = new Counter("ID1", "Título", "Tipo", 5, 10, true);

        assertEquals("ID1", counter.getId());
        assertEquals("Título", counter.getTitle());
        assertEquals(5, counter.getActualValue());

        counter.setId("ID2");
        counter.setActualValue(15);
        assertEquals("ID2", counter.getId());
        assertEquals(15, counter.getActualValue());
    }

    @Test
    public void testReachedGoal() {
        Counter counter = new Counter("1", "Test", "Test", 5, 10, true);
        assertFalse(counter.reachedGoal());

        counter.setActualValue(10);
        assertTrue(counter.reachedGoal());
    }

    @Test
    public void testToMap() {
        Counter counter = new Counter("ID1", "T", "T", 1, 1, true);
        Map<String, Object> map = counter.toMap();
        assertEquals("ID1", map.get("id"));
        assertTrue((Boolean) map.get("active"));
    }
}