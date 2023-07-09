package tourGuide.service;

import gpsUtil.location.Attraction;
import tourGuide.dto.NearByAttractionDto;
import tourGuide.exception.UserNotFoundException;

import java.util.List;

public interface GpsService {


	/**
	 * Returns the list of all the attractions.
	 *
	 * @return List<Attraction>
	 */
	List<Attraction> getAllAttractions();

	/**
	 * Returns a list of NearByAttractionDto representing the five nearest Attractions from the given User.
	 *
	 * @param userName
	 * @return List<NearByAttractionDto>
	 * @throws UserNotFoundException if the given user was not found
	 */
	List<NearByAttractionDto> getNearbyAttractions(String userName) throws UserNotFoundException;
}
