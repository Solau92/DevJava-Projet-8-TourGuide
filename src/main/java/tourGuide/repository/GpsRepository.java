package tourGuide.repository;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;

import java.util.List;
import java.util.UUID;

public interface GpsRepository {

	/**
	 * Returns the list of all attractions.
	 * @return a List of Attractions
	 */
	List<Attraction> getAllAttractions();

	/**
	 * Returns the location of a user, given his id.
	 *
	 * @param userId
	 * @return
	 */
	VisitedLocation getUserLocation(UUID userId);
}
