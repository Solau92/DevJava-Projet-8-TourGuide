package tourGuide.service.implementation;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import gpsUtil.location.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.VisitedLocation;
import tourGuide.exception.UserNotFoundException;
import tourGuide.service.TourGuideService;
import tourGuide.tracker.WorkerTracking;
import tourGuide.user.User;
import tripPricer.Provider;
import tripPricer.TripPricer;

@Service
public class TourGuideServiceImpl implements TourGuideService {
	public static final int NUMBER_OF_THREADS = 40;
	private final GpsUtil gpsUtil;
	private final RewardsServiceImpl rewardsService;
	private final TripPricer tripPricer = new TripPricer();

	@Value("${tourGuide.tripPricerApiKey}")
	private String tripPricerApiKey;

	private Logger logger = LoggerFactory.getLogger(TourGuideServiceImpl.class);
	private UserServiceImpl userService;

	public TourGuideServiceImpl(GpsUtil gpsUtil, RewardsServiceImpl rewardsServiceImpl, UserServiceImpl userService) {
		this.gpsUtil = gpsUtil;
		this.rewardsService = rewardsServiceImpl;
		this.userService = userService;

		// Tracking à lancer au démarrage ?
		// this.trackAllUsersLocation();

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

		// TODO : Voir si en paramètre ou pas ? --> plutôt avec une liste en paramètres, parce que pas forcément tous les users
		List<User> users = new ArrayList<>(userService.getAllUsers().values());

		ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

		List<WorkerTracking> trackers = new ArrayList<>();
		int bucketSize = users.size() / NUMBER_OF_THREADS;

		for (int i = 0; i < NUMBER_OF_THREADS; i++) {
			int from = i * bucketSize;
			int to = (i + 1) * bucketSize;
			if (i == NUMBER_OF_THREADS - 1 || to > users.size()) {
				to = users.size();
			}
			logger.info("Thread " + (i+1) + " will treat users between " + from + " and " + (to - 1));
			trackers.add(new WorkerTracking(this, users.subList(from, to)));
		}

		for (WorkerTracking t : trackers) {
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

		// TODO : Voir si en paramètre ou pas ? --> plutôt avec une liste en paramètres, parce que pas forcément tous les users
		List<User> users = new ArrayList<>(userService.getAllUsers().values());

		ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

		List<WorkerTracking> trackers = new ArrayList<WorkerTracking>();
		int bucketSize = users.size() / NUMBER_OF_THREADS;

		for (int i = 0; i < NUMBER_OF_THREADS; i++) {
			int from = i * bucketSize;
			int to = (i + 1) * bucketSize;
			if (i == NUMBER_OF_THREADS - 1 || to > users.size()) {
				to = users.size();
			}
			logger.info("Thread " + (i+1) + " will treat users between " + from + " and " + (to - 1));
			trackers.add(new WorkerTracking(this, users.subList(from, to)));
		}

		for (WorkerTracking t : trackers) {
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
//		logger.info("dans trackUserLocation : " + user.getUserName());
		VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
		user.addToVisitedLocations(visitedLocation);
//		logger.info("dans trackUserLocation, après addVisitedLocation : " + user.getUserName());
		rewardsService.calculateRewards(user);
		return visitedLocation;
	}

	public List<Provider> getTripDeals(User user) {

		int cumulatativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();
		List<Provider> providers = tripPricer.getPrice(this.tripPricerApiKey, user.getUserId(), user.getUserPreferences().getNumberOfAdults(),
				user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulatativeRewardPoints);
		user.setTripDeals(providers);
		return providers; // ou userService.getTripDeals ??
	}

}
