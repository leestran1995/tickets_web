package com.example.tickets_web;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Ticket
 *
 * This class is used to represent each ticket that is created for an
 * event.
 */
public class Ticket implements Serializable {
    private byte[] hash;    // The hash value of the ticket
    private boolean used;   // Whether the ticket has been used yet
    private boolean sold;   // Whether the ticket has been sold yet

    public Ticket(byte[] hash_input) {
        hash = hash_input;
        used = false;
        sold = false;
    }

    public boolean validateTicket(byte[] hash_input) {
        if(used) {
            return false;
        }
        else if (Arrays.equals(hash_input, hash)) {
            used = true;
            return true;
        }
        else {
            return false;
        }
    }


    public byte[] getHash() {
        return hash;
    }

    public boolean isUsed() {
        return used;
    }

    public boolean isSold() {
        return sold;
    }

    public void setSold(boolean sold) {
        this.sold = sold;
    }

    public void refund() {
        used = false;
        sold = false;
    }

    // We don't need Setters since the hash value of a ticket should never change and
    // the ticket being used only happens when we check to validate it.
}

