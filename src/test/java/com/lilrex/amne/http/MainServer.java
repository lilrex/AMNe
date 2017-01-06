package com.lilrex.amne.http;

import com.lilrex.amne.endpoint.EndPoint;
import com.lilrex.amne.worker.ReceiverProvokedSender;

import java.io.IOException;
import java.net.InetAddress;

public class MainServer {

    public static void main(String[] args) {
        EndPoint ep = new EndPoint(InetAddress.getLoopbackAddress(), 5672, "");
        ReceiverProvokedSender sender = null;
        try {
            sender = new ReceiverProvokedSender(
                    "upstream", "upstream", ep,
                    "downstream", "downstream", ep,
                    "guest", "guest");
            sender.addBindingKeys("upstream");
            sender.startListening();
        } catch (IOException e) {
            e.printStackTrace();
        }

        AMNeServer server = new AMNeServer(sender);
        server.doStart(18866);
    }

}
