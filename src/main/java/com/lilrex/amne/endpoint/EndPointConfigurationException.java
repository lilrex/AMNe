package com.lilrex.amne.endpoint;

public class EndPointConfigurationException extends RuntimeException{

    EndPointConfigurationException(final String issue) {
        super(issue);
    }
}
