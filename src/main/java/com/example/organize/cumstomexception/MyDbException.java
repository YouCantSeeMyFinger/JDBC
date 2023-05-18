package com.example.organize.cumstomexception;

import lombok.extern.slf4j.Slf4j;

/**
 * UncheckExceptioin = RuntimeException
 *
 */

@Slf4j
public class MyDbException extends RuntimeException {

    public MyDbException() {
        super();
    }

    public MyDbException(String message) {
        super(message);
    }

    public MyDbException(String message, Throwable cause) {
        super(message, cause);
    }

    public MyDbException(Throwable cause) {
        super(cause);
    }
}
