package tourGuide.service.implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.service.RewardsService;
import tourGuide.tracker.WorkerRewards;
import tourGuide.user.User;
import tourGuide.user.UserReward;

@Service
public class RewardsServiceImpl implements RewardsService {

	private Logger logger = LoggerFactory.getLogger(RewardsServiceImpl.class);
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;
	public static final int NUMBER_OF_THREADS = 30;

	// proximity in miles
    private int defaultProximityBuffer = 10;
	private int proximityBuffer = defaultProximityBuffer;
	private int attractionProximityRange = 200;
	private final GpsUtil gpsUtil;
	private final RewardCentral rewardsCentral;

	public RewardsServiceImpl(GpsUtil gpsUtil, RewardCentral rewardCentral) {
		this.gpsUtil = gpsUtil;
		this.rewardsCentral = rewardCentral;
	}
	
	@Override
	public void setProximityBuffer(int proximityBuffer) {
		this.proximityBuffer = proximityBuffer;
	}
	
	@Override
	public void setDefaultProximityBuffer() {
		proximityBuffer = defaultProximityBuffer;
	}

	@Override
	public void calculateAllRewards(List<User> users) {

		///////////////////// Trouver les rewards
		List<Attraction> attractions = gpsUtil.getAttractions();
		List<User> rewardedUsers = new ArrayList<>();

		for (User user : users) {

			List<VisitedLocation> userLocations = user.getVisitedLocations();

			for (VisitedLocation visitedLocation : userLocations) {
				for (Attraction attraction : attractions) {
					if (user.getUserRewards().stream().filter(r -> r.attraction.attractionName.equals(attraction.attractionName)).count() == 0) {
						if (nearAttraction(visitedLocation, attraction)) {
							user.addUserReward(new UserReward(visitedLocation, attraction, -1));
							rewardedUsers.add(user);
						}
					}
				}
			}
		}

		logger.info("rewardedUser size : " + rewardedUsers.size());

		///////////////////// Découper ma liste et lancer les threads

		ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

		List<WorkerRewards> tasks = new ArrayList<>();

		int activeNoOfThreads = Math.min(NUMBER_OF_THREADS, users.size());

		int bucketSize = users.size() / activeNoOfThreads;

		for (int i = 0; i < activeNoOfThreads; i++) {
			int from = i * bucketSize;
			int to = (i + 1) * bucketSize;
			if (i == activeNoOfThreads - 1 || to > rewardedUsers.size()) {
				to = rewardedUsers.size();
			}
			logger.info("Thread " + (i+1) + " will treat users between " + from + " and " + (to - 1));
			tasks.add(new WorkerRewards(this, rewardedUsers.subList(from, to)));
		}

		for(WorkerRewards t : tasks) {
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


	@Override
	public void calculateRewards(User user) {

		List<VisitedLocation> userLocations = user.getVisitedLocations();
		List<Attraction> attractions = gpsUtil.getAttractions();

//		logger.info(String.valueOf(user.getUserRewards().size()));

		for(VisitedLocation visitedLocation : userLocations) {
//			logger.info("visited location : " + visitedLocation.location.latitude + " " + visitedLocation.location.longitude);
			for(Attraction attraction : attractions) {
//				logger.info(attraction.attractionName);
				if(user.getUserRewards().stream().filter(r -> r.attraction.attractionName.equals(attraction.attractionName)).count() == 0) {
//					logger.info("user jamais allé dans attraction");
					if(nearAttraction(visitedLocation, attraction)) {
//						logger.info("visited location near attraction --> calculate reward");
						user.addUserReward(new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));
//						logger.info("size après add reward : " + String.valueOf(user.getUserRewards().size()));
					}
				}
			}
		}
//		logger.info("size à la fin : " + String.valueOf(user.getUserRewards().size()));
	}
	

	private boolean isWithinAttractionProximity(Attraction attraction, Location location) {
		return getDistance(attraction, location) > attractionProximityRange ? false : true;
	}
	
	private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
		return getDistance(attraction, visitedLocation.location) > proximityBuffer ? false : true;
	}
	
	@Override
	public int getRewardPoints(Attraction attraction, User user) {
		return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
	}
	

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
