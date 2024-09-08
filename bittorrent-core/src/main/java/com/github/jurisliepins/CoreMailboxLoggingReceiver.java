package com.github.jurisliepins;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CoreMailboxLoggingReceiver implements MailboxReceiver {
    private final Logger logger;

    public CoreMailboxLoggingReceiver() {
        this.logger = LoggerFactory.getLogger(getClass().getName());
    }

    protected Logger logger() {
        return logger;
    }
}
