package tourGuide.service;

import gpsUtil.location.Attraction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tourGuide.repository.GpsRepositoryImpl;

import java.util.List;

@Service
public class GpsServiceImpl implements GpsService {

	private Logger logger = LoggerFactory.getLogger(GpsServiceImpl.class);

	private GpsRepositoryImpl gpsRepository;

	public GpsServiceImpl(GpsRepositoryImpl gpsRepository) {
		this.gpsRepository = gpsRepository;
	}

	@Override
	public List<Attraction> getAllAttractions(){
		return gpsRepository.getAllAttractions();
	}





}
