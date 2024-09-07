package com.github.jurisliepins;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CoreMailboxReceiver implements MailboxReceiver {
    private final Logger logger;

    public CoreMailboxReceiver() {
        this.logger = LoggerFactory.getLogger(getClass().getName());
    }

    protected Logger logger() {
        return logger;
    }
}
