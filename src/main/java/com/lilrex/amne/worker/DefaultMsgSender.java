package com.lilrex.amne.worker;

import com.lilrex.amne.endpoint.EndPoint;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.net.InetAddress;

public class DefaultMsgSender extends Worker implements IMsgSender {

    private static final String DEFAULT_ROUTING_KEY = "";

    private Channel channel;
    private String routingKey = DEFAULT_ROUTING_KEY;

    public DefaultMsgSender(String exchangeName, EndPoint endpoint, String username, String password) throws IOException {
        super(exchangeName);
        start(endpoint, username, password);
        channel = epc.getConnection().createChannel();
        // default exchange type is "fanout" when an empty routing key is used
        channel.exchangeDeclare(exchangeName, "fanout", true, false, null);
    }

    public DefaultMsgSender(String exchangeName, String routingKey, EndPoint endpoint, String username, String password) throws IOException {
        super(exchangeName);
        start(endpoint, username, password);
        channel = epc.getConnection().createChannel();
        setRoutingKey(routingKey);
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
        try {
            if(routingKey == "") {
                channel.exchangeDeclare(exchangeName, "fanout", true, false, null);
            } else {
                channel.exchangeDeclare(exchangeName, "direct", true, false, null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(byte[] data) {
        try {
            channel.basicPublish(exchangeName, routingKey, null, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        EndPoint ep = new EndPoint(InetAddress.getLoopbackAddress(), 5672, "");
        try {
            DefaultMsgSender sender = new DefaultMsgSender("test", ep, "guest", "guest");
//            sender.setRoutingKey("test");
            sender.send(new String("test").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
