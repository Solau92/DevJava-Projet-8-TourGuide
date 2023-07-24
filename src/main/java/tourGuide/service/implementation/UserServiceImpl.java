package tourGuide.service.implementation;

import gpsUtil.location.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tourGuide.dto.TripDealsPrefDto;
import tourGuide.exception.UserAlreadyExistsException;
import tourGuide.exception.UserNotFoundException;
import tourGuide.repository.implementation.UserRepositoryImpl;
import tourGuide.service.UserService;
import tourGuide.user.User;
import tourGuide.user.UserPreferences;
import tourGuide.user.UserReward;
import tripPricer.Provider;
import tripPricer.TripPricer;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

	private final TripPricer tripPricer = new TripPricer();
	private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	private UserRepositoryImpl userRepository;
	@Value("${tourGuide.tripPricerApiKey}")
	private String tripPricerApiKey;

	public UserServiceImpl(UserRepositoryImpl userRepository) {
		this.userRepository = userRepository;
	}

	/**
	 * Returns a Map with all users.
	 *
	 * @return a Map<String, User>, in which users are identified by their userNames
	 */
	@Override
	public Map<String, User> getAllUsers() {
		return userRepository.getAllUsers();
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

		if (userRepository.getUserByUserName(userName).isEmpty()) {
			logger.error("User with userName " + userName + " was not found");
			throw new UserNotFoundException("User with userName " + userName + " was not found");
		} else {
			return userRepository.getUserByUserName(userName);
		}
	}

	/**
	 * Adds the user given in parameter and returns an Optional containing the user saved, or empty if the user already exists.
	 *
	 * @param user
	 * @return Optional<User> containing the user added, or empty
	 * @throws UserAlreadyExistsException if the user already exists
	 */
	@Override
	public Optional<User> addUser(User user) throws UserAlreadyExistsException {

		Optional<User> optionalUser = userRepository.addUser(user);

		if (optionalUser.isEmpty()) {
			logger.error("User not registered : user with userName " + user.getUserName() + " already exists");
			throw new UserAlreadyExistsException("User not registered : user with userName " + user.getUserName() + " already exists");
		}
		return optionalUser;
	}

	/**
	 * Returns an Optional containing the last visited Location of the given user, empty if there is no Location registered for the user.
	 *
	 * @param userName
	 * @return Optional<User> containing the last visited Location of the given user, empty if there is no Location registered for the user.
	 * @throws UserNotFoundException if the user was not found
	 */
	@Override
	public Optional<Location> getUserLocation(String userName) throws UserNotFoundException {

		User user = this.getUserByUserName(userName).get();
		Optional<Location> userLocation = userRepository.getUserLocation(user);

		if (!userLocation.isEmpty()) {
			return userLocation;
		}
		logger.info("User location not found");
		return Optional.empty();
	}

	/**
	 * Returns the last visited Location of all the users.
	 *
	 * @return Map<UUID, Location> containing for all the users their ID and their last visited Location
	 */
	@Override
	public Map<UUID, Location> getAllCurrentLocations() {
		return userRepository.getAllCurrentLocations();
	}

	/**
	 * Returns a list of the UserRewards of the given user.
	 *
	 * @param userName
	 * @return List<UserReward>
	 * @throws UserNotFoundException if user was not found in the users repository list
	 */
	@Override
	public List<UserReward> getUserRewards(String userName) throws UserNotFoundException {
		return userRepository.getUserRewards(userName);
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
		return userRepository.getTripDeals(userName);
	}

	/**
	 * Returns a list of Provider corresponding to the trip deals for a given User and his trip UserPreferences.
	 *
	 * @param tripDealsPrefDto
	 * @return List<Provider>
	 * @throws UserNotFoundException if the User was not found
	 */
	@Override
	public List<Provider> calculateTripDeals(TripDealsPrefDto tripDealsPrefDto) throws UserNotFoundException {

		User user = this.getUserByUserName(tripDealsPrefDto.getUserName()).get();

		UserPreferences userPreferences = new UserPreferences();
		userPreferences.setTripDuration(tripDealsPrefDto.getTripDuration());
		userPreferences.setNumberOfAdults(tripDealsPrefDto.getNumberOfAdults());
		userPreferences.setNumberOfChildren(tripDealsPrefDto.getNumberOfChildren());
		userPreferences.setTripDuration(tripDealsPrefDto.getTripDuration());
		user.setUserPreferences(userPreferences);

		int cumulativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();
		List<Provider> providers = tripPricer.getPrice(this.tripPricerApiKey, user.getUserId(), userPreferences.getNumberOfAdults(),
				userPreferences.getNumberOfChildren(), userPreferences.getTripDuration(), cumulativeRewardPoints);

		List<Provider> providersCorresponding = new ArrayList<>();

		for (Provider p : providers) {

			if (p.price > tripDealsPrefDto.getLowerPricePoint() && p.price < tripDealsPrefDto.getHigherPricePoint()) {
				providersCorresponding.add(p);
			}
		}

		user.setTripDeals(providersCorresponding);
		return providersCorresponding;

	}

}
