package com.example.tickets_web;

public class Exceptions {

    public static class NoTicketsAvailableException extends Exception {
        public NoTicketsAvailableException(String message) {
            super(message);
        }
    }

    public static class EventNotFoundException extends Exception {
        public EventNotFoundException(String message) { super(message); }
    }
}
