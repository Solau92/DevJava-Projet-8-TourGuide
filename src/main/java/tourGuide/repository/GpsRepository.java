package tourGuide.repository;

import gpsUtil.location.Attraction;

import java.util.List;

public interface GpsRepository {

	/**
	 * Returns the list of all attractions.
	 * @return a List of Attractions
	 */
	List<Attraction> getAllAttractions();
}
