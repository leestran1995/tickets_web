package com.example.tickets_web;

import org.junit.Test;

import static org.junit.Assert.*;

public class EventTest {
    String username = "tyler";
    String eventName = "concert";
    String password = "hunter2";
    int numTickets = 20;

    @Test
    public void addTickets() {
        Backend b = new Backend();
        Event event = b.createAndSaveTickets(password, eventName, numTickets, username);
        assert(event.getTotalNumTickets() == numTickets);
        try {
            event.addTickets(numTickets, password);
            assert(event.getTotalNumTickets() == numTickets * 2);
        } catch (Exceptions.BadPasswordException e) {
            e.printStackTrace();
        }
    }
}