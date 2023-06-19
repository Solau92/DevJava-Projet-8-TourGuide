package tourGuide.repository;

import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import tourGuide.user.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

	Map<String, User> getAllUsers();

	Optional<User> getUserByUserName(String userName);

	Optional<User> addUser(User user);

	Location getUserLocation(User user);

	Map<UUID, Location> getAllCurrentLocations();
}
