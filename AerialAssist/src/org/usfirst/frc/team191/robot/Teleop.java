/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Timer;
/**
 *
 * @author mary farmer
 */
public class Teleop {
    private RobotControls mRobotControls;

    Teleop(RobotControls rbControls){
        mRobotControls = rbControls;
    }
    public void  disable(){
        mRobotControls.disable();

    }

    // We do 3 things: drive the robot, check for button clicks and move the turret to target
    public void execute(){
        mRobotControls.drive();
        mRobotControls.updateStatus();
        Timer.delay(0.005); // this should get rid of the annoying message around Output not enabled often enough

    }
    public void init() {
        mRobotControls.init();

    }
   
}