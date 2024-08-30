package com.github.jurisliepins;

import com.github.jurisliepins.info.InfoHash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CoreLoggingMailboxReceiver implements MailboxReceiver {
    private final Logger logger;

    public CoreLoggingMailboxReceiver() {
        this.logger = LoggerFactory.getLogger(getClass().getName());
    }

    public CoreLoggingMailboxReceiver(final InfoHash infoHash) {
        this.logger = LoggerFactory.getLogger(getClass().getName() + " (%s)".formatted(infoHash));
    }

    protected Logger logger() {
        return logger;
    }
}
