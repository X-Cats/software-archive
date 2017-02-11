package org.usfirst.frc.xcats.robot;

public class VisionData
{
    boolean result;
    int distance_in_inches;
    int angle_from_center_in_deg;
    int angle_from_target;
    
    public VisionData(boolean res, int distance, int afc, int aft)
    {
        result = res;
        distance_in_inches = distance;
        angle_from_center_in_deg = afc;
        angle_from_target = aft;    	
    }
    
    public boolean getResult()
    {
    	return result;
    }
    
    public int getDistanceInInches()
    {
    	return distance_in_inches;
    }
    
    public int getAngleFromCenterInDeg()
    {
    	return angle_from_center_in_deg;
    }
    
    public int getAngleFromTarget()
    {
    	return angle_from_target;
    }
}
