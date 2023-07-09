package tourGuide.repository.implementation;

import gpsUtil.location.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Repository;
import tourGuide.exception.UserAlreadyExistsException;
import tourGuide.exception.UserNotFoundException;
import tourGuide.helper.UsersTestConfig;
import tourGuide.repository.UserRepository;
import tourGuide.user.User;
import tourGuide.user.UserPreferences;
import tourGuide.user.UserReward;
import tripPricer.Provider;

import java.util.*;

@Repository
@DependsOn("usersTestConfig")
public class UserRepositoryImpl implements UserRepository {

	private final Map<String, User> internalUserMap = new HashMap<>();
	private Logger logger = LoggerFactory.getLogger(UserRepositoryImpl.class);
	private Map<String, User> users;

	public UserRepositoryImpl() {
		this.users = new HashMap<>();
		if (UsersTestConfig.TEST_MODE) {
			logger.info("TestMode enabled");
			logger.debug("Initializing users");
			this.users = UsersTestConfig.initializeInternalUsers();
			logger.info("Finished initializing users");

		}
	}

	/**
	 * Returns a Map with all users in repository list.
	 *
	 * @return a Map<String, User>, in which users are identified by their userNames
	 */
	@Override
	public Map<String, User> getAllUsers() {
		return users;
	}

	/**
	 * Returns an Optional containing the user found in repository list, given the userName provided, and an empty Optional if the user was not found.
	 *
	 * @param userName
	 * @return Optional<User> containing the user found, or empty
	 * @throws UserNotFoundException
	 */
	@Override
	public Optional<User> getUserByUserName(String userName) throws UserNotFoundException {

		for (String s : users.keySet()) {
			if (s.equalsIgnoreCase(userName)) {
				return Optional.of(users.get(s));
			}
		}
		logger.error("User with userName " + userName + " was not found");
		return Optional.empty();
	}

	/**
	 * Adds to the repository users list the user given in parameter and returns an Optional containing the user saved, or empty if the user already exists.
	 *
	 * @param user
	 * @return Optional<User> containing the user added, or empty
	 * @throws UserAlreadyExistsException
	 */
	@Override
	public Optional<User> addUser(User user) {

		if (isUserAlreadyRegistered(user)) {
			logger.error("User with userName " + user.getUserName() + " already exists");
			return Optional.empty();
		}
		User userSaved = user;
		if (userSaved.getUserId() == null) {
			userSaved = new User(UUID.randomUUID(), user.getUserName(), user.getPhoneNumber(), user.getEmailAddress());
		}
		users.put(userSaved.getUserName(), userSaved);
		return Optional.of(userSaved);
	}

	/**
	 * Returns true if the user already exists in the repository list, false otherwise.
	 *
	 * @param user
	 * @return true if the user already exists in the repository list, false otherwise
	 */
	private boolean isUserAlreadyRegistered(User user) {
		return users.containsKey(user.getUserName());
	}

	/**
	 * Returns an Optional containing the last visited Location of the given user, empty if there is no Location registered for the user.
	 *
	 * @param user
	 * @return Optional<User> containing the last visited Location of the given user, empty if there is no Location registered for the user.
	 */
	@Override
	public Optional<Location> getUserLocation(User user) {

		if (user.getVisitedLocations().size() > 0) {
			return Optional.of(user.getLastVisitedLocation().location);
		}
		logger.info("User location not found for user " + user.getUserName());
		return Optional.empty();
	}

	/**
	 * Returns the last visited Location of all the users of the users repository list.
	 *
	 * @return Map<UUID, Location> containing for all the users their ID and their last visited Location
	 */
	@Override
	public Map<UUID, Location> getAllCurrentLocations() {

		Map<UUID, Location> currentLocations = new HashMap<>();

		for (User u : users.values()) {
			currentLocations.put(u.getUserId(), u.getLastVisitedLocation().location);
		}
		return currentLocations;
	}

	/**
	 * Returns a list of the UserRewards of the given user.
	 *
	 * @param userName
	 * @return List<Reward>
	 * @throws UserNotFoundException if user was not found in the users repository list
	 */
	@Override
	public List<UserReward> getUserRewards(String userName) throws UserNotFoundException {
		if (this.getUserByUserName(userName).isEmpty()) {
			logger.error("User with userName " + userName + " was not found");
			throw new UserNotFoundException("User with userName " + userName + " was not found");
		}
		return this.getUserByUserName(userName).get().getUserRewards();
	}

	/**
	 * Returns a list of Providers, corresponding to the trip deals for a given User.
	 *
	 * @param userName
	 * @return List<Provider>
	 * @throws UserNotFoundException if user was not found in the users repository list
	 */
	@Override
	public List<Provider> getTripDeals(String userName) throws UserNotFoundException {

		if (this.getUserByUserName(userName).isEmpty()) {
			logger.error("User with userName " + userName + " was not found");
			throw new UserNotFoundException("User with userName " + userName + " was not found");
		}
		return this.getUserByUserName(userName).get().getTripDeals();
	}

	//	@Override
	//	public UserPreferences setUserPreferences(User user, UserPreferences userPreferences) {
	//		user.setUserPreferences(userPreferences);
	//		return user.getUserPreferences();
	//	}

}
