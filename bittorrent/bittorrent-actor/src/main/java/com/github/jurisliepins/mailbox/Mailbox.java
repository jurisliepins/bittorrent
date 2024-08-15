package com.github.jurisliepins.mailbox;

import com.github.jurisliepins.ActorRef;

public sealed interface Mailbox permits MailboxSuccess, MailboxFailure {

    default <T> void reply(final T replyMessage) {
        switch (this) {
            case MailboxSuccess success -> success.sender().post(replyMessage);
            case MailboxFailure failure -> failure.sender().post(replyMessage);
        }
    }

    default <T> void reply(final T replyMessage, final ActorRef replySender) {
        switch (this) {
            case MailboxSuccess success -> success.sender().post(replyMessage, replySender);
            case MailboxFailure failure -> failure.sender().post(replyMessage, replySender);
        }
    }
}
