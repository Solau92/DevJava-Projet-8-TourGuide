package tourGuide.repository.implementation;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.springframework.stereotype.Repository;
import tourGuide.repository.GpsRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class GpsRepositoryImpl implements GpsRepository {

	List<Attraction> attractions;
	private GpsUtil gpsUtil;

	public GpsRepositoryImpl(GpsUtil gpsUtil) {
		this.gpsUtil = gpsUtil;
		this.attractions = new ArrayList<>();
	}

	/**
	 * Returns the list of all attractions registered in GpsUtil.
	 * @return a List of Attractions
	 */
	@Override
	public List<Attraction> getAllAttractions() {
		if(attractions.isEmpty()) {
			return gpsUtil.getAttractions();
		}
		return this.attractions;
	}

	/**
	 * Returns the location of a user, given his id.
	 *
	 * @param userId
	 * @return
	 */
	@Override
	public VisitedLocation getUserLocation(UUID userId) {
		return gpsUtil.getUserLocation(userId);
	}
}
