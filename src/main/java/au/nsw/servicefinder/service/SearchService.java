package au.nsw.servicefinder.service;

import au.nsw.servicefinder.model.ServiceRecord;

public interface SearchService {
/*
 * Searches for services based on the provided criteria.
 *
 * @param keyword   the search keyword
 * @param category  the service category
 * @param page      the page number (0-based)
 * @param size      the maximum number of results per page
 * @return          the page of search results
 */
    PageResult<ServiceRecord> search(
            String keyword,
            String category,
            int page,
            int size);
}