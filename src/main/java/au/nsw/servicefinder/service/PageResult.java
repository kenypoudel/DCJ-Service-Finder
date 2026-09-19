package au.nsw.servicefinder.service;

import java.util.List;

/**
 * Represents a page of results from a search query.
 * @param <T> the type of the content in the page
 * @param content the list of results on this page
 * @param page the current page number (0-based)
 * @param size the maximum number of results per page
 * @param totalResults the total number of matching results
 * @param totalPages the total number of pages
 * Note: page numbers are 0-based, so:
 * Page 0 → results 1–10
 * Page 1 → results 11–20
 * ...
 * Returns a PageResult object containing the content, page number, 
 * size, total results, and total pages.
 */
public record PageResult<T>(
                List<T> content,
                int page,
                int size,
                int totalResults,
                int totalPages) {
}

