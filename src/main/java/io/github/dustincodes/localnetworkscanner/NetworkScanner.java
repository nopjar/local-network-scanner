package io.github.dustincodes.localnetworkscanner;

import java.io.IOException;
import java.net.*;

public class NetworkScanner {

    public NetworkScanner() {
    }

    public void scanLocalNetwork() {
        final byte[] ip = getLocalIp();
        if (ip == null) {
            return;
        }

        for (int i = 1; i <= 254; i++) {
            final int j = i;
            new Thread(() -> {
                try {
                    ip[3] = (byte) j;
                    InetAddress address = InetAddress.getByAddress(ip);
                    String output = address.toString().substring(1);
                    if (address.isReachable(5000)) {
                        System.out.println(output + " is on the network");
                    } else {
                        System.out.println("Not reachable: " + output);
                    }
                } catch (IllegalArgumentException | IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public InetAddress getInetAddressInLocalNetwork(byte subnet) {
        final byte[] ip = getLocalIp();
        if (ip == null) {
            return null;
        }
        ip[3] = subnet;

        try {
            InetAddress address = InetAddress.getByAddress(ip);
            if (address.isReachable(2000)) {
                return address;
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private byte[] getLocalIp() {
        try {
            return InetAddress.getLocalHost().getAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
    }
}
