package com.example.tickets_web;

import java.io.Serializable;
import java.util.Arrays;

import com.example.tickets_web.Backend;
/**
 * This class represents all of the data for a single event a user would
 * use. It will store the tickets for itself, track how many tickets have been created, and generate
 * new tickets for the event.
 */
public class Event implements Serializable {
    private String eventName;

    private int totalNumTickets;
    private int ticketsSold;
    private Ticket[] tickets;

    Event(String eventNameIn, int totalNumTicketsIn, Ticket[] ticketsIn) {
        eventName = eventNameIn;
        totalNumTickets = totalNumTicketsIn;
        tickets = ticketsIn;
        ticketsSold = 0;
    }


    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public int getTotalNumTickets() {
        return totalNumTickets;
    }

    public int getTicketsSold() {
        return ticketsSold;
    }

    /**
     * Get the next unsold ticket.
     * @return The Ticket object for the next undelivered ticket
     */
    public Ticket getNextUnsoldTicket() throws Exceptions.NoTicketsAvailableException {
        for(Ticket tic : tickets) {
            if (!tic.isSold()) {
                tic.setSold(true);
                ticketsSold++;
                return tic;
            }
        }
        throw new  Exceptions.NoTicketsAvailableException("No more tickets available for purchase");
    }

    /**
     * Refund a single ticket
     * <p>
     *     Given a hash, refund the ticket with the corresponding hash. Mark that ticket
     *     as unsold so we can sell it to the next person.
     * </p>
     * @param hash The data for the ticket
     * @return True if the refund was successful
     */
    public boolean refundTicket(byte[] hash) {
        for (Ticket tic : tickets) {
            if(tic.validateTicket(hash)) {
                tic.refund();
                ticketsSold--;
                return true;
            }
        }
        return false;
    }

    /**
     * Verify whether a single ticket is valid
     * <p>
     *     If the ticket doesn't exist in the database, or has been
     *     used before, it is an invalid ticket.
     * </p>
     * @param hash The data of the ticket to check for
     * @return True if the ticket is valid, false otherwise.
     */
    public boolean verifyTicket(byte[] hash) {
        for (Ticket tic : tickets) {
            if (tic.validateTicket(hash)) {
                return true;
            }
        }
        return false;
    }

    public void addTickets(int numNewTickets, String password) throws Exceptions.BadPasswordException {
        String concatPass = password + "0";
        byte[] hash = Backend.createHashFromPassword(concatPass);
        Ticket testTicket = tickets[0];
        if(!(hash == testTicket.getHash())) {
            throw new Exceptions.BadPasswordException("Bad password given");
        }

        int newSize = numNewTickets + totalNumTickets;
        Ticket[] newTicketBase = new Ticket[newSize];
        Ticket[] newTickets = Backend.createTicketHashes(password, numNewTickets, totalNumTickets);

        // Copy old tickets
        System.arraycopy(tickets, 0, newTicketBase, 0, totalNumTickets);
        // Copy new tickets
        for(int i = totalNumTickets; i < newSize; i++) {
            newTicketBase[i] = newTickets[totalNumTickets - i];
        }
        tickets = newTicketBase;
        totalNumTickets = newSize;
    }
}
