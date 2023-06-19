package tourGuide.service;

import gpsUtil.location.Location;
import tourGuide.user.User;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
	Map<String, User> getAllUsers();

	Optional<User> getUserByUserName(String userName);

	Optional<User> addUser(User user);

	Map<UUID, Location> getAllCurrentLocations();
}
