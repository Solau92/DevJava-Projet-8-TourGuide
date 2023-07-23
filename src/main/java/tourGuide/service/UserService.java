package tourGuide.service;

import gpsUtil.location.Location;
import tourGuide.dto.TripDealsPrefDto;
import tourGuide.exception.UserAlreadyExistsException;
import tourGuide.exception.UserNotFoundException;
import tourGuide.user.User;
import tourGuide.user.UserReward;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

import tripPricer.Provider;


public interface UserService {

	/**
	 * Returns a Map with all users.
	 *
	 * @return a Map<String, User>, in which users are identified by their userNames
	 */
	Map<String, User> getAllUsers();

	/**
	 * Returns an Optional containing the user found in repository list, given the userName provided, and an empty Optional if the user was not found.
	 *
	 * @param userName
	 * @return Optional<User> containing the user found, or empty
	 * @throws UserNotFoundException
	 */
	Optional<User> getUserByUserName(String userName) throws UserNotFoundException;

	/**
	 * Adds the user given in parameter and returns an Optional containing the user saved, or empty if the user already exists.
	 *
	 * @param user
	 * @return Optional<User> containing the user added, or empty
	 * @throws UserAlreadyExistsException if the user already exists
	 */
	Optional<User> addUser(User user) throws UserAlreadyExistsException;

	/**
	 * Returns an Optional containing the last visited Location of the given user, empty if there is no Location registered for the user.
	 * @param userName
	 * @return Optional<User> containing the last visited Location of the given user, empty if there is no Location registered for the user.
	 * @throws UserNotFoundException if the user was not found
	 */
	Optional<Location> getUserLocation(String userName) throws UserNotFoundException;

	/**
	 * Returns the last visited Location of all the users.
	 *
	 * @return Map<UUID, Location> containing for all the users their ID and their last visited Location
	 */
	Map<UUID, Location> getAllCurrentLocations();

	/**
	 * Returns a list of the UserRewards of the given user.
	 *
	 * @param userName
	 * @return List<UserReward>
	 * @throws UserNotFoundException if user was not found in the users repository list
	 */
	List<UserReward> getUserRewards(String userName) throws UserNotFoundException;

	/**
	 * Returns a list of Providers, corresponding to the trip deals for a given User.
	 *
	 * @param userName
	 * @return List<Provider>
	 * @throws UserNotFoundException if user was not found in the users repository list
	 */
	List<Provider> getTripDeals(String userName) throws UserNotFoundException;

	/**
	 * TODO
	 * @param tripDealsPrefDto
	 * @return
	 * @throws UserNotFoundException
	 */
	List<Provider>calculateTripDeals(TripDealsPrefDto tripDealsPrefDto) throws UserNotFoundException;
}
