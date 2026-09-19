package au.nsw.servicefinder.repository;

import java.util.List;

import au.nsw.servicefinder.model.ServiceRecord;
/**
 * Repository interface for accessing service records.
 * Provides a method to retrieve all service records from the data source.
 * Returns a list of ServiceRecord objects to the caller.
 */
public interface ServiceRepository {
	List<ServiceRecord> findAll();
}