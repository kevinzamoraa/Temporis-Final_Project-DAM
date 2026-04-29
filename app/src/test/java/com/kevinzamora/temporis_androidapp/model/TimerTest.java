package com.kevinzamora.temporis_androidapp.model;

import com.google.firebase.Timestamp;
import junit.framework.TestCase;
import java.util.Date;

public class TimerTest extends TestCase {

    private Timer timer;
    private final String id = "timer_001";
    private final String name = "Pomodoro";
    private final int duration = 25;
    private final boolean isActive = true;
    private final Timestamp createdAt = new Timestamp(new Date());
    private final String uid = "user_123";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // Inicializamos un objeto base para las pruebas
        timer = new Timer(id, name, duration, isActive, createdAt, uid);
    }

    public void testGetId() {
        assertEquals(id, timer.getId());
    }

    public void testTestGetName() {
        assertEquals(name, timer.getName());
    }

    public void testGetDuration() {
        assertEquals(duration, timer.getDuration());
    }

    public void testIsActive() {
        assertEquals(isActive, timer.isActive());
    }

    public void testGetCreatedAt() {
        assertEquals(createdAt, timer.getCreatedAt());
    }

    public void testGetUid() {
        assertEquals(uid, timer.getUid());
    }

    public void testSetId() {
        String newId = "timer_999";
        timer.setId(newId);
        assertEquals(newId, timer.getId());
    }

    public void testTestSetName() {
        String newName = "Break";
        timer.setName(newName);
        assertEquals(newName, timer.getName());
    }

    public void testSetUid() {
        String newUid = "user_456";
        timer.setUid(newUid);
        assertEquals(newUid, timer.getUid());
    }

    public void testSetDuration() {
        int newDuration = 5;
        timer.setDuration(newDuration);
        assertEquals(newDuration, timer.getDuration());
    }

    public void testSetActive() {
        timer.setActive(false);
        assertFalse(timer.isActive());
    }

    public void testSetCreatedAt() {
        Timestamp newTimestamp = new Timestamp(new Date(0)); // Fecha época
        timer.setCreatedAt(newTimestamp);
        assertEquals(newTimestamp, timer.getCreatedAt());
    }

    public void testTestEquals() {
        // Creamos un segundo timer con los mismos datos exactos
        Timer timer2 = new Timer(id, name, duration, isActive, createdAt, uid);

        // Creamos un tercer timer con un ID distinto
        Timer timer3 = new Timer("otro_id", name, duration, isActive, createdAt, uid);

        assertTrue("Dos timers con los mismos datos deberían ser iguales", timer.equals(timer2));
        assertFalse("Timers con distinto ID no deberían ser iguales", timer.equals(timer3));
        assertFalse("No debería ser igual a un objeto nulo", timer.equals(null));
    }

    public void testTestHashCode() {
        Timer timer2 = new Timer(id, name, duration, isActive, createdAt, uid);

        assertEquals("Si dos objetos son iguales, su HashCode debe ser el mismo",
                timer.hashCode(), timer2.hashCode());
    }
}