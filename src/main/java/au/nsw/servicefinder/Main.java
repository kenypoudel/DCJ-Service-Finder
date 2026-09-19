package au.nsw.servicefinder;

import au.nsw.servicefinder.web.ServiceFinderServer;

/**
 * The entry point for the Service Finder application.
 * Starts the Service Finder server on the default port (8080).
 */
public class Main {

    public static void main(String[] args) {

        try {
            ServiceFinderServer.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


