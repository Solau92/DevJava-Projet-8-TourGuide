package tourGuide.service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tourGuide.repository.UserRepositoryImpl;
import tourGuide.user.User;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

	//	private final GpsUtil gpsUtil;
	public TourGuideService tourGuideService;
	private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	private UserRepositoryImpl userRepository;

	public UserServiceImpl(/*GpsUtil gpsUtil,*/ UserRepositoryImpl userRepository) {
		//		this.gpsUtil = gpsUtil;
		this.userRepository = userRepository;
		//		this.tracker = new Tracker();
	}

	@Override
	public Map<String, User> getAllUsers() {
		return userRepository.getAllUsers();
	}

	@Override
	public Optional<User> getUserByUserName(String userName) {
		return userRepository.getUserByUserName(userName);
	}

	@Override
	public Optional<User> addUser(User user) {
		return userRepository.addUser(user);
	}

	public Location getUserLocation(String userName) {

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
}
