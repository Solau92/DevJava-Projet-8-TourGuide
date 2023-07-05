package tourGuide.service;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import tourGuide.user.User;

import java.util.List;

public interface RewardsService {
	void setProximityBuffer(int proximityBuffer);

	void setDefaultProximityBuffer();

	void calculateAllRewards(List<User> users);

	void calculateRewards(User user);

	boolean isWithinAttractionProximity(Attraction attraction, Location location);

	boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction);

	int getRewardPoints(Attraction attraction, User user);

	double getDistance(Location loc1, Location loc2);
}
