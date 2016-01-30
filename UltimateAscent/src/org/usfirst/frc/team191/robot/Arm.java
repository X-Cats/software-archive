/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author x-cats
 */
public class Arm {
    
    private DigitalInput mTopLS;
    private DigitalInput mBottomLS;
    private Jaguar mJaguar;
    
    public Arm(){
        mTopLS = new DigitalInput(Enums.ARM_TOPLS);
        mBottomLS = new DigitalInput(Enums.ARM_BOTTOMLS);
        mJaguar = new Jaguar(Enums.ARM_JAGUAR);
    }
    
    public void updateStatus(){
        
        SmartDashboard.putBoolean("Arm Top LS", mTopLS.get());
        SmartDashboard.putBoolean("Arm Bottom LS", mBottomLS.get());
        
        if(isAtTop() && mJaguar.get() > 0.0){
            mJaguar.set(0.0);
        }
        if(isAtBottom() && mJaguar.get() < 0.0){
            mJaguar.set(0.0);
        }
    }
    
    public void setSpeed(double speed){
        
        //The motor for this is reveresed
       // speed = -speed;
        if(isAtTop() && -speed > 0.0){
            mJaguar.set(0.0);
        }
        else if(isAtBottom() && -speed < 0.0){
            mJaguar.set(0.0);
        }
        else {
            mJaguar.set(speed);
        }
    }

    public boolean isAtTop() {
        if(mBottomLS.get())
            return true;
        else
            return false;
    }
    
    public boolean isAtBottom() {
        if(mTopLS.get())
            return true;
        else
            return false;
    }
}
