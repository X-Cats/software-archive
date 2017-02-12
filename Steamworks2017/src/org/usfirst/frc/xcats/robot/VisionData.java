package org.usfirst.frc.xcats.robot;

public class VisionData
{
    boolean result = false;
    int distance_in_inches = 0;
    int angle_from_center_line_in_deg = 0;
    int facing_angle_in_deg = 0;
    
    public VisionData()
    {}
    
    public void setResult(boolean res)
    {
    	result = res;
    }
    
    public void setDistanceInInches(int distance)
    {
    	distance_in_inches = distance;
    }
    
    public void setAngleFromCenterLineInDeg(int angle_from_center)
    {
    	angle_from_center_line_in_deg = angle_from_center;
    }
    
    public void setFacingAngleInDeg(int facing_angle)
    {
    	facing_angle_in_deg = facing_angle;
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
