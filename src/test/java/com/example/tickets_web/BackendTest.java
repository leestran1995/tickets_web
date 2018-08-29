package com.example.tickets_web;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BackendTest {
    String username = "tyler";
    String eventName = "concert";
    String password = "hunter2";
    int numTickets = 20;

    @Test
    public void verifyTicket() {
        Backend b = new Backend();
        Event event = b.createAndSaveTickets(password, eventName, numTickets, username);
        try {
            Ticket tic = event.getNextUnsoldTicket();

            assert(event.verifyTicket(tic.getHash()));      // Initial ticket redemption
            assert(!event.verifyTicket(tic.getHash()));     // Should not be able to use a ticket twice
            byte[] hash = {1, -1, 4};
            Ticket badTicket = new Ticket(hash);
            assert(!event.verifyTicket(badTicket.getHash()));   // Trying to redeem a ticket that shouldn't exist
        } catch (Exceptions.NoTicketsAvailableException e) {
            System.out.println("No more available tickets");
        }


    }

    @Test
    public void refundTicket() {
        Backend b = new Backend();
        Event event = b.createAndSaveTickets(password, eventName, numTickets, username);
        try {
            Ticket tic = event.getNextUnsoldTicket();
            boolean isSold = tic.isSold();
            event.refundTicket(tic.getHash());

            assert(event.getTicketsSold() == 0);
            Ticket ticketAfterRefund = event.getNextUnsoldTicket();
            assert(tic.getHash() != ticketAfterRefund.getHash());   // Old ticket should not be valid anymore.
        } catch (Exceptions.NoTicketsAvailableException e) {
            System.out.println("No more available tickets");
        }
    }

    @Test
    public void createAndSavesTickets() {
    }
}