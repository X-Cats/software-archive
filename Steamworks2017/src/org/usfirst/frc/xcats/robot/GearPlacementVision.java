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
        
        if (rectList.size() == 0)
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
        
        result = true;
              
        long t1 = System.currentTimeMillis();
//        System.out.println("\nImage used: " + imageName);
        System.out.println("Distance to target: " + distance_in_inches + " inches");
        System.out.println("Facing angle to target: " + facing_angle_in_deg + " degrees");
        System.out.println("Done in " + (t1-t0) + " ms");

        return visionData;
    }
}
