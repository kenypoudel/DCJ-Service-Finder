package au.nsw.servicefinder.service;

import au.nsw.servicefinder.model.ServiceRecord;

public interface SearchService {

    PageResult<ServiceRecord> search(
            String keyword,
            String category,
            int page,
            int size
    );
}