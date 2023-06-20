package tourGuide.repository.implementation;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import tourGuide.repository.GpsRepository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class GpsRepositoryImpl implements GpsRepository {

	private Logger logger = LoggerFactory.getLogger(GpsRepositoryImpl.class);

	private GpsUtil gpsUtil;

	List<Attraction> attractions;

	public GpsRepositoryImpl(GpsUtil gpsUtil) {
		this.attractions = new ArrayList<>();
		this.gpsUtil = gpsUtil;
	}

	@Override
	public List<Attraction> getAllAttractions() {
		return gpsUtil.getAttractions();
	}
}
