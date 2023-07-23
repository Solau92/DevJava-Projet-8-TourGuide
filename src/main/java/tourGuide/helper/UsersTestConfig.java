package tourGuide.helper;

import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import tourGuide.user.User;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.IntStream;

/**
 * Used to create users to test the app.
 */
@Configuration
public class UsersTestConfig {

	@Value("${testMode}")
	public static boolean TEST_MODE;
	private static Logger logger = LoggerFactory.getLogger(UsersTestConfig.class);
	private static Map<String, User> internalUserMap = new HashMap<>();

	/**
	 * Creates the number of users set in InternalTestHelper and add them to a Map which associate a userName to a User.
	 *
	 * @return Map<String, User>
	 */
	public static Map<String, User> initializeInternalUsers() {
		IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
			String userName = "internalUser" + i;
			String phone = "000";
			String email = userName + "@tourGuide.com";
			User user = new User(UUID.randomUUID(), userName, phone, email);
			generateUserLocationHistory(user);

			internalUserMap.put(userName, user);
		});
		logger.info("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");

		return internalUserMap;
	}

	/**
	 * Creates and add three VisitedLocation for the given User.
	 *
	 * @param user
	 */
	private static void generateUserLocationHistory(User user) {
		IntStream.range(0, 3).forEach(i -> {
			user.addToVisitedLocations(new VisitedLocation(user.getUserId(), new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
		});
	}

	/**
	 * Returns random longitude.
	 *
	 * @return double (from -180 to 180)
	 */
	private static double generateRandomLongitude() {
		double leftLimit = -180;
		double rightLimit = 180;
		return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}

	/**
	 * Returns random latitude.
	 *
	 * @return double (from -85.05112878 to 85.05112878)
	 */
	private static double generateRandomLatitude() {
		double leftLimit = -85.05112878;
		double rightLimit = 85.05112878;
		return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}

	/**
	 * Returns a random Date.
	 *
	 * @return Date
	 */
	private static Date getRandomTime() {
		LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
		return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
	}

	@Value("${testMode}")
	public void setTestMode(boolean testMode) {
		UsersTestConfig.TEST_MODE = testMode;
		logger.info("Test mode : " + TEST_MODE);
	}


}
