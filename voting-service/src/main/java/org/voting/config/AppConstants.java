package org.voting.config;

public final class AppConstants {

    private AppConstants() { }

    public static final int DEFAULT_SESSION_DURATION = 1;
    public static final String ABBLE_TO_VOTE = "ABBLE_TO_VOTE";
    public static final String ERROR_AGENDA_NOT_FOUND = "Agenda not found.";
    public static final String ERROR_SESSION_ALREADY_EXISTS = "A voting session already exists for this agenda.";
    public static final String ERROR_SESSION_CLOSED = "Voting session is not open.";
    public static final String ERROR_SESSION_NOT_FOUND = "Voting session not found.";
    public static final String ERROR_DUPLICATE_VOTE = "You have already voted in this session.";
    public static final String ERROR_INVALID_CPF = "Invalid CPF.";
    public static final String ERROR_CPF_UNABLE_TO_VOTE = "User is not allowed to vote.";
    public static final String ERROR_SESSION_NOT_ENDED = "Unable to retrieve results: the voting session is still ongoing.";
    public static final String ERROR_KAFKA_CONNECT = "Failed to publish voting result on Kafka.";
}
