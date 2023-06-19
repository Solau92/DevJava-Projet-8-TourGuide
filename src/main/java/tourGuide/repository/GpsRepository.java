package tourGuide.repository;

import gpsUtil.location.Attraction;

import java.util.List;

public interface GpsRepository {
	List<Attraction> getAllAttractions();
}
