package tourGuide.service;

import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import tourGuide.dto.TripDealsPrefDto;
import tourGuide.exception.UserNotFoundException;
import tourGuide.user.User;
import tripPricer.Provider;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface TourGuideService {

	Location getUserLocation(String userName) throws UserNotFoundException;

	Map<UUID, Location> getAllCurrentLocations() throws UserNotFoundException;

	void trackAllUsersLocationOnce();

	VisitedLocation trackUserLocation(User user);

	List<Provider> getTripDeals(TripDealsPrefDto tripDealsPrefDto) throws UserNotFoundException;

}
