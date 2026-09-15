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
 */
public final class ServiceRepositoryImpl
        implements ServiceRepository {

    /*
     * Jackson needs the generic type information to convert
     * the JSON array into a List<ServiceRecord>.
     */
    private static final TypeReference<List<ServiceRecord>> SERVICE_LIST = new TypeReference<>() {
    };

    /*
     * ObjectMapper is responsible for converting JSON data
     * into Java objects.
     */
    private final ObjectMapper objectMapper;

    /*
     * Path to the supplied service data file.
     *
     * Keeping this as a dependency allows the data location
     * to be changed without modifying the repository logic.
     */
    private final Path dataPath;

    /**
     * Creates the repository with the required dependencies.
     *
     * @param objectMapper Jackson JSON object mapper
     * @param dataPath path to the service data file
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

            /*
             * First check whether the data file exists
             * at the configured path.
             */
            if (Files.exists(dataPath)) {

                return read(
                        Files.newInputStream(dataPath));
            }

            /*
             * The expected data file could not be found,
             * so fail with a clear error message.
             */
            throw new IllegalStateException(
                    "Service data was not found at " + dataPath);

        } catch (IOException exception) {

            /*
             * Convert the low-level file error into a clear
             * application-level exception.
             */
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

/**
 * It means:
 * 
 * Open data.json → read the JSON → convert every JSON object into a
 * ServiceRecord → return them as a List.
 */