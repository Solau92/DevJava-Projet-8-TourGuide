package tourGuide;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import gpsUtil.GpsUtil;
import rewardCentral.RewardCentral;
import tourGuide.repository.implementation.GpsRepositoryImpl;
import tourGuide.service.RewardsService;
import tourGuide.service.implementation.RewardsServiceImpl;

@Configuration
public class TourGuideModule {

	@Bean
	public GpsUtil getGpsUtil() {
		return new GpsUtil();
	}

	@Bean
	public RewardsService getRewardsService() {
		return new RewardsServiceImpl(new GpsRepositoryImpl(getGpsUtil()), getRewardCentral());
	}

	@Bean
	public RewardCentral getRewardCentral() {
		return new RewardCentral();
	}

}
