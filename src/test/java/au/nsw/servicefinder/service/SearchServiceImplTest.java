package au.nsw.servicefinder.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import au.nsw.servicefinder.model.ServiceRecord;
import au.nsw.servicefinder.validation.ServiceValidatorImpl;

class SearchServiceImplTest {

        private final List<ServiceRecord> services = List.of(
                        service(1, "Housing Assistance", "Housing", "Help with housing", "/housing"),
                        service(2, "Health Support", "HEALTH", "Health information", "/health"),
                        service(3, "Education Help", "Education", "Support for students", "/education"),
                        service(4, "Community Support", "housing", "Housing advice", "/community"),
                        service(5, "Legal Help", "Legal Aid", "Legal advice", ""));

        private final SearchService searchService = new SearchServiceImpl(
                        () -> services,
                        new ServiceValidatorImpl());

        @Test
        void searchesTitleAndDescriptionCaseInsensitively() {
                PageResult<ServiceRecord> result = searchService.search(
                                "HEALTH", "", 0, 10);

                assertEquals(List.of(2), ids(result));
        }

        @Test
        void filtersCategoriesCaseInsensitively() {
                PageResult<ServiceRecord> result = searchService.search(
                                "", "housing", 0, 10);

                assertEquals(List.of(1, 4), ids(result));
        }

        @Test
        void paginatesWithAtMostTenResults() {
                PageResult<ServiceRecord> result = searchService.search(
                                "", "", 0, 3);

                assertEquals(3, result.content().size());
                assertEquals(5, result.totalResults());
                assertEquals(2, result.totalPages());
        }

        @Test
        void returnsEmptyPageWhenPageIsBeyondResults() {
                PageResult<ServiceRecord> result = searchService.search(
                                "", "", 2, 3);

                assertEquals(List.of(), result.content());
                assertEquals(5, result.totalResults());
        }

        @Test
        void rejectsInvalidPagination() {
                assertThrows(IllegalArgumentException.class,
                                () -> searchService.search("", "", -1, 10));
                assertThrows(IllegalArgumentException.class,
                                () -> searchService.search("", "", 0, 11));
        }

        private static List<Integer> ids(PageResult<ServiceRecord> result) {
                return result.content().stream()
                                .map(ServiceRecord::id)
                                .toList();
        }

        private static ServiceRecord service(
                        int id,
                        String title,
                        String category,
                        String description,
                        String url) {
                return new ServiceRecord(
                                id,
                                title,
                                category,
                                "2026-01-01",
                                "9:00 AM - 5:00 PM",
                                url,
                                description);
        }
}