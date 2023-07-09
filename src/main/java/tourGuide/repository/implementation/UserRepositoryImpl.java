package tourGuide.repository.implementation;

import gpsUtil.location.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Repository;
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

	@Override
	public Map<String, User> getAllUsers() {
		return users;
	}

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

	private boolean isUserAlreadyRegistered(User user) {
		return users.containsKey(user.getUserName());
	}

	@Override
	public Optional<Location> getUserLocation(User user) {

		if (user.getVisitedLocations().size() > 0) {
			return Optional.of(user.getLastVisitedLocation().location);
		}
		logger.info("User location not found for user " + user.getUserName());
		return Optional.empty();
	}

	@Override
	public Map<UUID, Location> getAllCurrentLocations() {

		Map<UUID, Location> currentLocations = new HashMap<>();

		for (User u : users.values()) {
			currentLocations.put(u.getUserId(), u.getLastVisitedLocation().location);
		}
		return currentLocations;
	}

	@Override
	public List<UserReward> getUserRewards(String userName) throws UserNotFoundException {
		if (this.getUserByUserName(userName).isEmpty()) {
			logger.error("User with userName " + userName + " was not found");
			throw new UserNotFoundException("User with userName " + userName + " was not found");
		}
		return this.getUserByUserName(userName).get().getUserRewards();
	}

	@Override
	public List<Provider> getTripDeals(String userName) throws UserNotFoundException {

		if (this.getUserByUserName(userName).isEmpty()) {
			logger.error("User with userName " + userName + " was not found");
			throw new UserNotFoundException("User with userName " + userName + " was not found");
		}
		return this.getUserByUserName(userName).get().getTripDeals();
	}

	@Override
	public UserPreferences setUserPreferences(User user, UserPreferences userPreferences) {
		user.setUserPreferences(userPreferences);
		return user.getUserPreferences();
	}

}
