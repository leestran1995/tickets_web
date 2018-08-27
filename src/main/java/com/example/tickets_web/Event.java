package com.example.tickets_web;

/**
 * This class represents all of the data for a single event a user would
 * use. It will store the tickets for itself, track how many tickets have been created, and generate
 * new tickets for the event.
 */
public class Event {
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
}
