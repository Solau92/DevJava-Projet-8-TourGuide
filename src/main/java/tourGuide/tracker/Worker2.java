package tourGuide.tracker;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rewardCentral.RewardCentral;
import tourGuide.service.RewardsService;
import tourGuide.service.implementation.RewardsServiceImpl;
import tourGuide.service.implementation.TourGuideServiceImpl;
import tourGuide.user.User;
import tourGuide.user.UserReward;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Worker2 extends Thread {

	private static final long trackingPollingInterval = TimeUnit.MINUTES.toSeconds(5);
	List<User> users;
	private Logger logger = LoggerFactory.getLogger(Worker2.class);

	private RewardsServiceImpl rewardService;
	private boolean stop = false;

	public Worker2(RewardsServiceImpl rewardService, List<User> users) {
		this.rewardService = rewardService;
		this.users = users;
	}

	public void stopTracking() {
		stop = true;
		this.interrupt();
	}

	@Override
	public void run() {

/*		while (true) {*/
//			StopWatch stopWatch = new StopWatch();

//			logger.info("Begin calculating rewards for " + users.size() + " users.");
//			stopWatch.start();

			logger.info("Thread name :" + Thread.currentThread().getName());

			for (User u : users) {

				for (UserReward r : u.getUserRewards()) {
					if (r.getRewardPoints() == -1) {
						r.setRewardPoints(rewardService.getRewardPoints(r.attraction, u));
					}
				}
			}

//			stopWatch.stop();
//			logger.info("Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");

/*			if (Thread.currentThread().isInterrupted() || stop) {
				logger.debug("Stopping");
				break;
			}

			try {
				logger.debug("Sleeping");
				TimeUnit.SECONDS.sleep(trackingPollingInterval);
			} catch (InterruptedException e) {
				break;
			}*/

	//	}
	}
}
