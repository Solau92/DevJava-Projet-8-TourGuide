package tourGuide.tracker;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tourGuide.service.implementation.RewardsServiceImpl;
import tourGuide.user.User;
import tourGuide.user.UserReward;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class WorkerRewards extends Thread {

	private List<User> users;
	private Logger logger = LoggerFactory.getLogger(WorkerRewards.class);

	private RewardsServiceImpl rewardService;
	private boolean stop = false;

	public WorkerRewards(RewardsServiceImpl rewardService, List<User> users) {
		this.rewardService = rewardService;
		this.users = users;
	}

	/**
	 * Stops the run() method
	 */
	public void stopTracking() {
		stop = true;
		this.interrupt();
	}

	/**
	 * Calculates the reward points for all UserReward just added but without points for each user of the users list
	 */
	@Override
	public void run() {

		StopWatch stopWatch = new StopWatch();

		logger.info("Begin calculating rewards for " + users.size() + " users.");
		stopWatch.start();

		logger.info("Thread name :" + Thread.currentThread().getName());

		for (User u : users) {

			for (UserReward r : u.getUserRewards()) {
				if (r.getRewardPoints() == -1) {
					r.setRewardPoints(rewardService.getRewardPoints(r.attraction, u));
				}
			}
		}

		stopWatch.stop();
		logger.info("Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");

	}
}
