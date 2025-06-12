package org.voting.config;

public final class AppConstants {

    private AppConstants() { }

    public static final int DEFAULT_SESSION_DURATION = 1;
    public static final String ERROR_AGENDA_NOT_FOUND = "Agenda not found.";
    public static final String ERROR_SESSION_ALREADY_EXISTS = "A voting session already exists for this agenda.";
    public static final String ERROR_SESSION_CLOSED = "Voting session is not open.";
    public static final String ERROR_SESSION_NOT_FOUND = "Voting session not found.";
    public static final String ERROR_DUPLICATE_VOTE = "You have already voted in this session.";
}
