package com.lilrex.amne.worker;


import com.lilrex.amne.endpoint.EndPoint;
import com.lilrex.amne.endpoint.EndPointConnection;
import com.lilrex.amne.endpoint.EndPointConnectionException;

import java.io.IOException;

/**
 * A worker is something that can communicate with an endpoint
 */
public abstract class Worker {

    protected static final String DEFAULT_EXCHANGE = "";

    protected String exchangeName = DEFAULT_EXCHANGE;
    protected String queueName;

    protected EndPointConnection epc;

    public Worker(String exchangeName, String queueName) {
        this.exchangeName = exchangeName;
        this.queueName = queueName;
    }

    public Worker(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public void start(EndPoint endpoint, String username, String password) {
        try {
            epc = new EndPointConnection(endpoint, username, password);
        } catch (EndPointConnectionException e) {
            try {
                if (epc != null) {
                    System.out.println("Closing the Endpoint Connecting");
                    epc.closeGracefully();
                }
                System.err.println(e.getMessage());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }
}
