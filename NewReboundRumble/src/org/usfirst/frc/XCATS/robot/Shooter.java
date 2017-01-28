package org.usfirst.frc.XCATS.robot;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStationLCD;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


/**
 *
 * @author mary farmer
 */
public class Shooter {
    private Jaguar mShooter;
    private Acquisition mAcquisition;
    private double mSpeed;
    private Encoder mShooterEncoder;
    private double mShooterDistance;
    private String mShooterStatusMsg="";
//    private DataLogger mlogfile;
    private boolean mShooterEncoderAtSpeed=false;
    private boolean mWaitingForSpeed=false;
    private Timer mPrepTimer;
    private boolean mPrepShoot = false;
    private double mShooterIntercept = -325.0;
    private double mShooterSlope = 6050.0;
    private Relay mLightRelay;

    private DigitalInput mBallLS;


    public Shooter(){
        mShooter = new Jaguar(Enums.SHOOTER_SPEED_CONTROLLER);
        mAcquisition = new Acquisition();
        mPrepTimer = new Timer();

        if (Enums.IS_FINAL_ROBOT){
            mShooterIntercept = Enums.SHOOTER_ENCODER_INTERCEPT_FINAL;
            mShooterSlope = Enums.SHOOTER_ENCODER_SLOPE_FINAL;
        } else {
            mShooterIntercept = Enums.SHOOTER_ENCODER_INTERCEPT_PROTO;
            mShooterSlope = Enums.SHOOTER_ENCODER_SLOPE_PROTO;
        }


        try
        {
            mShooterEncoder = new Encoder(Enums.SHOOTER_ENCODER_A,Enums.SHOOTER_ENCODER_B);
            mShooterEncoder.setDistancePerPulse(1.0);
            mShooterEncoder.start();

        }catch (Exception ex)
        {
            mShooterStatusMsg = ex.getMessage();
        }

        try
        {
            mLightRelay = new Relay(Enums.SHOOTER_LIGHT_RELAY);
            mLightRelay.set(Relay.Value.kOff);

        } catch (Exception ex)
        {

        }

        try
        {
            mBallLS = new DigitalInput(Enums.ELEVATOR_BALLREADY_LS);
        } catch (Exception ex){
            
        }


    }

    public void engageShooter(){
        if (Enums.IS_FINAL_ROBOT){
            mShooter.set(mSpeed);
        } else
            mShooter.set(-1.0 * mSpeed);
    }

    private double getShooterSpeed(){

        if (Enums.IS_FINAL_ROBOT){
            return mShooter.get();
        } else
        {
            return -1.0 * mShooter.get();
        }
        
    }

    private int getShooterEncoderAvg(){
        double sum=0;
        int result=0;
        for (int i=0;i<100;i++){
            sum += mShooterEncoder.getRate();
           // Timer.delay(0.001);
        }

        if (Enums.IS_FINAL_ROBOT){
            result = (int) (sum / 100);

        } else{
            result = (int) ( (sum / 100));
        }

    //    System.out.println ("Encoder average "+ result);
        return result;
    }

    public boolean getPrepToShoot(){
        return mPrepShoot;
    }
    public void prepToShoot(){
        if (!mPrepShoot){
            //Turn off the shooter wheels, back off the acquisistion until timer times out
            mPrepShoot = true;
            mShooter.set(0);
            mPrepTimer.reset();
            mPrepTimer.start();
            mAcquisition.rollOut();
        }
    }

    private double getSpeedSPFromEncoder(double encoder) {
        //this returns the target speed if we pass in a  known encoder value
        double m = mShooterSlope;
        double b = mShooterIntercept;

        return ((encoder - b)/m);
    }
    private int getShooterEncoderSP(double speed){
        //this is based on the experiment with the 10 second holds at encoder speed
        double m = mShooterSlope;
        double b = mShooterIntercept;
        
        return (int)(speed * m + b);
    }

    public void getShooterIntercept(){
        int avg1 = this.getShooterEncoderAvg();
        int avg2 = this.getShooterEncoderAvg();
        int avg3 = this.getShooterEncoderAvg();
        int avg4 = this.getShooterEncoderAvg();
        int avg5 = this.getShooterEncoderAvg();

        int target = getShooterEncoderSP(mSpeed);
        int avgE = (avg1 + avg2 + avg3 + avg4 + avg5)/5;

        mShooterIntercept = mShooterIntercept + (target - avgE);
    }

    public void idleShooter(){
        System.out.println("Setting Idle Speed");
        if (Enums.IS_FINAL_ROBOT){
            mShooter.set(Enums.SHOOTER_IDLE_SPEED);
        } else
            mShooter.set(-1.0 * Enums.SHOOTER_IDLE_SPEED);
    }

    public void setShooterDistance(double distance){
        mShooterDistance = distance;
    }
    public void setSpeedandEngage(double speed){
       // System.out.println("Setting Speed: "+ speed);
        mSpeed = speed;
        if (Enums.IS_FINAL_ROBOT){
            mShooter.set(mSpeed);

        }else
            mShooter.set(-1.0 * mSpeed);
    }

    public void setSpeed(double speed){
        mSpeed = speed;
    }
    public double getSpeed(){
        return mSpeed;
    }

    public Jaguar getShooterJaguar(){
        return mShooter;
    }

    public Acquisition getAcquisition(){
        return mAcquisition;
    }

    //this assumes the speed has already been set on the wheel
    public void Shoot(boolean waitForSpeed){
        mWaitingForSpeed = waitForSpeed;
        if (waitForSpeed){
        }
        else {
            System.out.println("Shooting 2!");
            mAcquisition.shoot();
        }
    }

    public void disable(){
        mSpeed = 0.0;
        if (mAcquisition != null){
            mAcquisition.disable();
        }
    }

    public void setLight(Relay.Value val){
        if (mLightRelay != null){
        mLightRelay.set(val);

        }
    }


    public void updateStatus(){


        if (mAcquisition.getElevatorRollIn()){
            if (mBallLS.get() && getShooterSpeed() < 0.25 ){
               //must disable the elevator in this case because a ball has come up and the shooter is not moving
                mAcquisition.stopElevator();
                outputToUserScreen(DriverStationLCD.Line.kUser2,"TURN ON SHOOTER!!!");
            } else {
                outputToUserScreen(DriverStationLCD.Line.kUser2,"                  ");
            }            
        }


        if (mPrepShoot) {
            if (mPrepTimer.get() >= Enums.SHOOTER_PREP_TIME) {
                mPrepTimer.stop();
                mAcquisition.stop();
                mPrepShoot = false;
                if (Enums.IS_FINAL_ROBOT){
                    mShooter.set(mSpeed);
                }
                else
                    mShooter.set(-1.0 * mSpeed);
            }
        }

        //Check to see if we are at speed
        int encAvg = 0;
        if (getShooterSpeed() < 0.25 ){
            //off
            mShooterEncoderAtSpeed = false;
            if (mLightRelay != null){
                mLightRelay.set(Relay.Value.kOn);
            }
        } else {
            encAvg = this.getShooterEncoderAvg();
            double encSP = this.getShooterEncoderSP(mSpeed);
            double delta =  Math.max( Enums.SHOOTER_ENC_TOLERANCE_PCT * encSP,300);

            //if (encSP > (encAvg - Enums.SHOOTER_ENC_TOLERANCE) && encSP < (encAvg + Enums.SHOOTER_ENC_TOLERANCE)){
            
            if (encSP > (encAvg - delta) && encSP < (encAvg + delta)){
                //green
                mShooterEncoderAtSpeed=true;
                if (mLightRelay != null){
                mLightRelay.set(Relay.Value.kForward);

                }
            } else{
                //red
                mShooterEncoderAtSpeed=false;
                if (mLightRelay != null){
                    mLightRelay.set(Relay.Value.kReverse);

                }
            }
        }

        if (mWaitingForSpeed){
            if (mShooterEncoderAtSpeed){
                System.out.println("Shooting 1!");
                mAcquisition.shoot();
                mWaitingForSpeed=false;
            }
        }

        SmartDashboard.putBoolean("BallReady LS",mBallLS.get());  // this tells us the ball is firing
        SmartDashboard.putDouble("ShooterSetpoint", mSpeed);
        SmartDashboard.putDouble("ShooterValue",getShooterSpeed());
        SmartDashboard.putInt("ShooterEncoder",encAvg);
        SmartDashboard.putDouble("ShooterEncoderThreshold", this.getShooterEncoderSP(mSpeed));
        SmartDashboard.putBoolean("Shooter@Speed", mShooterEncoderAtSpeed);
        SmartDashboard.putBoolean("Prep Shoot",mPrepShoot);
        
        mAcquisition.updateStatus();
    }
 private void outputToUserScreen(DriverStationLCD.Line line, String strMsg) {
        try {
//            System.out.println(strMsg);

            DriverStationLCD driverScreen = DriverStationLCD.getInstance();
            driverScreen.println(line, 1, strMsg.substring(0, Math.min(strMsg.length(), DriverStationLCD.kLineLength)));
            driverScreen.updateLCD();

        } catch (Exception ex) {
            //do nothing
        }

    }

}
