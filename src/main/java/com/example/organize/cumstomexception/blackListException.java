package com.example.organize.cumstomexception;

public class blackListException extends RuntimeException {
    public blackListException() {
        super();
    }

    public blackListException(String message) {
        super(message);
    }

    public blackListException(String message, Throwable cause) {
        super(message, cause);
    }

    public blackListException(Throwable cause) {
        super(cause);
    }

}
