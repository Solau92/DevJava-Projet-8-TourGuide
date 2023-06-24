package tourGuide.dto;

public class NearByAttractionDto {

	private String attractionName;

	private double attractionLongitude;

	private double attractionLatitude;

	private double userLongitude;

	private double userLatitude;

	private double distanceBetween;

	private int rewardPoints;

	public void setAttractionName(String attractionName) {
		this.attractionName = attractionName;
	}

	public void setAttractionLongitude(double attractionLongitude) {
		this.attractionLongitude = attractionLongitude;
	}

	public void setAttractionLatitude(double attractionLatitude) {
		this.attractionLatitude = attractionLatitude;
	}

	public void setUserLongitude(double userLongitude) {
		this.userLongitude = userLongitude;
	}

	public void setUserLatitude(double userLatitude) {
		this.userLatitude = userLatitude;
	}

	public void setDistanceBetween(double distanceBetween) {
		this.distanceBetween = distanceBetween;
	}

	public void setRewardPoints(int rewardPoints) {
		this.rewardPoints = rewardPoints;
	}

	public String getAttractionName() {
		return attractionName;
	}

	public double getAttractionLongitude() {
		return attractionLongitude;
	}

	public double getAttractionLatitude() {
		return attractionLatitude;
	}

	public double getUserLongitude() {
		return userLongitude;
	}

	public double getUserLatitude() {
		return userLatitude;
	}

	public double getDistanceBetween() {
		return distanceBetween;
	}

	public int getRewardPoints() {
		return rewardPoints;
	}
}
