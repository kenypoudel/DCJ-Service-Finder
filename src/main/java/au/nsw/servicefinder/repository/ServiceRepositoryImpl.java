package au.nsw.servicefinder.repository;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import au.nsw.servicefinder.model.ServiceRecord;

/**
 * Default implementation of the ServiceRepository.
 *
 * Responsible only for loading service records from the
 * supplied JSON data file.
 * Returns a list of ServiceRecord objects to the caller.
 * Conversion from JSON to ServiceRecord objects is handled
 * by the Jackson JSON library.
 */
public final class ServiceRepositoryImpl
        implements ServiceRepository {

    private static final TypeReference<List<ServiceRecord>> SERVICE_LIST = new TypeReference<>() {
    };

    private final ObjectMapper objectMapper;
    private final Path dataPath;

    /**
     * Creates the repository with the required dependencies.
     *
     * @param objectMapper Jackson JSON object mapper
     * @param dataPath     path to the service data file
     */
    public ServiceRepositoryImpl(
            ObjectMapper objectMapper,
            Path dataPath) {

        this.objectMapper = objectMapper;
        this.dataPath = dataPath;
    }

    /**
     * Loads all service records from the JSON dataset.
     *
     * @return list of service records
     */
    @Override
    public List<ServiceRecord> findAll() {

        try {

            if (Files.exists(dataPath)) {

                return read(
                        Files.newInputStream(dataPath));
            }

            throw new IllegalStateException(
                    "Service data was not found at " + dataPath);

        } catch (IOException exception) {

            throw new IllegalStateException(
                    "Unable to load service data",
                    exception);
        }
    }

    /**
     * Reads JSON data from the supplied input stream
     * and converts it into ServiceRecord objects.
     *
     * The try-with-resources statement automatically closes
     * the input stream after reading.
     * ServiceRecord objects are created using the Jackson JSON library.
     */
    private List<ServiceRecord> read(
            InputStream inputStream) throws IOException {

        try (inputStream) {

            return objectMapper.readValue(
                    inputStream,
                    SERVICE_LIST);
        }
    }
}
