package com.lilrex.amne.worker;

import com.lilrex.amne.endpoint.EndPoint;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class DefaultMsgReceiver extends Worker implements IMsgReceiver {

//    private static final Logger logger = LoggerFactory.getLogger(DefaultMsgReceiver.class);

    private EndPoint endpoint;
    private String username;
    private String password;
    private List<String> bindingKeys;

    public DefaultMsgReceiver(String exchangeName, String queueName, EndPoint endpoint,
                              String username, String password) {
        super(exchangeName, queueName);
        this.endpoint = endpoint;
        this.username = username;
        this.password = password;
        bindingKeys = new ArrayList<>();
    }

    public void addBindingKeys(String... keys) {
        for(String key : keys) {
            bindingKeys.add(key);
        }
    }

    public List<String> getBindingKeys() {
        return bindingKeys;
    }

    public void startListening() {
        start(endpoint, username, password);
        if(epc != null) {
//            logger.info("Connection to " + endpoint.toString() + " established, starting to listen ...");
            Thread consumerThread = new Thread() {
                @Override
                public void run() {
                    try {
                        final Channel channel = epc.getConnection().createChannel();

                        // when no binding keys are defined the exchange is usually "fanout"
                        if(bindingKeys.size() == 0) {
                            channel.exchangeDeclare(exchangeName, "fanout", true, false, null);
                            channel.queueDeclare(queueName, true, false, false, null);
                            channel.queueBind(queueName, exchangeName, "");
                        } else {
                            channel.exchangeDeclare(exchangeName, "direct", true, false, null);
                            channel.queueDeclare(queueName, true, false, false, null);
                            for (String bindingKey : bindingKeys) {
                                channel.queueBind(queueName, exchangeName, bindingKey);
                            }
                        }

                        channel.basicQos(1);

                        QueueingConsumer queueingConsumer = new QueueingConsumer(channel);

                        channel.basicConsume(queueName, false, queueingConsumer);
                        while (true) {
                            final QueueingConsumer.Delivery delivery = queueingConsumer
                                    .nextDelivery();
                            processMessage(delivery.getBody());
                            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            };
            consumerThread.setDaemon(true);
            consumerThread.start();
        }
    }

    protected void processMessage(byte[] msg) {
        String message = new String(msg);
        System.out.println(message);
    }

    public static void main(String[] args) {
        EndPoint ep = new EndPoint(InetAddress.getLoopbackAddress(), 5672, "");
        DefaultMsgReceiver receiver = new DefaultMsgReceiver("test", "test", ep, "guest", "guest");
//        receiver.addBindingKeys("downstream");
        receiver.startListening();
    }
}
