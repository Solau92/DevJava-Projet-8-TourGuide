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
import tourGuide.service.implementation.RewardsServiceImpl;
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

	private RewardsServiceImpl rewardsService;

	public TourGuideController(TourGuideServiceImpl tourGuideService, UserServiceImpl userService, GpsServiceImpl gpsService, RewardsServiceImpl rewardsService) {
		this.userService = userService;
		this.tourGuideService = tourGuideService;
		this.gpsService = gpsService;
		this.rewardsService = rewardsService;
	}

	@RequestMapping("/")
	public String index() {
		logger.info("/index");
		return "Greetings from TourGuide!";
	}

	@RequestMapping("/getLocation")
	public ResponseEntity<Location> getLocation(@RequestParam String userName) throws UserNotFoundException {
		logger.info("/getLocation for user " + userName);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(tourGuideService.getUserLocation(userName));
	}

	@RequestMapping("/getNearbyAttractions")
	public ResponseEntity<List<NearByAttractionDto>> getNearbyAttractions(@RequestParam String userName) throws UserNotFoundException {
		// A mettre dans TourGuideService ?? gpsService ?
		logger.info("/getNearbyAttractions for user " + userName);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(gpsService.getNearbyAttractions(userName));
	}

	// Added to test
	@RequestMapping("/getAllAttractions")
	public ResponseEntity<List<Attraction>> getAllAttractions() {
		logger.info("/getAllAttractions");
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(gpsService.getAllAttractions());
	}

	@RequestMapping("/getRewards")
	public ResponseEntity<List<UserReward>> getRewards(@RequestParam String userName) throws UserNotFoundException {
		// A mettre où ?
		// Laisser dans userService si juste pour retourner, mettre dans tourGuide ou reward Service si doit calculer d'abord
		logger.info("/getRewards for user " + userName);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(userService.getUserRewards(userName));
	}

	@RequestMapping("/getAllCurrentLocations")
	// En fait : lastVisitedLocation...
	public ResponseEntity<Map<UUID, Location>> getAllCurrentLocations() throws UserNotFoundException {
		logger.info("/getAllCurrentLocations");
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(tourGuideService.getAllCurrentLocations());
	}

	@RequestMapping("/trackAllUsersLocation")
	public ResponseEntity<Map<UUID, Location>> trackAllUsersLocation() throws UserNotFoundException {
		logger.info("/trackAllUsersLocation");
		tourGuideService.trackAllUsersLocationOnce();
		logger.info("tracked all users location once");
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(tourGuideService.getAllCurrentLocations());
	}

	@RequestMapping("/getTripDeals")
	public ResponseEntity<List<Provider>> getTripDeals(@RequestBody TripDealsPrefDto tripDealsPrefDto) throws UserNotFoundException {
		logger.info("/getTripDeals for user " + tripDealsPrefDto.getUserName());
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(tourGuideService.getTripDeals(tripDealsPrefDto));
	}

	// Added to test
	@PostMapping("/addUser")
	public ResponseEntity<User> addUser(@RequestBody User user) throws UserAlreadyExistsException {
		logger.info("/addUser named " + user.getUserName());
		return ResponseEntity.status(HttpStatus.CREATED).body(userService.addUser(user).get());
	}

}