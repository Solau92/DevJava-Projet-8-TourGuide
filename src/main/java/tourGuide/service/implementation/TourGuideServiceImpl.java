package tourGuide.service.implementation;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import gpsUtil.location.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.VisitedLocation;
import tourGuide.exception.UserNotFoundException;
import tourGuide.service.TourGuideService;
import tourGuide.tracker.Worker;
import tourGuide.user.User;
import tripPricer.Provider;
import tripPricer.TripPricer;

@Service
public class TourGuideServiceImpl implements TourGuideService {
	public static final int NUMBER_OF_THREADS = 10;
	private final GpsUtil gpsUtil;
	private final RewardsServiceImpl rewardsService;
	private final TripPricer tripPricer = new TripPricer();
	boolean testMode = true;
	private Logger logger = LoggerFactory.getLogger(TourGuideServiceImpl.class);
	private UserServiceImpl userService;

	public TourGuideServiceImpl(GpsUtil gpsUtil, RewardsServiceImpl rewardsServiceImpl, UserServiceImpl userService) {
		this.gpsUtil = gpsUtil;
		this.rewardsService = rewardsServiceImpl;
		this.userService = userService;

		//		if(testMode) {
		//			logger.info("TestMode enabled");
		//			logger.debug("Initializing users");
		//			initializeInternalUsers();
		//			logger.debug("Finished initializing users");
		//		}

		// A mettre ??
		//		this.trackAllUsersLocation();

		addShutDownHook();
	}

	@Override
	public Location getUserLocation(String userName) throws UserNotFoundException {

		Optional<Location> userLocation = userService.getUserLocation(userName);

		if (!userLocation.isEmpty()) {
			return userLocation.get();
		}
		User user = userService.getUserByUserName(userName).get();
		return this.trackUserLocation(user).location;
	}

	@Override
	public Map<UUID, Location> getAllCurrentLocations() throws UserNotFoundException {

		Map<UUID, Location> currentLocations = new HashMap<>();

		Map<String, User> users = userService.getAllUsers();

		for(User u : users.values()) {
			currentLocations.put(u.getUserId(), this.getUserLocation(u.getUserName()));
		}

		return currentLocations;
	}

	@Override
	public void trackAllUsersLocationOnce() {

		// Voir si en paramètre ou pas ?
		List<User> users = new ArrayList<>(userService.getAllUsers().values());

		ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

		List<Worker> trackers = new ArrayList<Worker>();
		int bucketSize = users.size() / NUMBER_OF_THREADS;

		for (int i = 0; i < NUMBER_OF_THREADS; i++) {
			int from = i * bucketSize;
			int to = (i + 1) * bucketSize;
			if (i == NUMBER_OF_THREADS - 1 || to > users.size()) {
				to = users.size();
			}
			logger.info("Thread " + (i+1) + " will treat users between " + from + " and " + (to - 1));
			trackers.add(new Worker(this, users.subList(from, to)));
		}

		for (Worker t : trackers) {
			executorService.execute(t);
			// Immediately stop tracking: will be done only once
			t.stopTracking();
		}
		executorService.shutdown();
		try {
			executorService.awaitTermination(15, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	// Never used
	@Override
	public void trackAllUsersLocation() {

		// Voir si en paramètre ou pas ?
		List<User> users = new ArrayList<>(userService.getAllUsers().values());

		ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

		List<Worker> trackers = new ArrayList<Worker>();
		int bucketSize = users.size() / NUMBER_OF_THREADS;

		for (int i = 0; i < NUMBER_OF_THREADS; i++) {
			int from = i * bucketSize;
			int to = (i + 1) * bucketSize;
			if (i == NUMBER_OF_THREADS - 1 || to > users.size()) {
				to = users.size();
			}
			logger.info("Thread " + (i+1) + " will treat users between " + from + " and " + (to - 1));
			trackers.add(new Worker(this, users.subList(from, to)));
		}

		for (Worker t : trackers) {
			executorService.execute(t);
			// Never stops tracking
		}
		executorService.shutdown();
		try {
			executorService.awaitTermination(15, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

	}


	@Override
	public VisitedLocation trackUserLocation(User user) {
		logger.info("dans trackUserLocation : " + user.getUserName());
		VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
		user.addToVisitedLocations(visitedLocation);
		logger.info("dans trackUserLocation, après addVisitedLocation : " + user.getUserName());
		rewardsService.calculateRewards(user);
		return visitedLocation;
	}

	public List<Provider> getTripDeals(User user) {

		// TODO : voir où je mets ça
		String tripPricerApiKey = "test-server-api-key";

		int cumulatativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();
		List<Provider> providers = tripPricer.getPrice(tripPricerApiKey, user.getUserId(), user.getUserPreferences().getNumberOfAdults(),
				user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulatativeRewardPoints);
		user.setTripDeals(providers);
		return providers;
	}

	private void addShutDownHook() { // TODO : à voir
/*		Runtime.getRuntime().addShutdownHook(new Thread() {
		      public void run() {
		        tracker.stopTracking();
		      }
		    });*/
	}

}
