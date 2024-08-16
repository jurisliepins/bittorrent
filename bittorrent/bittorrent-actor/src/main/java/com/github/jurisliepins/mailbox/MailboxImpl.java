package com.github.jurisliepins.mailbox;

import com.github.jurisliepins.ActorRef;
import com.github.jurisliepins.ActorSystem;

import java.util.Objects;

public final class MailboxImpl implements Mailbox {

    private MailboxStatus status;
    private Object message;
    private ActorSystem system;
    private ActorRef self;
    private ActorRef sender;
    private Throwable cause;

    @Override
    public MailboxStatus status() {
        return status;
    }

    @Override
    public Object message() {
        return message;
    }

    @Override
    public ActorSystem system() {
        return system;
    }

    @Override
    public ActorRef self() {
        return self;
    }

    @Override
    public ActorRef sender() {
        return sender;
    }

    @Override
    public Throwable cause() {
        return cause;
    }

    public void setStatus(final MailboxStatus status) {
        this.status = Objects.requireNonNull(status, "status must not be null");
    }

    public void setMessage(final Object message) {
        this.message = Objects.requireNonNull(message, "message must not be null");
    }

    public void setSystem(final ActorSystem system) {
        this.system = Objects.requireNonNull(system, "system must not be null");
    }

    public void setSelf(final ActorRef self) {
        this.self = Objects.requireNonNull(self, "self must not be null");
    }

    public void setSender(final ActorRef sender) {
        this.sender = Objects.requireNonNull(sender, "sender must not be null");
    }

    public void setCause(final Throwable cause) {
        this.cause = cause;
    }
}
