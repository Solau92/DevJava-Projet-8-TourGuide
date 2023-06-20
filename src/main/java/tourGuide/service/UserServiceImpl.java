package tourGuide.service;

import gpsUtil.location.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tourGuide.exception.UserAlreadyExistsException;
import tourGuide.exception.UserNotFoundException;
import tourGuide.repository.UserRepositoryImpl;
import tourGuide.user.User;
import tourGuide.user.UserReward;
import tripPricer.Provider;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

	public TourGuideService tourGuideService;
	private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	private UserRepositoryImpl userRepository;

	public UserServiceImpl(/*GpsUtil gpsUtil,*/ UserRepositoryImpl userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public Map<String, User> getAllUsers() {
		return userRepository.getAllUsers();
	}

	@Override
	public Optional<User> getUserByUserName(String userName) throws UserNotFoundException {

		if (userRepository.getUserByUserName(userName).isEmpty()) {
			throw new UserNotFoundException("User with userName " + userName + " was not found");
		}
		return userRepository.getUserByUserName(userName);
	}

	@Override
	public Optional<User> addUser(User user) throws UserAlreadyExistsException {
		if (userRepository.addUser(user).isEmpty()) {
			throw new UserAlreadyExistsException("User with userName " + user.getUserName() + " already exists");
		}
		return userRepository.addUser(user);
	}

	@Override
	public Location getUserLocation(String userName) throws UserNotFoundException {

		User user = this.getUserByUserName(userName).get();

		if (userRepository.getUserLocation(user) != null) {
			return userRepository.getUserLocation(user);
		} else {
			// Voir si on fait autrement que d'utiliser tourGuideService ??
			return tourGuideService.trackUserLocation(user).location;
		}
	}

	@Override
	public Map<UUID, Location> getAllCurrentLocations() {
		Map<UUID, Location> currentLocations = new HashMap<>();
		return userRepository.getAllCurrentLocations();
	}

	@Override
	public List<UserReward> getUserRewards(String userName) throws UserNotFoundException {
		return userRepository.getUserRewards(userName);
	}

	@Override
	public List<Provider> getTripDeals(String userName) throws UserNotFoundException {
		return userRepository.getTripDeals(userName);
	}
}
