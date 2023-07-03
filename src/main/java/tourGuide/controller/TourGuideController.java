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

	public TourGuideController(TourGuideServiceImpl tourGuideService, UserServiceImpl userService, GpsServiceImpl gpsService) {
		this.userService = userService;
		this.tourGuideService = tourGuideService;
		this.gpsService = gpsService;
	}

	@RequestMapping("/")
	public String index() {
		return "Greetings from TourGuide!";
	}

	@RequestMapping("/getLocation")
	public ResponseEntity<Location> getLocation(@RequestParam String userName) throws UserNotFoundException {
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(tourGuideService.getUserLocation(userName));
	}

	//  TodoNearlyDone : reward points
	//  Note: Attraction reward points can be gathered from RewardsCentral
	@RequestMapping("/getNearbyAttractions")
	public ResponseEntity<List<NearByAttractionDto>> getNearbyAttractions(@RequestParam String userName) throws UserNotFoundException {
		// A mettre dans TourGuideService ?? gpsService ? UserService ??
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(gpsService.getNearbyAttractions(userName));
	}

	// Added to test
	@RequestMapping("/getAllAttractions")
	public ResponseEntity<List<Attraction>> getAllAttractions() {
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(gpsService.getAllAttractions());
	}

	@RequestMapping("/getRewards")
	public ResponseEntity<List<UserReward>> getRewards(@RequestParam String userName) throws UserNotFoundException {
		// A mettre dans TourGuideService ?? gpsService ? UserService ??
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(userService.getUserRewards(userName));
	}

	@RequestMapping("/getAllCurrentLocations") // En fait : lastVisitedLocation...
	public ResponseEntity<Map<UUID, Location>> getAllCurrentLocations() throws UserNotFoundException {
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(tourGuideService.getAllCurrentLocations());
	}

	@RequestMapping("/getTripDeals")
	public ResponseEntity<List<Provider>> getTripDeals(@RequestParam String userName) throws UserNotFoundException {
		// TODO : à modifier, car doit non seulement renvoyer, mais avant calculer...
//		return ResponseEntity.status(HttpStatus.ACCEPTED).body(userService.getTripDeals(userName));
		// Voir si je laisse dans tourGuide ou si User Service ?
		// Et voir si user ou userName en paramètre ?
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(tourGuideService.getTripDeals(userService.getUserByUserName(userName).get()));
	}

	// Added to test
	@PostMapping("/addUser")
	public ResponseEntity<User> addUser(@RequestBody User user) throws UserAlreadyExistsException {
		return ResponseEntity.status(HttpStatus.CREATED).body(userService.addUser(user).get());
	}

}