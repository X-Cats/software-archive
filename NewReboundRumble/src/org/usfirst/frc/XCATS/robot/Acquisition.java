package org.usfirst.frc.XCATS.robot;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author mary farmer
 */
public class Acquisition {

    private Jaguar mAcquisition;
    private Jaguar mElevator;
    private Timer mTimer;
    private boolean mShootingEngaged = false;
    private DigitalInput mBallLS;
    private double mShooterElevatorTimeout = Enums.SHOOTER_ELEVATOR_TIMOUT;

    public Acquisition() {
        mAcquisition = new Jaguar(Enums.ACQUIS_FRONT_SPEED_CONTROLLER);
        mElevator = new Jaguar(Enums.ELEVATOR_SPEED_CONTROLLER);
        mTimer = new Timer();
        mBallLS = new DigitalInput(Enums.ELEVATOR_BALL_LS);
    }

    public void setShooterElevatorTimeout(double timeout){
        mShooterElevatorTimeout = timeout;
    }
    public void shoot() {
//        mTimer.reset();
//        mTimer.start();
//        mShootingEngaged = true;
        mElevator.set(Enums.ACQUISITION_SHOOT_SPEED);
        mAcquisition.set(Enums.ACQUISITION_SHOOT_SPEED);
    }

    public void elevatorRollIn() {
        //if (!mShootingEngaged) {
            mElevator.set(-Enums.ACQUISITION_SPEED);
        //}
    }

    public void elevatorRollOut() {
        mElevator.set(Enums.ACQUISITION_SPEED);
    }

    public void elevatorRollOutSlow() {
        mElevator.set(Enums.ACQUISITION_SLOW);
    }

    public void acqRollIn() {
        mAcquisition.set(Enums.ACQUISITION_SPEED);
    }

    public void acqRollOut() {
        mAcquisition.set(-Enums.ACQUISITION_SPEED);
    }
    public void acqRollOutSlow() {
        mAcquisition.set(-Enums.ACQUISITION_SLOW);
    }


    public void rollIn() {
        mAcquisition.set(Enums.ACQUISITION_SPEED);
        if (!mShootingEngaged) {
            mElevator.set(-Enums.ACQUISITION_SPEED);
        }
    }

    public void rollOut() {
        mAcquisition.set(-Enums.ACQUISITION_SPEED);
        mElevator.set(Enums.ACQUISITION_SPEED);
    }

    public void stopAcq() {
        mAcquisition.set(0.0);
    }

    public boolean getElevatorRollIn(){
        if (mElevator.get() < -0.1 ){
            return true;
        }
        else {
            return false;
        }
    }
    public void stopElevator() {
//        if (!mShootingEngaged) {
            mElevator.set(0.0);

 //       }
    }

    public void stop() {
        mAcquisition.set(0.0);
        if (!mShootingEngaged) {

            mElevator.set(0.0);
        }
    }

    public void disable(){
        //do this to disable/reset
    }

    public void updateStatus() {
        
        //if the limit switch fires then we start the timer
        if (mBallLS.get()){
            mTimer.reset();
            mTimer.start();
            mShootingEngaged = true;
            mElevator.set(0);
        }

        if (mShootingEngaged && mTimer.get() > mShooterElevatorTimeout) {
            SmartDashboard.putString("ShooterDebug", "Shooter Timeout expired");
            mTimer.stop();
            mShootingEngaged = false;
        }

        SmartDashboard.putBoolean("ElevatorLS",mBallLS.get());
        SmartDashboard.putDouble("ElevatorValue", mElevator.get());
        SmartDashboard.putDouble("AcquisitionValue", mAcquisition.get());
        SmartDashboard.putBoolean("ShootingEngaged", mShootingEngaged);
    }
}
