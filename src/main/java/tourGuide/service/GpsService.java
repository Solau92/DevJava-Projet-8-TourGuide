package tourGuide.service;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import tourGuide.dto.NearByAttractionDto;
import tourGuide.exception.UserNotFoundException;

import java.util.List;
import java.util.UUID;

public interface GpsService {


	/**
	 * Returns the list of all the attractions.
	 *
	 * @return List<Attraction>
	 */
	List<Attraction> getAllAttractions();

	VisitedLocation getUserLocation(UUID userId);

	/**
	 * Returns a list of NearByAttractionDto representing the five nearest Attractions from the given User.
	 *
	 * @param userName
	 * @return List<NearByAttractionDto>
	 * @throws UserNotFoundException if the given user was not found
	 */
	List<NearByAttractionDto> getNearbyAttractions(String userName) throws UserNotFoundException;
}
