package tourGuide.service;

import gpsUtil.location.Attraction;
import tourGuide.user.User;

import java.util.List;

public interface RewardsService {

	/**
	 * Sets the proximityBuffer.
	 *
	 * @param proximityBuffer
	 */
	void setProximityBuffer(int proximityBuffer);

	/**
	 * Sets the defaultProximityBuffer.
	 */
	void setDefaultProximityBuffer();

	/**
	 * Calculates and adds the Rewards for all the given Users.
	 *
	 * @param users
	 */
	void calculateAllRewards(List<User> users);

	/**
	 * Calculates and add the Rewards for a given User.
	 *
	 * @param user
	 */
	void calculateRewards(User user);

	/**
	 * Calculates the reward points fiven an attraction and a User.
	 *
	 * @param attraction
	 * @param user
	 * @return int (reward points)
	 */
	int getRewardPoints(Attraction attraction, User user);

}
