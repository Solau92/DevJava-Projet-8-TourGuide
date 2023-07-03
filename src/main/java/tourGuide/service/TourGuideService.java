package tourGuide.service;

import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import tourGuide.exception.UserNotFoundException;
import tourGuide.user.User;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface TourGuideService {
	Location getUserLocation(String userName) throws UserNotFoundException;

//	void trackAllUsersLocation(List<User> users);

	Map<UUID, Location> getAllCurrentLocations() throws UserNotFoundException;

	void trackAllUsersLocation();

	void trackAllUsersLocationOnce();

		//	public void trackAllUsersLocation(List<User> users) {

	VisitedLocation trackUserLocation(User user);
}
