package tourGuide.service;

import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import tourGuide.dto.TripDealsPrefDto;
import tourGuide.exception.UserNotFoundException;
import tourGuide.user.User;
import tripPricer.Provider;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface TourGuideService {

	/**
	 * Returns the last visited location of a User, given his userName.
	 *
	 * @param userName
	 * @return Location
	 * @throws UserNotFoundException if the User was not found
	 */
	Location getUserLocation(String userName) throws UserNotFoundException;

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

	/**
	 * Searches the Location of a given User, add it to his visited location and calculates the associate Reward.
	 *
	 * @param user
	 * @return the visited Location added
	 */
	VisitedLocation trackUserLocation(User user);

	/**
	 * Returns a list of Provider corresponding to the trip deals for a given User and his trip UserPreferences.
	 * @param tripDealsPrefDto
	 * @return List<Provider>
	 * @throws UserNotFoundException if the User was not found
	 */
	List<Provider> getTripDeals(TripDealsPrefDto tripDealsPrefDto) throws UserNotFoundException;

}
