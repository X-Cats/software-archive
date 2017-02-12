package org.usfirst.frc.xcats.robot;

public class VisionData
{
    boolean result;
    int distance_in_inches;
    int angle_from_center_line_in_deg;
    int facing_angle_in_deg;
    
    public VisionData(boolean res, int distance, int afcl, int fa)
    {
        result = res;
        distance_in_inches = distance;
        angle_from_center_line_in_deg = afcl;
        facing_angle_in_deg = fa;    	
    }
    
    public boolean getResult()
    {
    	return result;
    }
    
    public int getDistanceInInches()
    {
    	return distance_in_inches;
    }
    
    public int getAngleFromCenterLineInDeg()
    {
    	return angle_from_center_line_in_deg;
    }
    
    public int getFacingAngleInDeg()
    {
    	return facing_angle_in_deg;
    }
}
