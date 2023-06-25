package tourGuide.repository;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import tourGuide.repository.implementation.GpsRepositoryImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
class GpsRepositoryTest {

	@InjectMocks
	private GpsRepositoryImpl gpsRepository;

	@Mock
	private GpsUtil gpsUtil;

	@Test
	void getAllAttractions_Ok_Test(){

		// GIVEN
		Attraction attraction1 = new Attraction("attraction1", "city1", "state1", 50.0, 60.0);
		Attraction attraction2 = new Attraction("attraction2", "city2", "state2", 55.0, 66.0);

		List<Attraction> attractions = new ArrayList<>();
		attractions.add(attraction1);
		attractions.add(attraction2);

		when(gpsUtil.getAttractions()).thenReturn(attractions);

		// WHEN
		List<Attraction> attractionsFound = gpsRepository.getAllAttractions();

		// THEN
		assertEquals(2, attractionsFound.size());

	}

}
