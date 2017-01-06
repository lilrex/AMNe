package com.lilrex.amne.endpoint;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class EndPointConnectionTest {

    @Test
    public void testEndPointConnection() throws UnknownHostException{
        EndPointConnection epc = null;
        EndPoint ep = new EndPoint(InetAddress.getLoopbackAddress(), 5672, "");
        try {
            epc = new EndPointConnection(ep, "guest", "guest");
            Assert.assertNotNull(epc);
            Assert.assertNotNull(epc.getConnection());
        } catch (EndPointConnectionException e) {
            e.printStackTrace();
        } finally {
            try {
                if(epc != null)
                    epc.closeGracefully();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
