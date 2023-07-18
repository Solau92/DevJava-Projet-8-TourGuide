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
import tourGuide.dto.TripDealsPrefDto;
import tourGuide.exception.UserNotFoundException;
import tourGuide.repository.implementation.GpsRepositoryImpl;
import tourGuide.service.TourGuideService;
import tourGuide.worker.WorkerTracking;
import tourGuide.user.User;
import tripPricer.Provider;
import tripPricer.TripPricer;

@Service
public class TourGuideServiceImpl implements TourGuideService {
	public static final int NUMBER_OF_THREADS = 40;

//	***private final GpsUtil gpsUtil;
	private GpsRepositoryImpl gpsRepository;

	private final RewardsServiceImpl rewardsService;
	private final TripPricer tripPricer = new TripPricer();

	@Value("${tourGuide.tripPricerApiKey}")
	private String tripPricerApiKey;

	private Logger logger = LoggerFactory.getLogger(TourGuideServiceImpl.class);
	private UserServiceImpl userService;

	public TourGuideServiceImpl(/*GpsUtil gpsUtil*/GpsRepositoryImpl gpsRepository, RewardsServiceImpl rewardsServiceImpl, UserServiceImpl userService) {
//		***this.gpsUtil = gpsUtil;
		this.gpsRepository = gpsRepository;
		this.rewardsService = rewardsServiceImpl;
		this.userService = userService;
	}

	/**
	 * Returns the last visited location of a User, given his userName.
	 * @param userName
	 * @return Location
	 * @throws UserNotFoundException if the User was not found
	 */
	@Override
	public Location getUserLocation(String userName) throws UserNotFoundException {

		Optional<Location> userLocation = userService.getUserLocation(userName);

		if (!userLocation.isEmpty()) {
			return userLocation.get();
		}
		User user = userService.getUserByUserName(userName).get();
		return this.trackUserLocation(user).location;
	}

	/**
	 * Returns the last visited Location of all the Users.
	 * @return Map<UUID, Location> containing for all the User, their ID and last visited Location
	 * @throws UserNotFoundException if the User was not found
	 */
	@Override
	public Map<UUID, Location> getAllCurrentLocations() throws UserNotFoundException {

		Map<UUID, Location> currentLocations = new HashMap<>();

		Map<String, User> users = userService.getAllUsers();

		for (User u : users.values()) {
			currentLocations.put(u.getUserId(), this.getUserLocation(u.getUserName()));
		}

		return currentLocations;
	}

	/**
	 * For all the Users of the repository list, track the User location (cf. method trackUserLocation)
	 */
	@Override
	public void trackAllUsersLocationOnce() {

		List<User> users = new ArrayList<>(userService.getAllUsers().values());

		// Split the list of users and make a list of tasks

		ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

		int activeNumberOfThreads = Math.min(NUMBER_OF_THREADS, users.size());
		logger.info("number of threads : " + NUMBER_OF_THREADS + "real number of threads : " + activeNumberOfThreads);

		List<WorkerTracking> trackers = new ArrayList<>();
		int bucketSize = users.size() / activeNumberOfThreads;

		for (int i = 0; i < activeNumberOfThreads; i++) {
			int from = i * bucketSize;
			int to = (i + 1) * bucketSize;
			if (i == activeNumberOfThreads - 1 || to > users.size()) {
				to = users.size();
			}
			logger.info("Thread " + (i + 1) + " will treat users between " + from + " and " + (to - 1));
			trackers.add(new WorkerTracking(this, users.subList(from, to)));
		}

		// Execute all the tasks

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

	/**
	 * Searches the Location of a given User, add it to his visited location and calculates the associate Reward.
	 *
	 * @param user
	 * @return the visited Location added
	 */
	@Override
	public VisitedLocation trackUserLocation(User user) {

//		*** VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());

		VisitedLocation visitedLocation = gpsRepository.getUserLocation(user.getUserId());

		user.addToVisitedLocations(visitedLocation);
		rewardsService.calculateRewards(user);
		return visitedLocation;
	}

	/**
	 * Returns a list of Provider corresponding to the trip deals for a given User and his trip UserPreferences.
	 * @param tripDealsPrefDto
	 * @return List<Provider>
	 * @throws UserNotFoundException if the User was not found
	 */
	@Override
	public List<Provider> getTripDeals(TripDealsPrefDto tripDealsPrefDto) throws UserNotFoundException {

		User user = userService.getUserByUserName(tripDealsPrefDto.getUserName()).get();

		int cumulativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();
		List<Provider> providers = tripPricer.getPrice(this.tripPricerApiKey, user.getUserId(), tripDealsPrefDto.getNumberOfAdults(),
				tripDealsPrefDto.getNumberOfChildren(), tripDealsPrefDto.getTripDuration(), cumulativeRewardPoints);
		user.setTripDeals(providers);
		return providers;
	}



}
