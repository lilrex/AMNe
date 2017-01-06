package com.lilrex.amne.endpoint;

import org.junit.Assert;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class EndPointTest {

    // port out of range
    @Test(expected = EndPointConfigurationException.class)
    public void testEndPoint80() {
        EndPoint localhostEP = new EndPoint(80);
        Assert.assertNull(localhostEP);
    }

    @Test
    public void testEndPoint8080() {
        EndPoint localhostEP = new EndPoint(8080);
        Assert.assertNotNull(localhostEP);
    }

    @Test
    public void testEndPointLoopback() throws UnknownHostException {
        // Loopback address is easier to test than localhost address
        EndPoint ep = new EndPoint(InetAddress.getLoopbackAddress(), 15672, "cli");
        Assert.assertEquals("127.0.0.1:15672/cli", ep.toString());
    }

}
