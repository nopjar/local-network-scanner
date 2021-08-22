package io.github.dustincodes.localnetworkscanner;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class NetworkScanner {

    public NetworkScanner() {
    }

    public List<InetAddress> scanLocalNetwork() {
        final List<InetAddress> reachableAddresses = new ArrayList<>();
        final byte[] ip = getLocalIp();
        final ExecutorService service;
        if (ip == null) {
            return reachableAddresses;
        }

        service = Executors.newCachedThreadPool();
        for (int i = 1; i <= 254; i++) {
            final int j = i;
            service.execute(() -> {
                try {
                    ip[3] = (byte) j;
                    InetAddress address = InetAddress.getByAddress(ip);
                    if (address.isReachable(5000)) {
                        reachableAddresses.add(address);
                    }
                } catch (SocketException e) {
                    // ignore
                } catch (IllegalArgumentException | IOException e) {
                    e.printStackTrace();
                }
            });
        }
        service.shutdown();
        try {
            service.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
            service.shutdownNow();
        }

        return reachableAddresses;
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
