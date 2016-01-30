/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author mary farmer
 */

public class WheelEncoder extends Thread {
    private Encoder mEncoder;
    private DigitalInput mDIMaster;

    private final int ENCODER_PULSES_PER_REV = 1;
    private final float ENCODER_SAMPLE_TIME_SEC = 0.2f;
    private final boolean xcatsRPSenabled = true;
    
    // value to be returned to shooting mechanism...
    private double wheelRPS = 0.0;

    private long prevEncoder = 0;
    private long deltaEncoder = 0;

    private long prevTime = 0;
    private long deltaTime = 0;
    
    public WheelEncoder(){
        mDIMaster = new DigitalInput(Enums.OPTICAL_SENSOR_SLOT, Enums.OPTICAL_SENSOR_FRONT);
        mEncoder = new Encoder(mDIMaster,mDIMaster);
        
        mEncoder.start();    
     
        mEncoder.reset();
        prevTime = System.currentTimeMillis();
        prevEncoder = mEncoder.getRaw();
    }
    
    public void updateStatus(){
        SmartDashboard.putBoolean("Shooter DI Encoder",mDIMaster.get());
        SmartDashboard.putNumber("Encoder", mEncoder.getRaw());
        SmartDashboard.putNumber("FRC EncoderRate", -mEncoder.getRate());
        SmartDashboard.putNumber("Xcats RPS", wheelRPS);
        SmartDashboard.putNumber("Xcats RPS line plot", wheelRPS);
        SmartDashboard.putNumber("deltaEncoder", deltaEncoder);
        SmartDashboard.putNumber("deltaTime", deltaTime);
        SmartDashboard.putNumber("Xcats RPM", -wheelRPS * 60.0);
    }   
    
    public void run() {
        System.out.println("WheelEncoder::run - entry");
        for(;;) {
            try {
                Timer.delay(ENCODER_SAMPLE_TIME_SEC);
                //sleep(1000);
                calcXcatsRPS();
            } catch (Exception e) {
                e.toString();
            }
        }
    }
    
    private void calcXcatsRPS () {
        // calculate how much time has expired since mEncoder was last reset()
        long currTime = System.currentTimeMillis();
        long currEncoder = mEncoder.getRaw();

        deltaTime = currTime - prevTime;
        deltaEncoder = currEncoder - prevEncoder;
        
        prevTime = currTime;
        prevEncoder = currEncoder;
        
        // get how many pulses occurred since last reset()...
        //deltaEncoder = - mEncoder.getRaw();
        
        // calculate RPS...
        wheelRPS = (1000 * ((float)deltaEncoder / (float)ENCODER_PULSES_PER_REV)) / (float)deltaTime;
        
        // If using Xcats RPS algorithm, go ahead and reset the encoder...
        if (xcatsRPSenabled) {
            // Now reset raw mEncoder count to 0...
            //mEncoder.reset();
        }
    }

    // @Override of the FRC getRate
    public double getRate () {
        return(wheelRPS);
    }
    public double getFrcRPS () {
        return(mEncoder.getRate());
    }
    
    public double getXcatsRPS () {
        return(wheelRPS);
    }
}
