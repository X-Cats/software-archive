package org.usfirst.frc.XCATS.robot;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author mary farmer
 */
public class Arm {
    private Solenoid mUp;
    private Solenoid mDown;
    private boolean mOpenCheck = true;
    private Timer mTimer;
    private boolean mStrokingSolenoid=false;

    public Arm (){
        System.out.println("SolenoidInit");
        mUp = new Solenoid( Enums.SOLENOID_RELAY_OPEN);
        mDown = new Solenoid(Enums.SOLENOID_RELAY_CLOSE);
        mTimer = new Timer();

    }
    public void down(){
        if (!mStrokingSolenoid){
            mStrokingSolenoid = true;
            System.out.println("arm down");
            mTimer.reset();
            mTimer.start();
        }
    }
    public void up() {
        if (!mStrokingSolenoid){
            mStrokingSolenoid = true;
            System.out.println("arm up");
            mTimer.reset();
            mTimer.start();
        }
   }
    public void toggle(){
        if(mOpenCheck)
            down();
        else
            up();
    }

    public void disable(){
        mStrokingSolenoid = false;
       // up();

    }
    
    public void updateStatus(){
        if (mStrokingSolenoid){

            if (mOpenCheck){
                //Moving the Arm down
                if (mTimer.get() < 0.3) {
                    mUp.set(true);
                } else {
                    mUp.set(false);
                    mStrokingSolenoid = false;
                    mOpenCheck = false;
                }
            } else {
                //Moving the arm up
                if (mTimer.get() < 0.3) {
                    mDown.set(true);
                } else {
                    mDown.set(false);
                    mStrokingSolenoid = false;
                    mOpenCheck = true;
                }
            }
        }
        SmartDashboard.putBoolean("Arm Up", mOpenCheck);
    }

}
