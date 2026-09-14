package au.nsw.servicefinder.web;

import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;

import au.nsw.servicefinder.model.ServiceRecord;
import au.nsw.servicefinder.repository.ServiceRepository;
import au.nsw.servicefinder.repository.ServiceRepositoryImpl;
import au.nsw.servicefinder.service.PageResult;
import au.nsw.servicefinder.service.SearchService;
import au.nsw.servicefinder.service.SearchServiceImpl;
import au.nsw.servicefinder.validation.ServiceValidator;
import au.nsw.servicefinder.validation.ServiceValidatorImpl;
public class ServiceFinderServer {

    public static void start() throws Exception {

        ServiceRepository repository
               = new ServiceRepositoryImpl(
        new ObjectMapper(),
        Path.of("data.json"));

     ServiceValidator validator =
        new ServiceValidatorImpl();

SearchService searchService =
        new SearchServiceImpl(
                repository,
                validator
        );

        HttpServer server
                = HttpServer.create(
                        new InetSocketAddress(8080),
                        0
                );

        System.out.println(
                "Service Finder running at http://localhost:8080"
        );

        /*
         * ============================================================
         * API ENDPOINT
         * ============================================================
         *
         * When the browser requests:
         *
         * /api/services
         *
         * Java runs this code and returns service data as JSON.
         *
         * Example:
         *
         * /api/services?keyword=health&category=Health&page=0&size=10
         *
         */
        server.createContext("/api/services", exchange -> {

            String query
                    = exchange.getRequestURI().getQuery();

            System.out.println("Query: " + query);

            String keyword = "";
            String category = "";
            int page = 0;
            int size = 10;

            /*
             * Read query parameters.
             */
            if (query != null) {
                // System.out.println("QueryKeny: " + query);

                for (String parameter : query.split("&")) { // Split the query string into individual parameters using '&' as the delimiter

                    String[] parts
                            = parameter.split("=", 2); // Split the parameter into name and value, limit to 2 parts
                    System.out.println("QueryKeny1: " + parts[0] + " = " + parts[1]);
                    if (parts.length != 2) {
                        continue;
                    }

                    String name = parts[0];
                    System.out.println("QueryKeny2: " + name);
                    String value
                            = URLDecoder.decode(
                                    parts[1],
                                    StandardCharsets.UTF_8
                            );
                    System.out.println("QueryKeny3: " + value);
                    if (name.equals("keyword")) {

                        keyword = value;

                    } else if (name.equals("category")) {

                        category = value;

                    } else if (name.equals("page")) {

                        try {
                            page = Integer.parseInt(value);
                        } catch (NumberFormatException e) {

                            sendBadRequest(
                                    exchange,
                                    "Page must be a valid number."
                            );
                            return;
                        }

                    } else if (name.equals("size")) {

                        try {
                            size = Integer.parseInt(value);
                        } catch (NumberFormatException e) {

                            sendBadRequest(
                                    exchange,
                                    "Size must be a valid number."
                            );
                            return;
                        }
                    }

                }
            }

            System.out.println("Keyword: " + keyword);
            System.out.println("Category: " + category);
            System.out.println("Page: " + page);
            System.out.println("Size: " + size);

            if (page < 0 || size < 1 || size > 10) {

                sendBadRequest(
                        exchange,
                        "Invalid pagination. Page must be 0 or greater and size must be between 1 and 10."
                );

                return;
            }


            /*
             * Call the search service.
             *
             * The result contains:
             * - services for the current page
             * - current page number
             * - page size
             * - total number of results
             * - total number of pages
             */
            PageResult<ServiceRecord> result
                    = searchService.search(
                            keyword,
                            category,
                            page,
                            size
                    );

            /*
             * Convert the Java result into JSON.
             */
            String response
                    = new ObjectMapper()
                            .writeValueAsString(result);

            /*
             * Convert JSON string into bytes.
             */
            byte[] responseBytes
                    = response.getBytes(
                            StandardCharsets.UTF_8
                    );

            /*
             * Tell the browser that the response is JSON.
             */
            exchange.getResponseHeaders()
                    .set(
                            "Content-Type",
                            "application/json; charset=UTF-8"
                    );

            /*
             * Send HTTP 200 response.
             */
            exchange.sendResponseHeaders(
                    200,
                    responseBytes.length
            );

            /*
             * Send JSON to browser.
             */
            exchange.getResponseBody()
                    .write(responseBytes);

            exchange.close();
        });


        /*
         * ============================================================
         * INDEX.HTML
         * ============================================================
         *
         * When someone visits:
         *
         * http://localhost:8080/
         *
         * Java sends index.html to the browser.
         *
         */
        server.createContext("/", exchange -> {

            var resource
                    = ServiceFinderServer.class
                            .getClassLoader()
                            .getResourceAsStream("index.html");

            /*
             * If index.html cannot be found,
             * return HTTP 404.
             */
            if (resource == null) {

                String response
                        = "index.html not found";

                byte[] responseBytes
                        = response.getBytes(
                                StandardCharsets.UTF_8
                        );

                exchange.sendResponseHeaders(
                        404,
                        responseBytes.length
                );

                exchange.getResponseBody()
                        .write(responseBytes);

                exchange.close();

                return;
            }

            /*
             * Read index.html.
             */
            byte[] response
                    = resource.readAllBytes();

            /*
             * Tell browser this is HTML.
             */
            exchange.getResponseHeaders()
                    .set(
                            "Content-Type",
                            "text/html; charset=UTF-8"
                    );

            /*
             * Send HTTP 200.
             */
            exchange.sendResponseHeaders(
                    200,
                    response.length
            );

            /*
             * Send HTML to browser.
             */
            exchange.getResponseBody()
                    .write(response);

            exchange.close();
        });


        /*
         * ============================================================
         * STYLE.CSS
         * ============================================================
         *
         * When the browser requests:
         *
         * /style.css
         *
         * Java sends the CSS file.
         *
         */
        server.createContext("/style.css", exchange -> {

            var resource
                    = ServiceFinderServer.class
                            .getClassLoader()
                            .getResourceAsStream("style.css");

            /*
             * If style.css cannot be found,
             * return HTTP 404.
             */
            if (resource == null) {

                exchange.sendResponseHeaders(
                        404,
                        -1
                );

                exchange.close();

                return;
            }

            /*
             * Read CSS file.
             */
            byte[] response
                    = resource.readAllBytes();

            /*
             * Tell browser this is CSS.
             */
            exchange.getResponseHeaders()
                    .set(
                            "Content-Type",
                            "text/css"
                    );

            /*
             * Send HTTP 200.
             */
            exchange.sendResponseHeaders(
                    200,
                    response.length
            );

            /*
             * Send CSS to browser.
             */
            exchange.getResponseBody()
                    .write(response);

            exchange.close();
        });


        /*
         * ============================================================
         * SCRIPT.JS
         * ============================================================
         *
         * When the browser requests:
         *
         * /script.js
         *
         * Java sends the JavaScript file.
         *
         */
        server.createContext("/script.js", exchange -> {

            var resource
                    = ServiceFinderServer.class
                            .getClassLoader()
                            .getResourceAsStream("script.js");

            /*
             * If script.js cannot be found,
             * return HTTP 404.
             */
            if (resource == null) {

                exchange.sendResponseHeaders(
                        404,
                        -1
                );

                exchange.close();

                return;
            }

            /*
             * Read JavaScript file.
             */
            byte[] response
                    = resource.readAllBytes();

            /*
             * Tell browser this is JavaScript.
             */
            exchange.getResponseHeaders()
                    .set(
                            "Content-Type",
                            "application/javascript"
                    );

            /*
             * Send HTTP 200.
             */
            exchange.sendResponseHeaders(
                    200,
                    response.length
            );

            /*
             * Send JavaScript to browser.
             */
            exchange.getResponseBody()
                    .write(response);

            exchange.close();
        });


        /*
         * ============================================================
         * START SERVER
         * ============================================================
         */
        server.start();

    }

    private static void sendBadRequest(
            com.sun.net.httpserver.HttpExchange exchange,
            String errorMessage
    ) throws java.io.IOException {

        byte[] errorBytes
                = errorMessage.getBytes(
                        java.nio.charset.StandardCharsets.UTF_8
                );

        exchange.getResponseHeaders()
                .set(
                        "Content-Type",
                        "text/plain; charset=UTF-8"
                );

        exchange.sendResponseHeaders(
                400,
                errorBytes.length
        );

        exchange.getResponseBody()
                .write(errorBytes);

        exchange.close();
    }
}
