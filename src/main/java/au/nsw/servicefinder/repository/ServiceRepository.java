package au.nsw.servicefinder.repository;

import java.util.List;
import au.nsw.servicefinder.model.ServiceRecord;

public interface ServiceRepository {
	List<ServiceRecord> findAll();
}