package de.transformationsstadt.geoportal.entities;

/**
 * Ein Rechteck eben.
 * 
 * @author Sebastian Bruch
 *
 */
public class Rectangle {

	Double minX;
	Double minY;
	Double maxX;
	Double maxY;
	
	public Double getMinX() {
		return minX;
	}
	
	public void setMinX(Double minX) {
		this.minX = minX;
	}
	
	public Double getMinY() {
		return minY;
	}
	
	public void setMinY(Double minY) {
		this.minY = minY;
	}
	
	public Double getMaxX() {
		return maxX;
	}
	
	public void setMaxX(Double maxX) {
		this.maxX = maxX;
	}
	
	public Double getMaxY() {
		return maxY;
	}
	
	public void setMaxY(Double maxY) {
		this.maxY = maxY;
	}
	
	public Rectangle(Double _minX, Double _minY, Double _maxX, Double _maxY) {
		this.minX = _minX;
		this.minY = _minY;
		this.maxX = _maxX;
		this.maxY = _maxY;
	}
	
}
