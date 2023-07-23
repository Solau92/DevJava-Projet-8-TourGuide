package tourGuide.service.implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.repository.implementation.GpsRepositoryImpl;
import tourGuide.service.RewardsService;
import tourGuide.worker.WorkerRewards;
import tourGuide.user.User;
import tourGuide.user.UserReward;

@Service
public class RewardsServiceImpl implements RewardsService {

	public static final int NUMBER_OF_THREADS = 200;
	private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;
	private final RewardCentral rewardsCentral;
	private GpsRepositoryImpl gpsRepository;
	private Logger logger = LoggerFactory.getLogger(RewardsServiceImpl.class);
	// proximity in miles
	private int defaultProximityBuffer = 10;
	private int proximityBuffer = defaultProximityBuffer;
	private int attractionProximityRange = 200;

	public RewardsServiceImpl(GpsRepositoryImpl gpsRepository, RewardCentral rewardCentral) {
		this.gpsRepository = gpsRepository;
		this.rewardsCentral = rewardCentral;
	}

	/**
	 * Sets the proximityBuffer (in miles).
	 *
	 * @param proximityBuffer
	 */
	@Override
	public void setProximityBuffer(int proximityBuffer) {
		this.proximityBuffer = proximityBuffer;
	}

	/**
	 * Sets the defaultProximityBuffer (in miles).
	 */
	@Override
	public void setDefaultProximityBuffer() {
		proximityBuffer = defaultProximityBuffer;
	}

	/**
	 * Calculates and adds the Rewards for all the given Users.
	 *
	 * @param users
	 */
	@Override
	public void calculateAllRewards(List<User> users) {

		// Find the UserRewards that must be added

		List<Attraction> attractions = gpsRepository.getAllAttractions();

		List<User> rewardedUsers = new ArrayList<>();

		for (User user : users) {

			List<VisitedLocation> userLocations = user.getVisitedLocations();

			for (VisitedLocation visitedLocation : userLocations) {
				for (Attraction attraction : attractions) {
					if (user.getUserRewards().stream().filter(r -> r.attraction.attractionName.equals(attraction.attractionName)).count() == 0) {
						if (nearAttraction(visitedLocation, attraction)) {
							logger.info("add Reward to user " + user.getUserName() + " (reward points not calculated");
							user.addUserReward(new UserReward(visitedLocation, attraction, -1));
							rewardedUsers.add(user);
						}
					}
				}
			}
		}

		logger.info("Number of rewarde dUsers : " + rewardedUsers.size());

		// Split the list of users and make a list of tasks

		ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

		List<WorkerRewards> tasks = new ArrayList<>();

		int activeNumberOfThreads = Math.min(NUMBER_OF_THREADS, users.size());
		logger.info("number of threads : " + NUMBER_OF_THREADS + "real number of threads : " + activeNumberOfThreads);

		int bucketSize = users.size() / activeNumberOfThreads;

		for (int i = 0; i < activeNumberOfThreads; i++) {
			int from = i * bucketSize;
			int to = (i + 1) * bucketSize;
			if (i == activeNumberOfThreads - 1 || to > rewardedUsers.size()) {
				to = rewardedUsers.size();
			}
			logger.info("Thread " + (i + 1) + " will treat users between " + from + " and " + (to - 1));
			tasks.add(new WorkerRewards(this, rewardedUsers.subList(from, to)));
		}

		// Execute all the tasks

		for (WorkerRewards t : tasks) {
			executorService.execute(t);
			t.stopTracking();
		}

		executorService.shutdown();
		try {
			executorService.awaitTermination(25, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Calculates and add the Rewards for a given User.
	 *
	 * @param user
	 */
	@Override
	public void calculateRewards(User user) {

		List<VisitedLocation> userLocations = user.getVisitedLocations();
		List<Attraction> attractions = gpsRepository.getAllAttractions();

		for (VisitedLocation visitedLocation : userLocations) {
			for (Attraction attraction : attractions) {
				if (user.getUserRewards().stream().filter(r -> r.attraction.attractionName.equals(attraction.attractionName)).count() == 0) {
					if (nearAttraction(visitedLocation, attraction)) {
						user.addUserReward(new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));
					}
				}
			}
		}

	}

	/**
	 * Calculates the reward points given an attraction and a User.
	 *
	 * @param attraction
	 * @param user
	 * @return int (reward points)
	 */
	@Override
	public int getRewardPoints(Attraction attraction, User user) {
		return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
	}

	// Utilisée pour tester... (mise en public pour ça...) --> voir
	public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
		return getDistance(attraction, location) > attractionProximityRange ? false : true;
	}

	private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
		return getDistance(attraction, visitedLocation.location) > proximityBuffer ? false : true;
	}


	/**
	 * Calculates the distance between two given Locations.
	 *
	 * @param loc1
	 * @param loc2
	 * @return double (distance in miles)
	 */
	private double getDistance(Location loc1, Location loc2) {
		double lat1 = Math.toRadians(loc1.latitude);
		double lon1 = Math.toRadians(loc1.longitude);
		double lat2 = Math.toRadians(loc2.latitude);
		double lon2 = Math.toRadians(loc2.longitude);

		double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
				+ Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

		double nauticalMiles = 60 * Math.toDegrees(angle);
		double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
		return statuteMiles;
	}

}
