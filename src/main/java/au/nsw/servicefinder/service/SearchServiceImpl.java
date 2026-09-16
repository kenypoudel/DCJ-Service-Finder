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
         * @param validator  validates service records
         */
        public SearchServiceImpl(
                        ServiceRepository repository,
                        ServiceValidator validator) {

                /*
                 * requireNonNull ensures that the application
                 * cannot create this service without the required
                 * dependencies.
                 */
                this.repository = Objects.requireNonNull(repository);

                this.validator = Objects.requireNonNull(validator);
        }

        /**
         * Searches service records using keyword and category filters
         * and returns one page of results.
         * 
         * Also calculate the total number of pages.
         */
        @Override
        public PageResult<ServiceRecord> search(
                        String keyword,
                        String category,
                        int page,
                        int size) {

                if (page < 0 || size < 1 || size > 10) {
                        throw new IllegalArgumentException(
                                        "Page must be 0 or greater and size must be between 1 and 10.");
                }

  
                List<ServiceRecord> services = repository.findAll();

                List<ServiceRecord> filteredServices = services.stream()
                                .filter(Objects::nonNull)
                                .filter(validator::isValid)
                                .filter(service -> matchesKeyword(
                                                service,
                                                keyword))
                                .filter(service -> matchesCategory(
                                                service,
                                                category))
                                                                // .peek(service -> System.out.println(
                                                                //                 "Matched service: " + service.title()))
                                .collect(Collectors.toList());

               
                int totalResults = filteredServices.size();


                int totalPages = totalResults == 0
                                ? 0
                                : (int) Math.ceil(
                                                (double) totalResults / size);

             
                int start = page * size; // start position of the requested page

             
                if (start >= totalResults) {

                        return new PageResult<>(
                                        List.of(),
                                        page,
                                        size,
                                        totalResults,
                                        totalPages);
                }

             
                int end = Math.min(
                                start + size,
                                totalResults); // end position of the requested page

             
                List<ServiceRecord> pageResults = filteredServices.subList(
                                start,
                                end);

                
                return new PageResult<>(
                                pageResults,
                                page,
                                size,
                                totalResults,
                                totalPages);
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

              
                String searchKeyword = normalize(keyword);

                
                if (searchKeyword.isEmpty()) {
                        return true;
                }

              
                String title = normalize(service.title());

                String description = normalize(service.description());

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

                
                if (category == null
                                || category.isBlank()) {

                        return true;
                }

             
                if (service.category() == null) {

                        return false;
                }

               
                String[] categories = category.split(",");

                for (String selectedCategory : categories) {

                        if (normalize(service.category())
                                        .equals(
                                                        normalize(
                                                                        selectedCategory))) {

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