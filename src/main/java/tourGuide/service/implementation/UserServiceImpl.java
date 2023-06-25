package tourGuide.service.implementation;

import gpsUtil.location.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tourGuide.exception.UserAlreadyExistsException;
import tourGuide.exception.UserNotFoundException;
import tourGuide.repository.implementation.UserRepositoryImpl;
import tourGuide.service.UserService;
import tourGuide.user.User;
import tourGuide.user.UserReward;
import tripPricer.Provider;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

	private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	private UserRepositoryImpl userRepository;

	public UserServiceImpl(UserRepositoryImpl userRepository/*, TourGuideService tourGuideService*/) {
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
		} else {
			return userRepository.getUserByUserName(userName);
		}
	}

	@Override
	public Optional<User> addUser(User user) throws UserAlreadyExistsException {

		Optional<User> optionalUser = userRepository.addUser(user);

		if (optionalUser.isEmpty()) {
//			logger.error("User not registered : user with userName " + user.getUserName() + " already exists");
			throw new UserAlreadyExistsException("User not registered : user with userName " + user.getUserName() + " already exists");
		}
		return optionalUser;
	}

		@Override
		public Optional<Location> getUserLocation(String userName) throws UserNotFoundException {

			User user = this.getUserByUserName(userName).get();
			Optional<Location> userLocation = userRepository.getUserLocation(user);

			if (!userLocation.isEmpty()) {
				return userLocation;
			}
			return Optional.empty();
		}

	@Override
	public Map<UUID, Location> getAllCurrentLocations() {
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
