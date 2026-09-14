package au.nsw.servicefinder.service;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import au.nsw.servicefinder.model.ServiceRecord;
import au.nsw.servicefinder.repository.ServiceRepository;
import au.nsw.servicefinder.validation.ServiceValidator;

public class SearchServiceImpl implements SearchService {

    /*
     * Repository is responsible for loading
     * all service records from the JSON dataset.
     */
    private final ServiceRepository repository;

    /*
     * Validator is responsible for checking
     * whether a service record is valid.
     */
    private final ServiceValidator validator;

    /**
     * Creates the search service with the required dependencies.
     *
     * @param repository provides service records
     * @param validator validates service records
     */
    public SearchServiceImpl(
            ServiceRepository repository,
            ServiceValidator validator) {

        /*
         * requireNonNull ensures that the application
         * cannot create this service without the required
         * dependencies.
         */
        this.repository =
                Objects.requireNonNull(repository);

        this.validator =
                Objects.requireNonNull(validator);
    }

    /**
     * Searches service records using keyword and category filters
     * and returns one page of results.
     */
    @Override
    public PageResult<ServiceRecord> search(
            String keyword,
            String category,
            int page,
            int size) {

        /*
         * Load all service records from the repository.
         */
        List<ServiceRecord> services =
                repository.findAll();

        /*
         * Filter the records.
         *
         * 1. Remove invalid records
         * 2. Apply keyword search
         * 3. Apply category filtering
         */
        List<ServiceRecord> filteredServices =
                services.stream()
                        .filter(Objects::nonNull)
                        .filter(validator::isValid)
                        .filter(service ->
                                matchesKeyword(
                                        service,
                                        keyword
                                ))
                        .filter(service ->
                                matchesCategory(
                                        service,
                                        category
                                ))
                        .collect(Collectors.toList());

        /*
         * Calculate the total number of matching records.
         */
        int totalResults =
                filteredServices.size();

        /*
         * Calculate the total number of pages.
         */
        int totalPages =
                totalResults == 0
                        ? 0
                        : (int) Math.ceil(
                                (double) totalResults / size
                        );

        /*
         * Calculate where the requested page starts.
         *
         * Page numbering starts from 0.
         */
        int start =
                page * size;

        /*
         * If the requested page is beyond the
         * available results, return an empty page.
         */
        if (start >= totalResults) {

            return new PageResult<>(
                    List.of(),
                    page,
                    size,
                    totalResults,
                    totalPages
            );
        }

        /*
         * Calculate the end position without
         * going beyond the available results.
         */
        int end =
                Math.min(
                        start + size,
                        totalResults
                );

        /*
         * Extract only the records for
         * the requested page.
         */
        List<ServiceRecord> pageResults =
                filteredServices.subList(
                        start,
                        end
                );

        /*
         * Return the paginated search result.
         */
        return new PageResult<>(
                pageResults,
                page,
                size,
                totalResults,
                totalPages
        );
    }

    /**
     * Checks whether a service matches the keyword.
     *
     * The keyword is searched in both the service
     * title and description.
     */
    private boolean matchesKeyword(
            ServiceRecord service,
            String keyword) {

        /*
         * Normalize the search keyword.
         */
        String searchKeyword =
                normalize(keyword);

        /*
         * An empty keyword means all services
         * should match.
         */
        if (searchKeyword.isEmpty()) {
            return true;
        }

        /*
         * Normalize title and description before
         * performing a case-insensitive search.
         */
        String title =
                normalize(service.title());

        String description =
                normalize(service.description());

        return title.contains(searchKeyword)
                || description.contains(searchKeyword);
    }

    /**
     * Checks whether a service belongs to one of
     * the selected categories.
     *
     * Multiple categories are supported using
     * comma-separated values.
     *
     * Example:
     * Education,Health
     */
    private boolean matchesCategory(
            ServiceRecord service,
            String category) {

        /*
         * No category filter means all categories
         * should be included.
         */
        if (category == null
                || category.isBlank()) {

            return true;
        }

        /*
         * A service without a category cannot match
         * a selected category.
         */
        if (service.category() == null) {

            return false;
        }

        /*
         * Split the selected categories.
         *
         * Example:
         *
         * Education,Health
         *
         * becomes:
         *
         * Education
         * Health
         */
        String[] categories =
                category.split(",");

        /*
         * Check whether the service category matches
         * any selected category.
         */
        for (String selectedCategory :
                categories) {

            if (normalize(service.category())
                    .equals(
                            normalize(
                                    selectedCategory
                            )
                    )) {

                return true;
            }
        }

        return false;
    }

    /**
     * Normalizes text for case-insensitive comparisons.
     *
     * Null values become an empty string.
     * Leading and trailing spaces are removed.
     * Locale.ROOT provides consistent behaviour.
     */
    private String normalize(
            String value) {

        if (value == null) {
            return "";
        }

        return value
                .trim()
                .toLowerCase(Locale.ROOT);
    }
}