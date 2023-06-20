package tourGuide.service;

import gpsUtil.location.Location;
import tourGuide.exception.UserAlreadyExistsException;
import tourGuide.exception.UserNotFoundException;
import tourGuide.userModel.User;
import tourGuide.userModel.UserReward;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

import tripPricer.Provider;


public interface UserService {
	Map<String, User> getAllUsers();

	Optional<User> getUserByUserName(String userName) throws UserNotFoundException;

	Optional<User> addUser(User user) throws UserAlreadyExistsException;

	Location getUserLocation(String userName) throws UserNotFoundException;

	Map<UUID, Location> getAllCurrentLocations();

	List<UserReward> getUserRewards(String userName) throws UserNotFoundException;

	List<Provider> getTripDeals(String userName) throws UserNotFoundException;
}
