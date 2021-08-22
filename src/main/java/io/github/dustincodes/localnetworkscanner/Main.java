package io.github.dustincodes.localnetworkscanner;

import java.net.InetAddress;
import java.time.LocalTime;
import java.time.format.*;
import java.util.*;

public class Main {

    private static final byte SEARCHED_SUBNET = 21;
    private static final long DEVICE_LOOKUP_PERIOD = 5000;

    public static void main(String[] args) {
        System.out.println("[" + currentTime() + "] Starting networkscanner...");
        Runtime.getRuntime().addShutdownHook(new Thread(Main::shutdown));
        NetworkScanner scanner = new NetworkScanner();

        System.out.println("[" + currentTime() + "] Scanning for reachable subnets in network...");
        List<InetAddress> foundInetAddresses = scanner.scanLocalNetwork();
        System.out.println("[" + currentTime() + "] Addresses found:  " + foundInetAddresses.size());

        System.out.println("[" + currentTime() + "] Starting timerthread for device lookup...");
        Timer timer = new Timer(false);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("[" + currentTime() + "] Searching for subnet " + SEARCHED_SUBNET + "...");
                InetAddress mobilePhone = scanner.getInetAddressInLocalNetwork(SEARCHED_SUBNET);
                if (mobilePhone == null) {
                    System.out.println("[" + currentTime() + "] Device is not in local network.");
                } else {
                    System.out.println("[" + currentTime() + "] Found " + mobilePhone.getHostName());
                }
            }
        }, 0L, DEVICE_LOOKUP_PERIOD);
    }

    private static String currentTime() {
        return LocalTime.now().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM));
    }

    private static void shutdown() {
        System.out.println("[" + currentTime() + "] Exiting networkscanner...");
    }

}
