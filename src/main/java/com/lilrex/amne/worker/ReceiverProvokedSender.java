package com.lilrex.amne.worker;

import com.lilrex.amne.endpoint.EndPoint;

import java.io.IOException;
import java.net.InetAddress;

public class ReceiverProvokedSender extends DefaultMsgReceiver implements IMsgSender{

    protected IMsgSender sender;

    public ReceiverProvokedSender(
            String receiverExchangeName, String receiverQueueName, EndPoint receiverEndPoint,
            String senderExchangeName, String senderRoutingKey, EndPoint senderEndPoint,
            String username, String password) throws IOException {
        super(receiverExchangeName, receiverQueueName, receiverEndPoint, username, password);
        sender = new DefaultMsgSender(senderExchangeName, senderRoutingKey, senderEndPoint, username, password);
    }

    public ReceiverProvokedSender(String receiverExchangeName, String receiverQueueName, EndPoint receiverEndPoint,
                                  String username, String password) {
        super(receiverExchangeName, receiverQueueName, receiverEndPoint, username, password);
    }

    public void setSender(IMsgSender sender) {
        this.sender = sender;
    }

    @Override
    protected void processMessage(byte[] msg) {
        String message = new String(msg);
        System.out.println(message);
        send(msg);
    }
    
    public void send(byte[] data) {
        sender.send(data);
    }

    public static void main(String[] args) {
        EndPoint ep = new EndPoint(InetAddress.getLoopbackAddress(), 5672, "");
        ReceiverProvokedSender rps = null;
        try {
            IMsgSender sender = new DefaultMsgSender("downstream", "downstream", ep,
                    "guest", "guest");
            rps = new ReceiverProvokedSender("upstream", "upstream", ep,
                    "guest", "guest");
            rps.setSender(sender);
//            rps = new ReceiverProvokedSender(
//                    "upstream", "upstream", ep,
//                    "downstream", "downstream", ep,
//                    "guest", "guest");
            rps.addBindingKeys("upstream");
            rps.startListening();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
