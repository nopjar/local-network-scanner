package io.github.dustincodes.localnetworkscanner;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * A simple class which returns InetAddresses connected to your local network.
 */
public class NetworkScanner {

    public NetworkScanner() {
    }

    /**
     * Scans the local network for all subnets (1 - 254).
     * <p>
     * This method will return any {@link InetAddress} which returns true with {@link InetAddress#isReachable(int)} by
     * a timeout from 5000 milliseconds.
     * <p>
     * {@link SocketException}s will be ignored as I have no idea when they are thrown and what they mean. If you know
     * it, let me know please.
     *
     * @return a list of reachable InetAddresses
     */
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

    /**
     * Tries to find the IP-Address with the specified subnet in your local network.
     * <p>
     * The subnet is for example: {@code xxx.xxx.xxx.21}
     * <p>
     * If you want to find a specific device (like your phone) over the ip-address, make sure your router always assigns
     * the same ip-address to your device. You can set this up in the interface of your router.
     *
     * @param subnet the subnet of the device to search for
     * @return the corresponding InetAddress of the subnet, may be null if ip-address is not reachable
     */
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
