package tourGuide.service;

import gpsUtil.location.Attraction;
import tourGuide.dto.NearByAttractionDto;

import java.util.List;

public interface GpsService {


	List<Attraction> getAllAttractions();

	List<NearByAttractionDto> getNearbyAttractions(String userName);
}
