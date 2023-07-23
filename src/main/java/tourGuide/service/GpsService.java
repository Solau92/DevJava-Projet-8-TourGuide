package tourGuide.service;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import tourGuide.dto.NearByAttractionDto;
import tourGuide.exception.UserNotFoundException;
import tourGuide.user.User;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface GpsService {


	/**
	 * Returns the list of all the attractions.
	 *
	 * @return List<Attraction>
	 */
	List<Attraction> getAllAttractions();

	/**
	 * Returns the last visited location of a User, given his id.
	 *
	 * @param userId
	 * @return Location
	 */
	VisitedLocation getUserLocation(UUID userId);

	/**
	 * Returns a list of NearByAttractionDto representing the five nearest Attractions from the given User.
	 *
	 * @param userName
	 * @return List<NearByAttractionDto>
	 * @throws UserNotFoundException if the given user was not found
	 */
	List<NearByAttractionDto> getNearbyAttractions(String userName) throws UserNotFoundException;

	/**
	 * Returns the last visited location of a User, given his userName.
	 *
	 * @param userName
	 * @return Location
	 * @throws UserNotFoundException if the User was not found
	 */
	Location getUserLocation(String userName) throws UserNotFoundException;

	/**
	 * Searches the Location of a given User, add it to his visited location and calculates the associate Reward.
	 *
	 * @param user
	 * @return the visited Location added
	 */
	VisitedLocation trackUserLocation(User user);

	/**
	 * Returns the last visited Location of all the Users.
	 *
	 * @return Map<UUID, Location> containing for all the User, their ID and last visited Location
	 * @throws UserNotFoundException if the User was not found
	 */
	Map<UUID, Location> getAllCurrentLocations() throws UserNotFoundException;

	/**
	 * For all the Users, track the User location (cf. method trackUserLocation)
	 */
	void trackAllUsersLocationOnce();
}
