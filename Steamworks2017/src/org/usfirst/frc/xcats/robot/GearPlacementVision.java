package org.usfirst.frc.xcats.robot;

import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class GearPlacementVision
{
	public void GearPlacementVision()
	{
		
	}
    
    public VisionData processImage(Mat _mat)
    {
    	boolean result = false;
        int distance_in_inches = 0;
        int angle_from_center_line_in_deg = 0;
        int facing_angle_in_deg = 0;
       	VisionData visionData = new VisionData();

        System.loadLibrary( Core.NATIVE_LIBRARY_NAME);
        long t0 = System.currentTimeMillis();
        System.out.println("Extracting distance/angle from processed image...\n");

        GripPipeline gp = new GripPipeline();
        gp.process(_mat);   
        
  	    ArrayList<Rect> rectList = new ArrayList<Rect>();
        int rectNum = 0;
        rectList = null;
  	    
        for (MatOfPoint mop : gp.findContoursOutput())
        {
            rectNum++;

            Rect rect = Imgproc.boundingRect(mop);
            rectList.add(rect);
            System.out.println("Rectangle #" + rectNum
          		              + ": Height=" + rect.height
          		              + ", Width=" + rect.width
          		              + ", Area=" + rect.area()
          		              + ", X=" + rect.x
          		              + ", Y=" + rect.y);
        }
        
        if (rectList == null)
        {
            System.out.println("VISION: NOT ACCURATE - OPERATOR CONTROL NEEDED!!!");
        	visionData.setResult(false);
        	return visionData;
        }
        
  	    Rect left = null;
  	    Rect right = null;
  	    Rect taller = null;
  	    
        if ( (rectList.size() < 2) )
        {
            System.out.println("VISION: NOT ACCURATE - OPERATOR CONTROL NEEDED!!!");
        	visionData.setResult(false);
        	return visionData;
        }
        else if (rectList.size() == 2)
        {  
      	    if (rectList.get(0).x < rectList.get(1).x)
      	    {
      		    left = rectList.get(0);
      		    right = rectList.get(1); 
      	    }
      	    else
      	    {
      	      left = rectList.get(1);
      		  right = rectList.get(0); 
      	    }
        }
        else
        {
      	    System.out.println("Too many reflections found.  Reducing to two.");
      	    double big = 0, bigger = 0, biggest = 0;
      	    int big_i = 0, bigger_i = 0, biggest_i = 0;
      	    int i = 0;
      	    Double area1, area2; 
            for (Rect rr : rectList)
            {
      		    if (rr.area() > biggest)
      		    {
      			    big = bigger; bigger = biggest; biggest = rr.area();
      			    big_i = bigger_i; bigger_i = biggest_i; biggest_i = i;    			  
      		    }
      		    else if (rr.area() > bigger)
      		    {
      			    big = bigger; bigger = rr.area();
      			    big_i = bigger_i; bigger_i = i;
      		    }
      		    else if (rr.area() > big)
      		    {
      			    big = rr.area();
      			    big_i = i;
      		    }
      		    
      		    i++;
      	    }
      	    
            System.out.println("Big  Bigger  Biggest: " + 
      	    big_i + "  " + bigger_i + "  " + biggest_i);
      	    taller = rectList.get(biggest_i);
            left = (rectList.get(biggest_i).x < rectList.get(bigger_i).x) ? 
      			  rectList.get(biggest_i) : rectList.get(bigger_i);
      	    right = (rectList.get(biggest_i).x >= rectList.get(bigger_i).x) ? 
      			   rectList.get(biggest_i) : rectList.get(bigger_i);    
        }
        
        // Find the center pixels of the left and right tape
        int center_of_left_tape = (left.x + (left.width / 2)); 
        int center_of_right_tape = (right.x + (right.width / 2));
        
        // Calculate the distance based on the center point of the 2 rectangles        
        int center_to_center_dist = (center_of_right_tape - center_of_left_tape); 
        distance_in_inches = 5116 / center_to_center_dist;
        // Subtract fixed distance from Tape to tip of Pin
        visionData.setDistanceInInches((int)(distance_in_inches - Enums.PEG_LENGTH 
        		- Enums.PEG_CHANNEL_DEPTH - Enums.CAMERA_DIST_FROM_FRONT));
        
        // Calculate the Facing Angle based on center pixel of tape compared to center of image captured by camera
        int center_pixel_between_tape = (center_of_left_tape + (center_to_center_dist / 2));
        int center_pixel_of_camera = Enums.CAMERA_X_PIXELS_TOTAL / 2;
        int offset_to_center_of_camera = center_pixel_between_tape - center_pixel_of_camera;
        facing_angle_in_deg = offset_to_center_of_camera / Enums.PIXEL_PER_DEGREE;
        
        if (facing_angle_in_deg < 0)
        {
            System.out.println("Robot has to be rotated left...");       	
        }
        else
        {
            System.out.println("Robot has to be rotated right...");       	
        }
        
        visionData.setFacingAngleInDeg(facing_angle_in_deg);
        
		// Compute the ratio of the width of the two main rectangles.
		// Because the peg delivery channel may obstruct the full view of 
		// both tape/rect's when the robot is off the centerline by > N degrees (TBD), 
		// this computation *should* be related to our desired "AngleFromCenterline".
		// We will make a convention that the ratio is positive when the angle is 
		// counterclockwise from the centerline, negative otherwise.
		double rw = right.width; double lw = left.width;
		double widthRatio = (rw > lw) ? lw / rw : rw / lw;

		widthRatio = (rw > lw) ? widthRatio : - widthRatio;
		System.out.println("\nLEFT width = " + left.width + ", RIGHT width = " + right.width + 
				", Width ratio = " + widthRatio);
		
		// Compute the ratio of the area of the two main rectangles.
		double ra = right.area(); double la = left.area();
		double areaRatio = (ra > la) ? la / ra : ra / la;

		areaRatio = (ra > la) ? areaRatio : - areaRatio;
		System.out.println("LEFT area = " + left.area() + ", RIGHT area = " + right.area() + 
				", Area ratio = " + areaRatio);

		// Determine which zone we're in, use the area ratio
		// if areas are close (>= 75 percent), then zone 1
		// else zone 2
		
		// Determine left or right of center line
		// if area ratio is negative, then robot is left of centerline as it faces the peg
			// Rotate robot right ?? degrees
			// Move forward ?? inches to centerline
			// Rotate robot 90 degrees to the left
		// else robot is right of centerline as it faces the peg
			// Rotate robot left ?? degrees
			// Move forward ?? inches to centerline
			// Rotate robot 90 degrees to the right		
		
//		visionData.setAngleFromCenterLineInDeg(angle_from_center_line_in_deg);
        
        visionData.setResult(true);
              
        long t1 = System.currentTimeMillis();
//        System.out.println("\nImage used: " + imageName);
        System.out.println("Distance to target: " + distance_in_inches + " inches");
        System.out.println("Facing angle to target: " + facing_angle_in_deg + " degrees");
        System.out.println("Done in " + (t1-t0) + " ms");

        return visionData;
    }
}
