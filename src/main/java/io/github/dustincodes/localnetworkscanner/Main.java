package io.github.dustincodes.localnetworkscanner;

import java.net.InetAddress;
import java.time.LocalTime;
import java.time.format.*;
import java.util.*;

public class Main {

    private static final byte SEARCHED_SUBNET = 21;
    private static final long DEVICE_LOOKUP_PERIOD = 5000;

    private static NetworkScanner scanner;

    public static void main(String[] args) {
        log("Starting networkscanner...");
        Runtime.getRuntime().addShutdownHook(new Thread(Main::shutdown));
        scanner = new NetworkScanner();

        log("Scanning for reachable subnets in network...");
        List<InetAddress> foundInetAddresses = scanner.scanLocalNetwork();
        log("Addresses found:  " + foundInetAddresses.size());

        log("Starting timerthread for device lookup...");
        startLookupTimer();
    }

    private static void startLookupTimer() {
        Timer timer = new Timer(false);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                log("Searching for subnet " + SEARCHED_SUBNET + "...");
                InetAddress mobilePhone = scanner.getInetAddressInLocalNetwork(SEARCHED_SUBNET);
                if (mobilePhone == null) {
                    log("Device is not in local network.");
                } else {
                    log("Found " + mobilePhone.getHostName());
                }
            }
        }, 0L, DEVICE_LOOKUP_PERIOD);
    }

    private static String currentTime() {
        return LocalTime.now().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM));
    }

    private static void shutdown() {
        log("Exiting networkscanner...");
    }

    private static void log(String message) {
        System.out.println("[" + currentTime() + "] " + message);
    }

}
