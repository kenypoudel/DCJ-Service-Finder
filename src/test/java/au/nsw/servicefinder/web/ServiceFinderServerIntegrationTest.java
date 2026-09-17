package au.nsw.servicefinder.web;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.sun.net.httpserver.HttpServer;

/**
 * Integration tests for the ServiceFinderServer class.
 */
class ServiceFinderServerIntegrationTest {

    private static HttpServer server;
    private static HttpClient client;
    private static String baseUrl;

    /**
     * Start the server before running the integration tests.
     */
    @BeforeAll
    static void startServer() throws Exception {

        server = ServiceFinderServer.start(0);

        int port = server.getAddress().getPort();

        baseUrl = "http://localhost:" + port;

        client = HttpClient.newHttpClient();
    }
    /*
     * Stop the server after running the integration tests.
        * This ensures that the server does not continue running
     */
    @AfterAll
    static void stopServer() {

        if (server != null) {
            server.stop(0);
        }
    }
    /**
     * Test that the /api/services endpoint returns a successful response.
     * This sends an actual HTTP request: GET /api/services?page=0&size=10
     * and checks that the response status code is 200 and the Content-Type header is application/json.
     * 
     */
    @Test
    void servicesEndpointReturnsSuccessfulResponse() throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/services?page=0&size=10"))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        assertTrue(
                response.headers()
                        .firstValue("Content-Type")
                        .orElse("")
                        .contains("application/json"));
    }

    /**
     * Test that the /api/services endpoint returns paginated results.
     * This sends an actual HTTP request: GET /api/services?page=0&size=3
     * and checks that the response status code is 200 and the response body contains the expected pagination information.
     */
    @Test
    void servicesEndpointReturnsPaginatedResults() throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/services?page=0&size=3"))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        assertTrue(response.body().contains("\"page\":0"));
        assertTrue(response.body().contains("\"size\":3"));
        assertTrue(response.body().contains("\"content\""));
    }
    /**
     * Test that the /api/services endpoint rejects invalid page size.
     * This sends an actual HTTP request: GET /api/services?page=0&size=11
     * and checks that the response status code is 400 and the response body contains the expected error message.
     */
    @Test
    void servicesEndpointRejectsInvalidPageSize() throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/services?page=0&size=11"))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());

        assertTrue(
                response.body().contains("size must be between 1 and 10"));
    }
    /**
     * Test that the /api/categories endpoint returns a successful response.
     * This sends an actual HTTP request: GET /api/categories
     * and checks that the response status code is 200 and the Content-Type header is application/json.
     */
    @Test
    void categoriesEndpointReturnsSuccessfulResponse() throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/categories"))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        assertTrue(
                response.headers()
                        .firstValue("Content-Type")
                        .orElse("")
                        .contains("application/json"));

        assertTrue(response.body().startsWith("["));
        assertTrue(response.body().endsWith("]"));
    }
}