package au.nsw.servicefinder.service;

import java.util.List;

public record PageResult<T>(
        List<T> content,
        int page,
        int size,
        int totalResults,
        int totalPages
) {
}

/*Page 0 → results 1–10
Page 1 → results 11–20
Page 2 → results 21–23

The PageResult keeps information about:

content       → results on this page
page          → current page
size          → maximum results per page
totalResults  → total matching results
totalPages    → number of pages*/