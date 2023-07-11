package tourGuide.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tourGuide.dto.NearByAttractionDto;
import tourGuide.dto.TripDealsPrefDto;
import tourGuide.exception.UserAlreadyExistsException;
import tourGuide.exception.UserNotFoundException;
import tourGuide.service.implementation.GpsServiceImpl;
import tourGuide.service.implementation.TourGuideServiceImpl;
import tourGuide.service.implementation.UserServiceImpl;
import tourGuide.user.User;
import tourGuide.user.UserReward;
import tripPricer.Provider;

@RestController
public class TourGuideController {

	private Logger logger = LoggerFactory.getLogger(TourGuideController.class);

	private TourGuideServiceImpl tourGuideService;

	private UserServiceImpl userService;

	private GpsServiceImpl gpsService;


	public TourGuideController(TourGuideServiceImpl tourGuideService, UserServiceImpl userService, GpsServiceImpl gpsService/*, RewardsServiceImpl rewardsService*/) {
		this.userService = userService;
		this.tourGuideService = tourGuideService;
		this.gpsService = gpsService;
	}

	@RequestMapping("/")
	public String index() {
		logger.info("/index");
		return "Greetings from TourGuide!";
	}

	/**
	 * Gets the Location of a User given his userName.
	 *
	 * @param userName
	 * @return ResponseEntity<Location> with http status accepted
	 * @throws UserNotFoundException if the User was not found
	 */
	@RequestMapping("/getLocation")
	public ResponseEntity<Location> getLocation(@RequestParam String userName) throws UserNotFoundException {
		logger.info("/getLocation for user " + userName);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(tourGuideService.getUserLocation(userName));
	}

	/**
	 * Gets the nearest attraction of a User given his userName.
	 *
	 * @param userName
	 * @return ResponseEntity<List < NearByAttractionDto>> with http status accepted
	 * @throws UserNotFoundException if the User was not found
	 */
	@RequestMapping("/getNearbyAttractions")
	public ResponseEntity<List<NearByAttractionDto>> getNearbyAttractions(@RequestParam String userName) throws UserNotFoundException {
		logger.info("/getNearbyAttractions for user " + userName);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(gpsService.getNearbyAttractions(userName));
	}

	/**
	 * Gets all the registered attractions.
	 * Added to test
	 *
	 * @return ResponseEntity<List < Attraction>> with http status accepted
	 */
	@RequestMapping("/getAllAttractions")
	public ResponseEntity<List<Attraction>> getAllAttractions() {
		logger.info("/getAllAttractions");
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(gpsService.getAllAttractions());
	}

	/**
	 * Gets the rewards of a User given his userName.
	 *
	 * @param userName
	 * @return ResponseEntity<List < UserReward>> with http status accepted
	 * @throws UserNotFoundException if the User was not found
	 */
	@RequestMapping("/getRewards")
	public ResponseEntity<List<UserReward>> getRewards(@RequestParam String userName) throws UserNotFoundException {
		logger.info("/getRewards for user " + userName);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(userService.getUserRewards(userName));
	}

	/**
	 * Gets the last visited Location of all the Users.
	 *
	 * @return ResponseEntity<Map < UUID, Location>> with http status accepted
	 * @throws UserNotFoundException if the User was not found
	 */
	@RequestMapping("/getAllCurrentLocations")
	public ResponseEntity<Map<UUID, Location>> getAllCurrentLocations() throws UserNotFoundException {
		logger.info("/getAllCurrentLocations");
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(tourGuideService.getAllCurrentLocations());
	}

	/**
	 * Tracks the location of all the Users.
	 *
	 * @return ResponseEntity<Map < UUID, Location>> with http status accepted
	 * @throws UserNotFoundException if the User was not found
	 */
	@RequestMapping("/trackAllUsersLocation")
	public ResponseEntity<Map<UUID, Location>> trackAllUsersLocation() throws UserNotFoundException {
		logger.info("/trackAllUsersLocation");
		tourGuideService.trackAllUsersLocationOnce();
		logger.info("tracked all users location once");
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(tourGuideService.getAllCurrentLocations());
	}

	/**
	 * Gets the tripdeals for a given User and his trip preferences.
	 *
	 * @param tripDealsPrefDto
	 * @return ResponseEntity<List < Provider>> with http status accepted
	 * @throws UserNotFoundException if the User was not found
	 */
	@RequestMapping("/getTripDeals")
	public ResponseEntity<List<Provider>> getTripDeals(@RequestBody TripDealsPrefDto tripDealsPrefDto) throws UserNotFoundException {
		logger.info("/getTripDeals for user " + tripDealsPrefDto.getUserName());
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(tourGuideService.getTripDeals(tripDealsPrefDto));
	}

	/**
	 * Adds the given User.
	 *
	 * @param user
	 * @return ResponseEntity<User> with http status created
	 * @throws UserAlreadyExistsException if the User already exists
	 */
	@PostMapping("/addUser")
	public ResponseEntity<User> addUser(@RequestBody User user) throws UserAlreadyExistsException {
		logger.info("/addUser named " + user.getUserName());
		return ResponseEntity.status(HttpStatus.CREATED).body(userService.addUser(user).get());
	}

}