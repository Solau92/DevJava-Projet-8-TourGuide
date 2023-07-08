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

	int getRewardPoints(Attraction attraction, User user);

}
