/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

/**
 *
 * @author mary farmer
 */
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStationLCD;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Watchdog;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author mary farmer
 */
public class Shooter {
    double wheelPidKp = 50;
    double wheelPidKi = 0;
    double wheelPidKd = 0;
    double wheelPidKf = 0;
    double wheelPidPIDperiod = 5000;

    private Jaguar mShooterFront;
    private Jaguar mShooterRear;
    private double mSpeed;
    private double mShooterFrontDistance;
    private String mShooterFrontStatusMsg="";
//    private DataLogger mlogfile;
    private boolean mShooterFrontEncoderAtSpeed=false;
    private boolean mWaitingForSpeed=false;
    private Timer mPrepTimer;
    private boolean mPrepShoot = false;
    private double mShooterFrontIntercept = -325.0;
    private double mShooterFrontSlope = 6050.0;
    private Relay mLightRelay;
    private AutoTarget mAutoTarget;
    private PIDController mShooterPIDController;
    private Relay mLoaderLever;
    private Relay mLoaderFeed;
    private boolean mbFeedInProgress;
    private boolean mbClearInProgress;
    private boolean mbManualFeedInProgress;
    private boolean mbFeedLeverMoving;
    private boolean mbCompleteCycle;
    private DigitalInput mDILoaderLever;
    private Timer mFeederTimer;
    private Timer mFeederTimer2;
    private DigitalInput mOpticalSensor;
    private boolean shooterOverride;
    private int mShotsMade = 0;
    private int mShotsTarget = 0;
    private Timer mPacingTimer;
    
    
    
    // For the non-PIDController version...
    private WheelEncoder mWheelEncoder;

    // For the master frisbee shooter PID controller
    private DigitalInput mDIMaster;
//    private WheelEncoder2 mEncoder2;
    private Encoder mEncoder2;
    
    private DigitalInput mBallLS;

    private boolean usePIDController;

    public Shooter(){
 
        mPacingTimer = new Timer();
        mPacingTimer.start();
        
        mShooterFront = new Jaguar(Enums.SHOOTER_SPEED_CONTROLLER_FRONT);
        mShooterRear = new Jaguar(Enums.SHOOTER_SPEED_CONTROLLER_REAR);

        if (Enums.USE_PID_CONTROLLER) {
            mDIMaster = new DigitalInput(Enums.OPTICAL_SENSOR_SLOT, Enums.OPTICAL_SENSOR_FRONT);
//            mEncoder2 = new WheelEncoder2(mDIMaster,mDIMaster);
            mEncoder2 = new Encoder(mDIMaster,mDIMaster);
            mEncoder2.start();  
            mEncoder2.reset();

            //**************************************************************
            // Since we are using a PID controller, theoretically it is not
            // necessary to create a WheelEncode2.  But, if we choose to do
            // so anyways so that we can monitor RPM, etc. this would
            // be the correct invocation...
            //**************************************************************


            mShooterPIDController = new PIDController(
                    wheelPidKp,
                    wheelPidKi,
                    wheelPidKd,
                    wheelPidKf,
                    mEncoder2, // PIDSource source
                    mShooterFront, // PIDOutput output
                    wheelPidPIDperiod);

            // NEED TO TUNE OTHER VALUES SO PID CONTROLLER KNOWS HOW TO CONTROL mShooterFront !!!
            // NEED TO TUNE OTHER VALUES SO PID CONTROLLER KNOWS HOW TO CONTROL mShooterFront !!!
            // NEED TO TUNE OTHER VALUES SO PID CONTROLLER KNOWS HOW TO CONTROL mShooterFront !!!
            // NEED TO TUNE OTHER VALUES SO PID CONTROLLER KNOWS HOW TO CONTROL mShooterFront !!!

            //mShooterPIDController.setOutputRange(x, y);
            mShooterPIDController.setInputRange(-7100.0,7100.0);
            mShooterPIDController.setSetpoint(0);
            mShooterPIDController.setPercentTolerance(5.0);

               mShooterPIDController.enable();
        }
        else {
            mWheelEncoder = new WheelEncoder();
            mWheelEncoder.start();
        }

        mAutoTarget = new AutoTarget();
        
        mLoaderLever = new Relay(Enums.LOADER_LEVER_RELAY);
        mLoaderLever.set(Relay.Value.kOff);
        
        mDILoaderLever = new DigitalInput(Enums.LOADER_LEVER_LIMIT);
        
        mLoaderFeed = new Relay(Enums.LOADER_FEED_RELAY);
        mLoaderFeed.set(Relay.Value.kOff);
        
        mFeederTimer = new Timer();
        mFeederTimer2 = new Timer();
        mOpticalSensor = new DigitalInput(Enums.SHOOTER_OPTICAL_SENSOR);


    }

    public void engageShooter() {
            if (Enums.USE_PID_CONTROLLER) {
                mShooterPIDController.setSetpoint(-7100.0 * mSpeed);
            }
            else {
                mShooterFront.set(-1.0 * mSpeed);
            }
        } 

    private double getShooterSpeed() {
        
        return -1.0 * mShooterFront.get();


    }

    private int getShooterEncoderAvg() {
        double sum = 0;
        int result = 0;
        for (int i=0;i<100;i++){
            if (Enums.USE_PID_CONTROLLER) {
                sum += mEncoder2.getRate();
            }
            else {
                sum += mWheelEncoder.getRate();
            }
           // Timer.delay(0.001);
        }

            result = (int) ( (sum / 100));

        //    System.out.println ("Encoder average "+ result);
        return result;
    }

    public void incrementPidKp(int incValue) {
        wheelPidKp += incValue;
    }
    public boolean getPrepToShoot() {
        return mPrepShoot;
    }

    public void prepToShoot() {
        if (!mPrepShoot) {
            //Turn off the shooter wheels, back off the acquisistion until timer times out
            mPrepShoot = true;
            if (Enums.USE_PID_CONTROLLER) {
                mShooterPIDController.setSetpoint(0);
            }
            else {
                mShooterFront.set(0);
            }
            mPrepTimer.reset();
            mPrepTimer.start();
//            mAcquisition.rollOut();
        }
    }

    private double getSpeedSPFromEncoder(double encoder) {
        //this returns the target speed if we pass in a  known encoder value
        double m = mShooterFrontSlope;
        double b = mShooterFrontIntercept;

        return ((encoder - b) / m);
    }

    private int getShooterEncoderSP(double speed) {
        //this is based on the experiment with the 10 second holds at encoder speed
        double m = mShooterFrontSlope;
        double b = mShooterFrontIntercept;

        return (int) (speed * m + b);
    }

    public void getShooterIntercept() {
        int avg1 = this.getShooterEncoderAvg();
        int avg2 = this.getShooterEncoderAvg();
        int avg3 = this.getShooterEncoderAvg();
        int avg4 = this.getShooterEncoderAvg();
        int avg5 = this.getShooterEncoderAvg();

        int target = getShooterEncoderSP(mSpeed);
        int avgE = (avg1 + avg2 + avg3 + avg4 + avg5) / 5;

        mShooterFrontIntercept = mShooterFrontIntercept + (target - avgE);
    }

    public void idleShooter() {
        System.out.println("Setting Idle Speed");

            if (Enums.USE_PID_CONTROLLER) {
                mShooterPIDController.setSetpoint(-7100.0 * Enums.SHOOTER_IDLE_SPEED);
            }
            else {
                mShooterFront.set(-1.0 * Enums.SHOOTER_IDLE_SPEED);
            }
        }
   

    public void setShooterDistance(double distance) {
        mShooterFrontDistance = distance;
    }

    public void setSpeedandEngage(double speed) {
        // System.out.println("Setting Speed: "+ speed);
        mSpeed = speed;
        
            if (Enums.USE_PID_CONTROLLER) {
                mShooterPIDController.setSetpoint(-5000); // (7100.0 * mSpeed);
                mShooterRear.set(mSpeed * 0.8); //these motors are rotating in opposite directions
            }
            else {
                mShooterFront.set(mSpeed);
                mShooterRear.set(mSpeed * 0.8); //these motors are rotating in opposite directions
            }
//            mShooterRear.set(mSpeed ); //these motors are rotating in opposite directions
        
    }

   
    public void setSpeed(double speed){
        mSpeed = speed;
    }

    public double getSpeed() {
        return mSpeed;
    }

    public Jaguar getShooterJaguar() {
        return mShooterFront;
    }

    //this assumes the speed has already been set on the wheel
    public void Shoot(boolean waitForSpeed) {
        mWaitingForSpeed = waitForSpeed;
        if (waitForSpeed) {
        } else {
            System.out.println("Shooting 2!");
//            mAcquisition.shoot();
        }
    }

    public void disable() {
        mSpeed = 0.0;
        setSpeedandEngage(mSpeed);
        mbFeedInProgress=false;
        mLoaderFeed.set(Relay.Value.kOff);
        mShotsMade = 0;
        mShotsTarget = 0;
        mPacingTimer.reset();
    }

    public void setLight(Relay.Value val) {
        if (mLightRelay != null) {
            mLightRelay.set(val);

        }
    }
    
    private void checkFeedStatusOrig(){
        if (mbFeedInProgress) {

            //Now we need to stroke the other relay
            if (mFeederTimer.get() > Enums.FEEDER_RUN_TIME && !mbFeedLeverMoving ) {  
                mLoaderFeed.set(Relay.Value.kOff);
                if (mFeederTimer.get() > Enums.FEEDER_RUN_TIME + Enums.FEED_LEVER_DELAY){
                mbFeedLeverMoving = true;
                mLoaderLever.set(Relay.Value.kReverse);
                Timer.delay(0.05);                    
                }
            } else {
                if (mDILoaderLever.get()){
                    mbCompleteCycle = true;
                }
                if (mbCompleteCycle &&  !mDILoaderLever.get() && mbFeedLeverMoving) {
                    mLoaderLever.set(Relay.Value.kOff);
                    mbFeedInProgress = false;
                    mbFeedLeverMoving = false;
                }
            }
        }
    }
    
    private void checkFeedStatus(){
        
        if (mbClearInProgress){
            if (mFeederTimer.get() > Enums.FEEDER_CLEAR_TIME){
                mLoaderFeed.set(Relay.Value.kOff);
                mbClearInProgress = false;
            }
            
        }
     if (mbManualFeedInProgress){
            if (mFeederTimer.get() > Enums.FEEDER_MANUAL_TIME){
                mLoaderFeed.set(Relay.Value.kOff);
                mbManualFeedInProgress = false;
            }            
        }        
        else if (mbFeedInProgress) {
            
            //clear everything
            if (mFeederTimer.get() > Enums.FEEDER_FAILSAFE_TIME){               
                mLoaderFeed.set(Relay.Value.kOff);
                mLoaderLever.set(Relay.Value.kOff);
                mbFeedInProgress = false;
                mbCompleteCycle = true;
                mbFeedLeverMoving = false;                
            }
            
            //Check to see if we have run the feeder long enough
            if (!mbFeedLeverMoving) {
                if (mFeederTimer.get() > Enums.FEEDER_RUN_TIME + Enums.FEED_LEVER_DELAY || !mOpticalSensor.get()) {
                    
                    if (!mOpticalSensor.get()){
                            mLoaderFeed.set(Relay.Value.kOff);
                            mbFeedInProgress = false;
                    }                    
                    
                } else if (mFeederTimer.get() > Enums.FEED_LEVER_DELAY) {
                    mLoaderFeed.set(Relay.Value.kForward);
                }
            } else {

                //if we are here, we are in the part of the feed cycle that moves the lever
                if (mDILoaderLever.get() && mFeederTimer.get() > 0.15) {
                    mbCompleteCycle = true;
                }
                if (mbCompleteCycle && !mDILoaderLever.get() && mbFeedLeverMoving) {
                    mLoaderLever.set(Relay.Value.kOff);
                    mbFeedLeverMoving = false;
                    System.out.println("Turning off lever");
                }
            }
        }
        else if (shooterOverride) {
            if (mFeederTimer.get() > Enums.SHOOTER_OVERRIDE_TIME){
                mLoaderLever.set(Relay.Value.kOff);
                shooterOverride = false;
            }
        }
    }
 
    public void clearJam(){
        if (!mbClearInProgress){
            mbClearInProgress = true;
            mLoaderFeed.set(Relay.Value.kReverse);
            mFeederTimer.reset();
            mFeederTimer.start();
        }
        
    }
    public void manualFeed(){
        if (!mbManualFeedInProgress){
            mbManualFeedInProgress = true;
            mLoaderFeed.set(Relay.Value.kForward);
            mFeederTimer.reset();
            mFeederTimer.start();
        }
        
    }

    public void feedFrisbee(){
        if (!mbFeedInProgress){
            mbFeedInProgress = true;
            
            //check the optical sensor, this will be false if there is a frisbee in position to shoot
            if (!mOpticalSensor.get()){
                mShotsMade +=1;            
                mbFeedLeverMoving=true;
                mbCompleteCycle = false;
                mLoaderLever.set(Relay.Value.kReverse);
            } else
            {
                //if we are here then there is no frisbee in position, cycle the feeder
                mbFeedLeverMoving = false;
//                mbCompleteCycle = true;
                mLoaderFeed.set(Relay.Value.kForward);                
            }
            
            mFeederTimer.reset();
            mFeederTimer2.reset();
            mFeederTimer.start();
        }
    }
    public void overrideShooter() {
        if (!shooterOverride){
            shooterOverride = true;
            mLoaderLever.set(Relay.Value.kReverse);
            mFeederTimer.reset();
            mFeederTimer.start();
        }
    }
     public void feedFrisbeeOrig(){        
        if (!mbFeedInProgress){
            mbFeedInProgress = true;
            mbFeedLeverMoving = false;
            mbCompleteCycle = false;
            mLoaderFeed.set(Relay.Value.kForward);
            mFeederTimer.start();
        }
    } 
    public void updateStatus(){
        SmartDashboard.putNumber("wheelPidKp", wheelPidKp);
        SmartDashboard.putNumber("wheelPidKi", wheelPidKi);
        SmartDashboard.putNumber("wheelPidKd", wheelPidKd);
        SmartDashboard.putNumber("wheelPidPIDperiod", wheelPidPIDperiod);
        
        SmartDashboard.putBoolean("Shooter Optical Sensor", mOpticalSensor.get());
        SmartDashboard.putNumber("Timer2", mFeederTimer2.get());
        SmartDashboard.putNumber("Shots Made",mShotsMade);
        SmartDashboard.putNumber("Shots Pace",mShotsTarget);
        
        if (mPacingTimer.get() > Enums.SHOOTER_PACE_TARGET){
            mShotsTarget+=1;
            mPacingTimer.reset();
        }
        
        
        checkFeedStatus();
        if (Enums.USE_PID_CONTROLLER) {
            SmartDashboard.putNumber("mSpeed", mSpeed);
            SmartDashboard.putNumber("mShooterPIDController.get()", mShooterPIDController.get());
            SmartDashboard.putNumber("mShooterPIDController.getSetpoint()", mShooterPIDController.getSetpoint());
            // SmartDashboard.putNumber("mShooterPIDController.getError()", mShooterPIDController.getError());
            //SmartDashboard.putBoolean("mShooterPIDController.onTarget()?", mShooterPIDController.onTarget());
            SmartDashboard.putNumber("mEncoder2.getRaw()", mEncoder2.getRaw());
            SmartDashboard.putNumber("mEncoder2.getRate()", mEncoder2.getRate());
            //mEncoder2.updateStatus();
        }   
        else {
            mWheelEncoder.updateStatus();
        }
        SmartDashboard.putBoolean("Feeder Leaver Limit",mDILoaderLever.get());
        if (mLoaderLever.get()==Relay.Value.kReverse){
            SmartDashboard.putBoolean("Feeder Lever Relay" , true);
        }else
        {
            SmartDashboard.putBoolean("Feeder Lever Relay" ,false);            
        }
        SmartDashboard.putBoolean("Feed Lever Moving", mbFeedLeverMoving);
        SmartDashboard.putBoolean("Feed in Progress", mbFeedInProgress);
        SmartDashboard.putBoolean("Complete Feeder Cycle",mbCompleteCycle);
        SmartDashboard.putNumber("Avg Encoder RPS",this.getShooterEncoderAvg());
        SmartDashboard.putNumber("shooterSetpoint", mShooterFront.get());
        
//        if (mAcquisition.getElevatorRollIn()){
//            if (mBallLS.get() && getShooterSpeed() < 0.25 ){
//               //must disable the elevator in this case because a ball has come up and the shooter is not moving
//                mAcquisition.stopElevator();
//                outputToUserScreen(DriverStationLCD.Line.kUser2,"TURN ON SHOOTER!!!");
//            } else {
//                outputToUserScreen(DriverStationLCD.Line.kUser2,"                  ");
//            }            
//        }


//        if (mPrepShoot) {
//            if (mPrepTimer.get() >= Enums.SHOOTER_PREP_TIME) {
//                mPrepTimer.stop();
//                mAcquisition.stop();
//                mPrepShoot = false;
//                if (Enums.IS_FINAL_ROBOT){
//                    mShooterFront.set(mSpeed);
//                }
//                else
//                    mShooterFront.set(-1.0 * mSpeed);
//            }
//        }

        //Check to see if we are at speed
//        int encAvg = 0;
//        if (getShooterSpeed() < 0.25 ){
//            //off
//            mShooterFrontEncoderAtSpeed = false;
//            if (mLightRelay != null){
//                mLightRelay.set(Relay.Value.kOn);
//            }
//        } else {
//            encAvg = this.getShooterEncoderAvg();
//            double encSP = this.getShooterEncoderSP(mSpeed);
//            double delta =  Math.max( Enums.SHOOTER_ENC_TOLERANCE_PCT * encSP,300);
//
//            //if (encSP > (encAvg - Enums.SHOOTER_ENC_TOLERANCE) && encSP < (encAvg + Enums.SHOOTER_ENC_TOLERANCE)){
//            
//            if (encSP > (encAvg - delta) && encSP < (encAvg + delta)){
//                //green
//                mShooterFrontEncoderAtSpeed=true;
//                if (mLightRelay != null){
//                mLightRelay.set(Relay.Value.kForward);
//
//                }
//            } else{
//                //red
//                mShooterFrontEncoderAtSpeed=false;
//                if (mLightRelay != null){
//                    mLightRelay.set(Relay.Value.kReverse);
//
//                }
//            }
//        }
//
//        if (mWaitingForSpeed){
//            if (mShooterFrontEncoderAtSpeed){
//                System.out.println("Shooting 1!");
////                mAcquisition.shoot();
//                mWaitingForSpeed=false;
//            }
//        }
//
////        SmartDashboard.putBoolean("BallReady LS",mBallLS.get());  // this tells us the ball is firing
////        SmartDashboard.putDouble("ShooterSetpoint", mSpeed);
////        SmartDashboard.putDouble("ShooterValue",getShooterSpeed());
////        SmartDashboard.putInt("ShooterEncoder",encAvg);
////        SmartDashboard.putDouble("ShooterEncoderThreshold", this.getShooterEncoderSP(mSpeed));
////        SmartDashboard.putBoolean("Shooter@Speed", mShooterFrontEncoderAtSpeed);
////        SmartDashboard.putBoolean("Prep Shoot",mPrepShoot);
//        
////        mAcquisition.updateStatus();
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
