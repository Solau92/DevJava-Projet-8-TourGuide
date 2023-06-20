package tourGuide.repository;

import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import tourGuide.exception.UserNotFoundException;
import tourGuide.user.User;
import tourGuide.user.UserReward;
import tripPricer.Provider;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

	Map<String, User> getAllUsers();

	Optional<User> getUserByUserName(String userName) throws UserNotFoundException;

	Optional<User> addUser(User user);

	Location getUserLocation(User user);

	Map<UUID, Location> getAllCurrentLocations();

	List<UserReward> getUserRewards(String userName) throws UserNotFoundException;

	List<Provider> getTripDeals(String userName) throws UserNotFoundException;
}
