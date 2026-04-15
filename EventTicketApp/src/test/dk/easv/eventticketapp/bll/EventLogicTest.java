package dk.easv.eventticketapp.bll;

import dk.easv.eventticketapp.be.Event;
import dk.easv.eventticketapp.dao.IEventDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EventLogicTest {

    private EventLogic eventLogic;

    @BeforeEach
    void setUp() {
        // 1. Create a Fake DAO (Mock) that acts like a database,
        // but doesn't actually do anything complex. It won't touch SQL.
        IEventDAO fakeDAO = new IEventDAO() {
            @Override
            public Event createEvent(Event event) { return event; } // Just returns what it received

            @Override public int getEventCount() { return 0; }
            @Override public List<Event> getAllEvents() { return null; }
            @Override public void updateEvent(Event event) { }
            @Override public List<Event> getEventsByIds(List<Integer> ids) { return null; }
            @Override public void deleteEvent(int id) { }
        };

        // 2. Inject the fake DAO into our Logic via the new constructor!
        eventLogic = new EventLogic(fakeDAO);
    }

    // --- BUSINESS RULE TESTS ---

    @DisplayName("Name is empty")
    @Test
    void createEvent_ShouldThrowException_WhenNameIsEmpty() {
        // ARRANGE: Create an event with an empty name
        Event badEvent = new Event(0, "", "Bratislava", LocalDateTime.now(), null, "Description", "Location", 100);

        // ACT & ASSERT: We expect our logic to throw an Exception
        Exception exception = assertThrows(Exception.class, () -> {
            eventLogic.createEvent(badEvent);
        });

        // Check if it threw the exact correct error message
        assertEquals("Event name is required", exception.getMessage());
    }

    @DisplayName("Zero tickets or less")
    @Test
    void createEvent_ShouldThrowException_WhenTicketsAreZeroOrLess() {
        // ARRANGE: Create an event with an invalid number of tickets (0)
        Event badEvent = new Event(0, "Super Concert", "Bratislava", LocalDateTime.now(), null, "Description", "Location", 0);

        // ACT & ASSERT
        Exception exception = assertThrows(Exception.class, () -> {
            eventLogic.createEvent(badEvent);
        });

        assertEquals("Max tickets must be greater than 0", exception.getMessage());
    }

    @DisplayName("Data validation")
    @Test
    void createEvent_ShouldSucceed_WhenAllDataIsValid() throws Exception {
        // ARRANGE: Create a perfect event that passes all rules
        Event goodEvent = new Event(0, "Perfect Concert", "Bratislava", LocalDateTime.now(), null, "Description", "Location", 500);

        // ACT: Pass it through the logic layer
        Event result = eventLogic.createEvent(goodEvent);

        // ASSERT: If the code reaches here without crashing, the logic works!
        assertNotNull(result, "Event should be successfully returned from the DAO");
        assertEquals("Perfect Concert", result.getName());
    }
}