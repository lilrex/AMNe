package com.lilrex.amne.endpoint;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Establish a RabbitMQ connection to a specific endpoint
 */
public class EndPointConnection {
    protected Connection connection;

    public EndPoint endpoint;

    public EndPointConnection(EndPoint endpoint,
                              String username,
                              String password) throws EndPointConnectionException {
        this.endpoint = endpoint;
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(endpoint.getHostAddress());
        factory.setPort(endpoint.getPort());
        factory.setVirtualHost(endpoint.getVirtualHost());
        factory.setUsername(username);
        factory.setPassword(password);
        try {
            connection = factory.newConnection();
        } catch (Exception e) {
            if(e instanceof TimeoutException) {
                throw new EndPointConnectionException("Connection to " + endpoint.toString() +
                        " with username: " + username + " timed out. Closing ...");
            } else {
                throw new EndPointConnectionException("Connection to " + endpoint.toString() +
                        " with username " + username + " unsuccessful. Closing ...");
            }
        }

    }

    public Connection getConnection() {
        return connection;
    }

    public void closeGracefully() throws IOException {
        connection.close();
    }
}
