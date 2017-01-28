package org.usfirst.frc.XCATS.robot;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.DriverStationLCD;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class XCatDrive extends RobotDrive {
    private DriveEncoders m_encoders;
    private double m_wheelSpeeds[] = new double[kMaxNumberOfMotors];
    private double m_maxRate = 0.0;
    private boolean m_useSpeedController = false;
    private boolean m_swapBackToFront;
    private double mSpeedMax = 1.0;
    private boolean mHighSpeed = true;
    private boolean mArcadeDrive = false;



    XCatDrive(final int frontLeftMotor, final int frontRightMotor) {
        super(frontLeftMotor, frontRightMotor);

        invertMotors();

    }

    public void setEncoders(DriveEncoders newEncoders) {
        m_encoders = newEncoders;
    }

    public void setUseSpeedController(boolean useSpeedController) {
        m_useSpeedController = useSpeedController;
    }

    public boolean getUseSpeedController() {
        return m_useSpeedController;
    }

    public void setBackToFrontOrienation(boolean swapBackToFront){
        m_swapBackToFront = swapBackToFront;
    }
    public boolean getBackToFrontOrientation(){
        return m_swapBackToFront;
    }
    public void swapBackToFront() {
        m_swapBackToFront = !m_swapBackToFront;
        // need to invert the motors
        invertMotors();
    }

    public void disable(){
        //do this to disable
    }

    public int getAccumulator(){
        return m_encoders.getAccumulator();
    }

    public void resetAccumulator(){
        m_encoders.resetAccumulator();
    }

    private double getFeedbackSpeed(double lastValue, double currentValue, double rate) {
        double retVal = currentValue;
        double maxRate = 6000;  // start out at 6000
        double stepCorrection = 0.15;
        double deadband = 0.02;
        double effectiveValue;

        if (m_swapBackToFront) {
            rate = -1 * rate;
        }

        m_maxRate = Math.max(m_maxRate, rate);  // reestimate the max based on these encoders, they may have changed
        effectiveValue = rate / maxRate;

        if (Math.abs(lastValue - currentValue) <= deadband || currentValue < deadband) {
            retVal = currentValue;
        } else {

            //the rate cannot exceed 1.00
            retVal = Math.min((currentValue - effectiveValue) * stepCorrection + lastValue, 1.00);
           // System.out.println("NV " + retVal + " =  CV: " + currentValue + ", EV: " + effectiveValue + ", LV: " + lastValue);

        }

        return retVal;
    }
    /*
    public double getDistance(){
    return 0.0;
    }
     */

    public void setDriveMax(double maxSpeed){
        mSpeedMax = maxSpeed;
    }
    public void toggleDriveRange(){
        mHighSpeed = ! mHighSpeed;
        if (mHighSpeed)
        {
            mSpeedMax = 1.0;
            mArcadeDrive = false;
        }
        else
        {
            mSpeedMax = 1.0;
            mArcadeDrive = true;
        }
        
    }


    public void drive(GenericHID left, GenericHID right){
        if (mArcadeDrive){
            arcadeDrive(right);
        } else
        {
            tankDrive(left,right);
        }
        
    }

    public void arcadeDrive(GenericHID stick){
            super.arcadeDrive(stick.getY()* mSpeedMax,stick.getX() * mSpeedMax,false);
    }

    public void tankDrive(GenericHID leftValue,
                      GenericHID rightValue){
            super.tankDrive(leftValue.getY() * mSpeedMax, rightValue.getY() * mSpeedMax);
    }

    private void outputToUserScreen(String strMsg) {
        outputToUserScreen(strMsg, DriverStationLCD.Line.kUser5);
    }

    private void outputToUserScreen(String strMsg, DriverStationLCD.Line line) {
        try {
            System.out.println(strMsg);

            DriverStationLCD driverScreen = DriverStationLCD.getInstance();
            driverScreen.println(line, 1, strMsg.substring(0, Math.min(strMsg.length(), DriverStationLCD.kLineLength)));
            driverScreen.updateLCD();

        } catch (Exception ex) {
            //do nothing
        }
    }

    public void doNotInvertMotors() {
        setInvertedMotor(RobotDrive.MotorType.kRearLeft, m_swapBackToFront);
        setInvertedMotor(RobotDrive.MotorType.kRearRight, m_swapBackToFront);
    }

    public void invertMotors() {

        setInvertedMotor(RobotDrive.MotorType.kRearLeft, ! m_swapBackToFront);
        setInvertedMotor(RobotDrive.MotorType.kRearRight, ! m_swapBackToFront);

    }
    public void updateStatus(){
        SmartDashboard.putBoolean("ArcadeDrive", mArcadeDrive);
        SmartDashboard.putBoolean("Reversed", m_swapBackToFront);

    }

}
