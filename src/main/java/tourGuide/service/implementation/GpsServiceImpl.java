package tourGuide.service.implementation;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tourGuide.dto.NearByAttractionDto;
import tourGuide.exception.UserNotFoundException;
import tourGuide.repository.implementation.GpsRepositoryImpl;
import tourGuide.service.GpsService;
import tourGuide.service.UserService;

import java.util.*;

@Service
public class GpsServiceImpl implements GpsService {

	private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

	private Logger logger = LoggerFactory.getLogger(GpsServiceImpl.class);

	private GpsRepositoryImpl gpsRepository;

	private UserService userService;

	public GpsServiceImpl(GpsRepositoryImpl gpsRepository, UserService userService) {
		this.gpsRepository = gpsRepository;
		this.userService = userService;
	}

	@Override
	public List<Attraction> getAllAttractions() {
		return gpsRepository.getAllAttractions();
	}


	// Voir si doit rester ici...
	@Override
	public List<NearByAttractionDto> getNearbyAttractions(String userName) throws UserNotFoundException {

		Location userLocation = userService.getUserLocation(userName).get();
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
				nearByAttractions.add(dto);

				// Reward Points à ajouter

				numberOfAttractions--;
			}
			// Arrêter le parcours ?
		}

		return nearByAttractions;
	}

	private double distanceBetweenTwoPoints(Location userLocation, Location attractionLocation) {

		// Pris dans RewardService : mieux ici ? doublon ? à voir TODO
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
}
