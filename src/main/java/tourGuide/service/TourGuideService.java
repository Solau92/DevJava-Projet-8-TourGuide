package tourGuide.service;

import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import tourGuide.exception.UserNotFoundException;
import tourGuide.user.User;

public interface TourGuideService {
	Location getUserLocation(String userName) throws UserNotFoundException;

	VisitedLocation trackUserLocation(User user);
}
