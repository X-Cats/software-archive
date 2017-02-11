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
        int angle_from_center_in_deg = 0;
        int angle_from_target = 0;
    	
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
       
  	    Rect left = rectList.get(0);
  	    Rect right = rectList.get(0);
  	    Rect taller = rectList.get(0);
  	    
        if ( (rectList.size() < 2) )
        {
            System.out.println("VISION: NOT ACCURATE - OPERATOR CONTROL NEEDED!!!");
            result = false;
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
        
        // Calculate the distance based on the center point of the 2 rectangles
        int center_to_center_dist = ( ( right.x + (right.width / 2) ) - ( left.x + (left.width / 2) ) ); 
        distance_in_inches = 5116 / center_to_center_dist;
        
        result = true;
              
        long t1 = System.currentTimeMillis();
//        System.out.println("\nImage used: " + imageName);
        System.out.println("Distance to target: " + distance_in_inches + " inches");
        System.out.println("Done in " + (t1-t0) + " ms");

       	VisionData data = new VisionData(result, distance_in_inches, angle_from_center_in_deg, angle_from_center_in_deg);
       
        return data;
    }
}
