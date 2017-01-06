package com.lilrex.amne.endpoint;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Stack;

/**
 * Represents an endpoint that can be connected to
 */
public final class EndPoint implements Serializable {
    public final InetAddress address;

    public final int port;

    public final String virtualHost;

    public EndPoint(final int port) {
        this((byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, port);
    }

    public EndPoint(final byte ipv4_0, final byte ipv4_1, final byte ipv4_2, final byte ipv4_3, final int port) {
        this(ipv4(ipv4_0, ipv4_1, ipv4_2, ipv4_3), port, "/");
    }

    public EndPoint(final InetAddress address, final int port, final String virtualHost) {
        if (address == null)
            throw new EndPointConfigurationException("Address cannot be null");
        if (address.isMulticastAddress())
            throw new EndPointConfigurationException("Address cannot be multicast");
        if (port < 1025 || port > 65535)
            throw new EndPointConfigurationException("Port must be between 1025 and 65535, inclusive.");
        this.address = address;
        this.port = port;

        String[] pieces = virtualHost.split("/");
        final Stack<String> validPieces = new Stack<String>();
        for (final String p : pieces) {
            if (p == null || p.length() == 0)
                continue;
            final String tp = p.trim();
            if (tp.length() == 0)
                continue;
            if (tp.contains("/"))
                throw new EndPointConfigurationException("Routes may not contain pieces with forward slashes!");
            if (tp.equals("..")) {
                if (!validPieces.isEmpty())
                    validPieces.pop();
            } else if (tp.equals(".")) {
                continue;
            } else {
                validPieces.push(p.toLowerCase().trim());
            }
        }
        this.virtualHost = "/" + String.join("/", validPieces);
    }

    private static InetAddress ipv4(final byte ipv4_0, final byte ipv4_1, final byte ipv4_2, final byte ipv4_3) {
        try {
            return InetAddress.getByAddress(new byte[] {
                    ipv4_0, ipv4_1, ipv4_2, ipv4_3
            });
        } catch (final UnknownHostException uhe) {
            return null;
        }
    }

    public String getHostAddress() {
        return address.getHostAddress();
    }

    public int getPort() {
        return port;
    }

    public String getVirtualHost() {
        return virtualHost;
    }

    public String toString() {
        return address.getHostAddress() + ":" + port + getVirtualHost();
    }
}
