package com.loreweave.loreweave.exception;

public class CannotVoteOnOwnStoryPartException extends RuntimeException {
    public CannotVoteOnOwnStoryPartException() {
        super("You cannot vote on your own story part.");
    }
}
