package org.computer.knauss.reqtDiscussion.ui.visualization;

import java.awt.Rectangle;

public interface IZoomable {

	public void setZoomFactor(double zoom);
	
	public double getZoomFactor();
	
	public void zoomToFitRect(Rectangle rect);
	
}
