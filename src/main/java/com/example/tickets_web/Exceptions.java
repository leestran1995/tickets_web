package com.example.tickets_web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class Exceptions {

    public static class NoTicketsAvailableException extends Exception {
        public NoTicketsAvailableException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class EventNotFoundException extends Exception {
        public EventNotFoundException(String message) { super(message); }
    }
}
