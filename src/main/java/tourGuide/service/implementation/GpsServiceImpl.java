package tourGuide.service.implementation;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tourGuide.dto.NearByAttractionDto;
import tourGuide.exception.UserNotFoundException;
import tourGuide.repository.implementation.GpsRepositoryImpl;
import tourGuide.service.GpsService;
import tourGuide.service.RewardsService;
import tourGuide.service.UserService;
import tourGuide.user.User;
import tourGuide.worker.WorkerTracking;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class GpsServiceImpl implements GpsService {

	public static final int NUMBER_OF_THREADS = 40;
	private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;
	private Logger logger = LoggerFactory.getLogger(GpsServiceImpl.class);
	private GpsRepositoryImpl gpsRepository;
	private UserService userService;

	private RewardsService rewardsService;

	public GpsServiceImpl(GpsRepositoryImpl gpsRepository, UserService userService/*, TourGuideServiceImpl tourGuideService*/, RewardsServiceImpl rewardsService) {
		this.gpsRepository = gpsRepository;
		this.userService = userService;
		this.rewardsService = rewardsService;
	}

	/**
	 * Returns the list of all the attractions from repository.
	 *
	 * @return List<Attraction>
	 */
	@Override
	public List<Attraction> getAllAttractions() {
		return gpsRepository.getAllAttractions();
	}

	/**
	 * Returns the last visited location of a User, given his id.
	 *
	 * @param userId
	 * @return Location
	 */
	@Override
	public VisitedLocation getUserLocation(UUID userId) {
		return gpsRepository.getUserLocation(userId);
	}

	/**
	 * Returns a list of NearByAttractionDto representing the five nearest Attractions from the given User.
	 *
	 * @param userName
	 * @return List<NearByAttractionDto>
	 * @throws UserNotFoundException if the given user was not found
	 */
	@Override
	public List<NearByAttractionDto> getNearbyAttractions(String userName) throws UserNotFoundException {

		Location userLocation = this.getUserLocation(userName);
		double userLatitude = userLocation.latitude;
		double userLongitude = userLocation.longitude;

		List<Attraction> attractions = gpsRepository.getAllAttractions();

		Map<Double, Attraction> sortedAttractions = new TreeMap<>();

		int numberOfAttractions = 5;

		for (Attraction a : attractions) {
			Double distanceBetween = this.distanceBetweenTwoPoints(userLocation, a);
			sortedAttractions.put(distanceBetween, a);
		}

		List<NearByAttractionDto> nearByAttractions = new ArrayList<>();

		for (Double distance : sortedAttractions.keySet()) {

			if (numberOfAttractions > 0) {

				NearByAttractionDto dto = new NearByAttractionDto();
				dto.setAttractionName(sortedAttractions.get(distance).attractionName);
				dto.setAttractionLatitude(sortedAttractions.get(distance).latitude);
				dto.setAttractionLongitude(sortedAttractions.get(distance).longitude);
				dto.setUserLatitude(userLatitude);
				dto.setUserLongitude(userLongitude);
				dto.setDistanceBetween(distance);
				dto.setRewardPoints(rewardsService.getRewardPoints(sortedAttractions.get(distance), userService.getUserByUserName(userName).get()));

				nearByAttractions.add(dto);

				numberOfAttractions--;
			}
		}
		return nearByAttractions;
	}

	/**
	 * Returns the distance between the give user Location ande the given attraction Location.
	 *
	 * @param userLocation
	 * @param attractionLocation
	 * @return double (distance in miles)
	 */
	private double distanceBetweenTwoPoints(Location userLocation, Location attractionLocation) {

		double lat1 = Math.toRadians(userLocation.latitude);
		double lon1 = Math.toRadians(userLocation.longitude);
		double lat2 = Math.toRadians(attractionLocation.latitude);
		double lon2 = Math.toRadians(attractionLocation.longitude);

		double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
				+ Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

		double nauticalMiles = 60 * Math.toDegrees(angle);
		double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;

		return statuteMiles;
	}

	/**
	 * Returns the last visited location of a User, given his userName.
	 *
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
	 * Searches the Location of a given User, add it to his visited location and calculates the associate Reward.
	 *
	 * @param user
	 * @return the visited Location added
	 */
	@Override
	public VisitedLocation trackUserLocation(User user) {

		VisitedLocation visitedLocation = gpsRepository.getUserLocation(user.getUserId());

		user.addToVisitedLocations(visitedLocation);
		rewardsService.calculateRewards(user);
		return visitedLocation;
	}

	/**
	 * Returns the last visited Location of all the Users.
	 *
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

}
