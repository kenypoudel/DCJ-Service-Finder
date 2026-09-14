package au.nsw.servicefinder;

import au.nsw.servicefinder.web.ServiceFinderServer;

public class Main {

    public static void main(String[] args) {

        try {
            ServiceFinderServer.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


