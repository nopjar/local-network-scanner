package io.github.dustincodes.localnetworkscanner;

import java.net.InetAddress;
import java.time.LocalTime;
import java.time.format.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        NetworkScanner scanner = new NetworkScanner();
        Timer timer = new Timer(false);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InetAddress mobilePhone = scanner.getInetAddressInLocalNetwork((byte) 21);
                if (mobilePhone == null) {
                    System.out.println("[" + currentTime() + "] Device is not in local network.");
                } else {
                    System.out.println("[" + currentTime() + "] Found " + mobilePhone.getHostName());
                }
            }
        }, 0, 5000);
    }

    private static String currentTime() {
        return LocalTime.now().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM));
    }



}
