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
import tourGuide.service.GpsServiceImpl;
import tourGuide.service.TourGuideService;
import tourGuide.service.UserServiceImpl;

@RestController
public class TourGuideController {

	private TourGuideService tourGuideService;

	private UserServiceImpl userServiceImpl;

	private GpsServiceImpl gpsService;

	public TourGuideController(TourGuideService tourGuideService, UserServiceImpl userServiceImpl, GpsServiceImpl gpsService) {
		this.userServiceImpl = userServiceImpl;
		this.tourGuideService = tourGuideService;
		this.gpsService = gpsService;
	}

	@RequestMapping("/")
	public String index() {
		return "Greetings from TourGuide!";
	}

	// Instead of old version
	@RequestMapping("/getLocation")
	public ResponseEntity<Location> getLocation(@RequestParam String userName) {
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(userServiceImpl.getUserLocation(userName));
	}

	//  TODO: Change this method to no longer return a List of Attractions.
	//  Instead: Get the closest five tourist attractions to the user - no matter how far away they are.
	//  Return a new JSON object that contains:
	// Name of Tourist attraction,
	// Tourist attractions lat/long,
	// The user's location lat/long,
	// The distance in miles between the user's location and each of the attractions.
	// The reward points for visiting each Attraction.
	//    Note: Attraction reward points can be gathered from RewardsCentral
	@RequestMapping("/getNearbyAttractions")
	public ResponseEntity<List<NearByAttractionDto>> getNearbyAttractions(@RequestParam String userName) {
		// A mettre dans gpsService ? UserService ?? TourGuideService ??
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(gpsService.getNearbyAttractions(userName));
	}

	// Added to test
	@RequestMapping("/getAllAttractions")
	public ResponseEntity<List<Attraction>> getNearbyAttractions() {
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(gpsService.getAllAttractions());
	}

	//    @RequestMapping("/getRewards")
	//    public String getRewards(@RequestParam String userName) {
	//    	return JsonStream.serialize(tourGuideService.getUserRewards(getUser(userName)));
	//    }


	// Instead of old version
	@RequestMapping("/getAllCurrentLocations")
	public ResponseEntity<Map<UUID, Location>> getAllCurrentLocations() {
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(userServiceImpl.getAllCurrentLocations());
	}

	//    @RequestMapping("/getTripDeals")
	//    public String getTripDeals(@RequestParam String userName) {
	//    	List<Provider> providers = tourGuideService.getTripDeals(getUser(userName));
	//    	return JsonStream.serialize(providers);
	//    }

	// A priori plus utile
	//    private User getUser(String userName) {
	//    	return tourGuideService.getUser(userName);
	//    }


}