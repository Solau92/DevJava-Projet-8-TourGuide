package tourGuide;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tourGuide.dto.NearByAttractionDto;
import tourGuide.exception.UserNotFoundException;
import tourGuide.service.GpsServiceImpl;
import tourGuide.service.TourGuideService;
import tourGuide.service.UserServiceImpl;
import tourGuide.user.UserReward;
import tripPricer.Provider;

@RestController
public class TourGuideController {

	private TourGuideService tourGuideService;

	private UserServiceImpl userService;

	private GpsServiceImpl gpsService;

	public TourGuideController(TourGuideService tourGuideService, UserServiceImpl userService, GpsServiceImpl gpsService) {
		this.userService = userService;
		this.tourGuideService = tourGuideService;
		this.gpsService = gpsService;
	}

	@RequestMapping("/")
	public String index() {
		return "Greetings from TourGuide!";
	}

	// Instead of old version
	@RequestMapping("/getLocation")
	public ResponseEntity<Location> getLocation(@RequestParam String userName) throws UserNotFoundException {
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(userService.getUserLocation(userName));
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
	public ResponseEntity<List<Attraction>> getNearbyAttractions() {
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(gpsService.getAllAttractions());
	}

	// Instead of old version
	@RequestMapping("/getRewards")
	public ResponseEntity<List<UserReward>> getRewards(@RequestParam String userName) throws UserNotFoundException {
		// A mettre dans TourGuideService ?? gpsService ? UserService ??
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(userService.getUserRewards(userName));
	}

	// Instead of old version
	@RequestMapping("/getAllCurrentLocations")
	public ResponseEntity<Map<UUID, Location>> getAllCurrentLocations() {
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(userService.getAllCurrentLocations());
	}

	// Instead of old version
	@RequestMapping("/getTripDeals")
	public ResponseEntity<List<Provider>> getTripDeals(@RequestParam String userName) throws UserNotFoundException {
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(userService.getTripDeals(userName));
	}

	// A priori plus utile
	//    private User getUser(String userName) {
	//    	return tourGuideService.getUser(userName);
	//    }


}