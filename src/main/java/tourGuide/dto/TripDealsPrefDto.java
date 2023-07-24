package tourGuide.dto;

import org.javamoney.moneta.Money;

public class TripDealsPrefDto {

	private String userName;

	private int numberOfAdults;

	private int numberOfChildren;

	private int tripDuration;

	private double lowerPricePoint;

	private double higherPricePoint;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getNumberOfAdults() {
		return numberOfAdults;
	}

	public void setNumberOfAdults(int numberOfAdults) {
		this.numberOfAdults = numberOfAdults;
	}

	public int getNumberOfChildren() {
		return numberOfChildren;
	}

	public void setNumberOfChildren(int numberOfChildren) {
		this.numberOfChildren = numberOfChildren;
	}

	public int getTripDuration() {
		return tripDuration;
	}

	public void setTripDuration(int tripDuration) {
		this.tripDuration = tripDuration;
	}

	public double getLowerPricePoint() {
		return lowerPricePoint;
	}

	public void setLowerPricePoint(double lowerPricePoint) {
		this.lowerPricePoint = lowerPricePoint;
	}

	public double getHigherPricePoint() {
		return higherPricePoint;
	}

	public void setHigherPricePoint(double higherPricePoint) {
		this.higherPricePoint = higherPricePoint;
	}
}
