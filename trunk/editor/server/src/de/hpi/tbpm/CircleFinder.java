package de.hpi.tbpm;
/***************************************
 * Copyright (c) 2008
 * Helen Kaltegaertner
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 ****************************************/

import static name.audet.samuel.javacv.jna.cxcore.cvClearMemStorage;
import static name.audet.samuel.javacv.jna.cxcore.v21.*;
import static name.audet.samuel.javacv.jna.cv.v21.*;
import static name.audet.samuel.javacv.jna.cvaux.v21.*;
import static name.audet.samuel.javacv.jna.highgui.v21.*;
import name.audet.samuel.javacv.*;
import com.sun.jna.Native;

import java.awt.Polygon;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.HashSet;

public class CircleFinder {
	private IplImage img0;
	private IplImage img;

	private static int thresh = 50;
	private CvMemStorage storage;
	private ArrayList<CircleStructure> circles;
	private ArrayList<PolygonStructure> polygons;

	public CircleFinder(String imgSource, ArrayList<PolygonStructure> polygons) {
		this.img0 = cvLoadImage(imgSource, 1);
		this.polygons = polygons;
		if (this.img0 == null) {
			System.out.println(imgSource);
		}
		this.img = cvCloneImage(this.img0);

		this.storage = cvCreateMemStorage(0);
		this.circles = new ArrayList<CircleStructure>();
	}

	public ArrayList<CircleStructure> findCircles() {
		
		IplImage gray = cvCreateImage(cvGetSize(this.img), 8, 1);
		IplImage edge = cvCreateImage(cvGetSize(this.img), 8, 1 );

		int[] colours = {0,1};
		int thresh = 50;		//fenster: 30
		double edgeThresh = 1;

		for ( int i : colours){
			if ( i == 0 ) {
				cvCvtColor(this.img, gray, CV_BGR2GRAY);
			}
			else {
				cvSetImageCOI(this.img, i);
				cvCopy(this.img, gray, null);
				
			}
			
			cvThreshold( gray, gray, thresh, 255, CV_THRESH_BINARY);
			cvSmooth( gray, gray, CV_GAUSSIAN, 11, 11, 0, 0 );
			cvCanny( gray, edge, edgeThresh, edgeThresh * 3, 5);
		
//			CanvasFrame canvas = new CanvasFrame("pic");
//			canvas.showImage( gray );
			
			CvSeq results = cvHoughCircles(gray, this.storage.getPointer(),
					CV_HOUGH_GRADIENT, 
					1, this.img.height/10, 		// dp, min distance between centers
					5, 35, 			// before 5, 35
					0, 0); 	// min max radius
	
			float[] p;
			CvPoint center;
			for (int j = 0; j < results.total; j++) {
				p = cvGetSeqElem(results, j).getFloatArray(0, 3);
				if (p[2] < this.img.height / 10 && p[2] > this.img.height / 30) {
					center = cvPoint(Math.round(p[0]), Math.round(p[1]));
					System.out.println("radius: " + p[2]);
					this.circles.add(new CircleStructure(center, p[2]));
				}
			}
		}

		// release all the temporary images
		cvReleaseImage(gray.pointerByReference());
		cvReleaseImage(edge.pointerByReference());
		
		System.out.println("circles total: " + this.circles.size());
		this.filterRedundancy();
		System.out.println("circles filtered: " + this.circles.size());
		
		// clear memory storage - reset free space position
		cvClearMemStorage(this.storage);
		
		return this.circles;

	}
	
	/**
	 * Open Image in pop up using canvas facility
	 */
	public void showCanvas(){
		CanvasFrame canvas = new CanvasFrame("pic");
		canvas.showImage( this.img );

		// release both image
		cvReleaseImage(this.img.pointerByReference());
		cvReleaseImage(this.img0.pointerByReference());
	}

	/**
	 * removes overlapping circles that were found due to
	 * applying several filters
	 * @param n
	 */
	private void filterRedundancy() {
		
		for ( int i = 0; i < this.circles.size(); i++ ) {
			
			ArrayList<CircleStructure> duplicates = new ArrayList<CircleStructure>();			
			CircleStructure c1 = this.circles.get(i);
			// instantiate Ellipse with width = height to have a circle
			Ellipse2D.Double e1 = new Ellipse2D.Double(
								c1.getCenter().x - c1.getRadius(), 
								c1.getCenter().y - c1.getRadius(), 
								c1.getRadius()*2, 
								c1.getRadius()*2);
			
			for ( int k = 0; k < this.polygons.size(); k++) {
				if ( e1.getBounds2D().intersects( this.polygons.get(k).getPolygon().getBounds2D() ) ) {
					duplicates.add(c1);
					i--;
					break;
				}
			}
			// duplicates need only be detected if the circle does not intersect one of the polygons
			if (duplicates.size() == 0) {
				// check for all other circles if their center is enclosed by th i-th circle))
				for ( int j = i+1; j < this.circles.size(); j++) {
					CircleStructure c2 = this.circles.get(j);
					// instantiate Ellipse with width = height to have a circle
					Ellipse2D.Double e2 = new Ellipse2D.Double(
									c2.getCenter().x - c2.getRadius(), 
									c2.getCenter().y - c2.getRadius(), 
									c2.getRadius()*2, 
									c2.getRadius()*2);
					if (e1.intersects(e2.getBounds2D()))
						duplicates.add(c2);
				}
			}
			this.circles.removeAll(duplicates);
		}

	}
}
