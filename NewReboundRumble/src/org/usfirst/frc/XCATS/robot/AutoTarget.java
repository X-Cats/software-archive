package org.usfirst.frc.XCATS.robot;

///*
//* To change this template, choose Tools | Templates
//* and open the template in the editor.
//*/
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStationEnhancedIO;
import edu.wpi.first.wpilibj.DriverStationLCD;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.camera.AxisCamera;
import edu.wpi.first.wpilibj.camera.AxisCameraException;
import edu.wpi.first.wpilibj.image.NIVisionException;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Watchdog;
import edu.wpi.first.wpilibj.image.BinaryImage;
import edu.wpi.first.wpilibj.image.ColorImage;
import edu.wpi.first.wpilibj.image.CriteriaCollection;
import edu.wpi.first.wpilibj.image.NIVision.MeasurementType;
import edu.wpi.first.wpilibj.image.ParticleAnalysisReport;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
*
* @author xcats
*/
public class AutoTarget {

 private AxisCamera mCamera;
 private CriteriaCollection mcc;      // the criteria for doing the particle filter operation
// private double cameraPosition = 0.5; //The camera servo position ranges from 1 being full down/left to 0 full up/right.
// private double cameraRotatePos = 0.5;
 //private Servo cameraSwivel;
 private Servo cameraSwivelTilt;
// private double mCameraRotateSP = 0.0;
 private double mTurretVoltageSP = 5.0;
 private boolean mAutoTargetEngaged = false;
 private boolean mDebugImages = Enums.DEBUG_CAMERA_IMAGES;  //false for competition
 private AnalogChannel mTurretVoltage;
 private double mTurretVoltageMax =0.0;
 private double mTurretVoltageMin = 0.0;
 private double mTurretVoltageCenter = 2.97;
 private DigitalInput mTurretLSRight;
 private DigitalInput mTurretLSLeft;
 private Jaguar mTurretSC;
 private double mTurretSpeed = Enums.TURRET_HIGH_SPEED;
 private String mAutoTargetStatusMsg = "";
 private double mLastAutoTargetTime = 0.0;
 private int mNumParticlesFound = 0;
 private boolean mAutoTargetSuccess = false;
 private boolean mAutoShootEnabled = false;
 private double mSpeedOffset = 0.0;
 private double mDistance = 0.0;
 private boolean mShootingEngaged = false;
 private Shooter mShooter;
 private int mNumIterations = 0;
 private boolean mAutoSpeed = true;
 private boolean mAutoTargetStarted = false;
 private double mCalcSpeed = 0;
 private DriverStationEnhancedIO mdsIO;
 private int mImageCaptureCt =10;

 public void initCamera() {

 }

 public AutoTarget() {

     //This is the enhanced IO on the driverstation
     mdsIO = DriverStation.getInstance().getEnhancedIO();

     try {
         if (Enums.IS_FINAL_ROBOT){
             mTurretVoltageMin = Enums.TURRET_MIN_VOLT_FINAL;
             mTurretVoltageMax = Enums.TURRET_MAX_VOLT_FINAL;
         } else {
             mTurretVoltageMin = Enums.TURRET_MIN_VOLT_PROTO;
             mTurretVoltageMax = Enums.TURRET_MAX_VOLT_PROTO;
         }


         updateCenterVoltage();

         //Shooter
         mShooter = new Shooter();

         if (Enums.CAMERA_ACTIVE) {
             //DO NOT FORGET: when using the servo, connecting the PWM requires the jumper to be present on the two pins next to the 3 PWM pins for the power to be properly set
             if (Enums.CAMERA_SERVOS_ACTIVE) {
                 //cameraSwivel = new Servo(Enums.CAMERA_SERVO_MODULE, Enums.CAMERA_SWIVEL_PWM); //put the camera servo on pwm 7. NOTE: this needs a jumper on the sidecar to get power to it
                 cameraSwivelTilt = new Servo(Enums.CAMERA_SERVO_MODULE, Enums.CAMERA_SWIVELUD_PWM); //put the camera servo on pwm 7. NOTE: this needs a jumper on the sidecar to get power to it

                 //start in the middle (home)
                 //cameraSwivel.set(0.50);
                 cameraSwivelTilt.set(Enums.CAMERA_SWIVEL_INIT_POS);

             }

//             Timer.delay(2.0);
             try {
                 mCamera = AxisCamera.getInstance();
                 mCamera.writeResolution(AxisCamera.ResolutionT.k640x480);
                 mCamera.writeBrightness(50);
                 mCamera.writeCompression(30);
                 mCamera.writeExposureControl(AxisCamera.ExposureT.flickerfree60);
                 mCamera.writeColorLevel(100);

             } catch (Exception ex) {
                 ex.printStackTrace();
             }
         }
     } catch (Exception ex) {
     }
     mcc = new CriteriaCollection();

     //this is the criteria that will be used to find the targets
     mcc.addCriteria(MeasurementType.IMAQ_MT_AREA, 0, Enums.PARTICLE_FILTER_LIMIT, true);
     mAutoTargetStatusMsg = "AutoTarget Init";
     System.out.println("AutoTarget Init");

     //Limit switches on the turret
     try {
         mTurretLSLeft = new DigitalInput(Enums.TURRET_LS_LEFT);
         mTurretLSRight = new DigitalInput(Enums.TURRET_LS_RIGHT);
//         mTurretLSCenter = new DigitalInput(Enums.TURRET_LS_CENTER);
     } catch (Exception ex) {
         System.out.println("Turret LS not found");
     }

     //Voltage channel on the turret potentiometer
     try {
         mTurretVoltage = new AnalogChannel(Enums.TURRET_VOLTAGE);
     } catch (Exception ex) {
         System.out.println("Turret Voltage Channel Not detected");
     }

     //Turret Speed Controller
     try {
         mTurretSC = new Jaguar(Enums.TURRET_SPEED_CONTROLLER);
         System.out.println("Turret Speed Controller initialized");
     } catch (Exception ex) {
         System.out.println("Turret Speed Controller not detected");
     }

//     mTurretSpeed = Enums.TURRET_HIGH_SPEED;
 }

 public Shooter getShooter() {
     return mShooter;
 }

// public void setTurretOffset(double offset) {
//     mTurretOffset = offset;
// }

 public void setSpeedOffset(double offset) {
     mSpeedOffset = offset;
 }

 public void setAutoShoot(boolean autoShoot) {
     mAutoShootEnabled = autoShoot;
 }

 public void setAutoSpeed(boolean autocalcSpeed) {
     mAutoSpeed = autocalcSpeed;
 }

 private void updateCenterVoltage() {
     if (Enums.IS_FINAL_ROBOT) {
         mTurretVoltageCenter = ((mTurretVoltageMax - mTurretVoltageMin) / 2.0) + mTurretVoltageMin;
     } else {
         mTurretVoltageCenter = 1.75;
     }
     System.out.println("Center Voltage:" +mTurretVoltageCenter );
 }

 public void disable() {
     //return to init positions
     if (Enums.CAMERA_SERVOS_ACTIVE) {
         //cameraSwivel.set(0.5);
         cameraSwivelTilt.set(Enums.CAMERA_SWIVEL_INIT_POS);
     }

     this.moveTurretToCenter();
     mImageCaptureCt = 10;

     if (mShooter != null) {
         mShooter.disable();
     }
     mAutoTargetEngaged = false;
 }

 public void moveCameraUp() {
     if (Enums.CAMERA_SERVOS_ACTIVE) {
         cameraSwivelTilt.set(cameraSwivelTilt.get() + 0.01);
     }
 }

 public void moveCameraDown() {
     if (Enums.CAMERA_SERVOS_ACTIVE) {
         cameraSwivelTilt.set(cameraSwivelTilt.get() - 0.01);
     }
 }

// public void moveCameraLeft() {
//     if (Enums.CAMERA_SERVOS_ACTIVE) {
//         cameraSwivel.set(cameraSwivel.get() - 0.01);
//     }
// }
//
// public void moveCameraRight() {
//     if (Enums.CAMERA_SERVOS_ACTIVE) {
//         cameraSwivel.set(cameraSwivel.get() + 0.01);
//     }
// }

 public void setTurretSpeedHi() {
     mTurretSpeed = Enums.TURRET_HIGH_SPEED;
 }

 public void setTurretSpeedLow() {
     mTurretSpeed = Enums.TURRET_LOW_SPEED;
 }

 public void moveTurretToCenter() {
     setTurretSP(mTurretVoltageCenter - mTurretVoltage.getVoltage());
 }

 public void stopTurret() {
     mTurretSC.set(0.0);
 }

 public void calcSpeedOffset() {
     //use the current encoder average to calculate a speed offset
 }

 public void setTurretSPNew(double deltaFromCenter){
     double delta = 0;
     double newTarget = 0;
     newTarget = mTurretVoltageCenter + deltaFromCenter; //this is where we want to drive to
     delta = newTarget - mTurretVoltage.getVoltage() ; // this is our current setting
     System.out.println("Setting Turret to relative to current position: "+delta);
     setTurretSP(delta);// this is the delta from our current setting, which is how this function works
 }

 public void setTurretSP(double sp) {
     //Calculate the voltage to set, based on a delta input from where we are. factor in the offset.
     double offset=0.0;
     offset = getScaledIO();
     System.out.println ("Turret Offset: "+offset);

     mTurretVoltageSP = mTurretVoltage.getVoltage() + sp + offset;
     if (offset + sp + mTurretVoltage.getVoltage() < mTurretVoltageMin) {
         mTurretVoltageSP = mTurretVoltageMin;
     } else if (offset + sp + mTurretVoltage.getVoltage() > mTurretVoltageMax) {
         mTurretVoltageSP = mTurretVoltageMax;
     } else {
         mTurretVoltageSP = mTurretVoltage.getVoltage() + sp + offset;
     }
     mAutoTargetEngaged = true;
 }

// public void setTurretSpeedTest(double speed) {
//     mTurretSC.set(speed);
// }
 public void moveTurretRight() {
     //we have limits switches that must be polled to ensure we do not burn the motor
     //System.out.println("moveTurretRight");

     //Every time we pass midpoint update our voltage so that we are accurate
     //theoretically it should never see the Left limit switch, but someone could have a motor backwards
     if (mTurretLSRight.get()) {
         mTurretSC.set(0.0);
         mAutoTargetEngaged = false;
         mTurretVoltageMax = mTurretVoltage.getVoltage();
         updateCenterVoltage();
     } else {
         mTurretSC.set(mTurretSpeed * 1.0);
     }
 }

 public void moveTurretLeft() {
     //we have limits switches that must be polled to ensure we do not burn the motor

     //Every time we pass midpoint update our voltage so that we are accurate
     //theoretically it should never see the Right limit switch, but someone could have a motor backwards
     if (mTurretLSLeft.get()) {
         mTurretSC.set(0.0);
         mAutoTargetEngaged = false;
         mTurretVoltageMin = mTurretVoltage.getVoltage();
         updateCenterVoltage();
     } else {
         mTurretSC.set(mTurretSpeed * -1.0);
     }
 }

 //this method moves the camera a delta value of sp added to the current position
// public void setCameraSP(double sp) {
//     if (Enums.CAMERA_SERVOS_ACTIVE) {
//
//         mCameraRotateSP = cameraSwivel.get() + sp;
//         if (sp + cameraSwivel.get() < 0.0) {
//             mCameraRotateSP = 0;
//         } else if (sp + cameraSwivel.get() > 1.0) {
//             mCameraRotateSP = 1.0;
//         } else {
//             mCameraRotateSP = cameraSwivel.get() + sp;
//         }
//
//         mAutoTargetEngaged = true;
//     }
// }

 //we are using this to simulate the turret,
 //we would have a similar set of methods to move the turret to SP
// public void moveCameraToSP() {
//     //  0.4 - 0.35 = -0.15, so move left
//     //  0.6 - 0.35 = 0.15, so move right
//     if (Enums.CAMERA_SERVOS_ACTIVE) {
//
//         if (mAutoTargetEngaged) {
//             double delta = cameraSwivel.get() - mCameraRotateSP;
//
//             if (delta > 0.0 && Math.abs(delta) > 0.01) {
//                 moveCameraLeft();
//             } else if (delta < 0.0 && Math.abs(delta) > 0.01) {
//                 moveCameraRight();
//             } else {
//                 mAutoTargetEngaged = false;
//             }
//
//         }
//     }
// }

 public void shooterAcqRollIn() {
     mShooter.getAcquisition().rollIn();
 }

 public void shooterAcqRollOut() {
     mShooter.getAcquisition().rollOut();
 }

 public void shooterAcqStop() {
     mShooter.getAcquisition().stop();
 }

 public void moveTurretToSP() {
     //  0.4 - 0.35 = -0.15, so move left
     //  0.6 - 0.35 = 0.15, so move right

     if (mAutoTargetEngaged && !mTurretLSRight.get() && !mTurretLSLeft.get() ) {
         double delta = mTurretVoltage.getVoltage() - mTurretVoltageSP;
         //System.out.println("Moving to target: "+delta);

         if (delta > 0.0 && Math.abs(delta) > 0.01) {
             moveTurretLeft();
         } else if (delta < 0.0 && Math.abs(delta) > 0.01) {
             moveTurretRight();
         } else {
             System.out.println("Turret @ SP");
             mTurretSC.set(0.0);
             mAutoTargetStarted = false;
             mAutoTargetEngaged = false;
             mTurretSpeed = Enums.TURRET_HIGH_SPEED;
             if (mShootingEngaged) {
                 if (Enums.RETARGET_ACTIVE && mNumIterations == 2) {
                     outputToUserScreen(DriverStationLCD.Line.kUser3, "SHOOT!!!!");
                     if (mAutoShootEnabled) {
                         mShooter.Shoot(true);
                     }
                 } else if (!Enums.RETARGET_ACTIVE) {
                     outputToUserScreen(DriverStationLCD.Line.kUser3, "SHOOT!!!!");
                     if (mAutoShootEnabled) {
                         mShooter.Shoot(true);
                     }
                 }

             }
         }
     } else if (mAutoTargetSuccess) {
         if (Enums.RETARGET_ACTIVE && mNumIterations < 2) {
             //recheck the target
             System.out.println("Retargetting");
             try {
                 this.calcTarget();
             } catch (Exception ex) {
             }
         }
     }
 }

 private double calcTurretAngleToMove(double xPos) {
     double deltaX;
     deltaX = xPos - 320;  //320 is half of 640 px. camera image
     deltaX *= (Enums.PIXEL_TO_T_ROTATION);

     return deltaX ;
 }

 private double calcCameraAngleToMove(double xPos) {
     double deltaX;
     deltaX = xPos - 320;  //320 is half of 640 px. camera image
     deltaX *= (Enums.PIXEL_TO_C_ROTATION);
     return deltaX;
 }

 private double calcSpeed(double distance) {
     //This function is the result of empiracle mapping of distance to speed setpoint
//     return 0.65;
     // below 12 feet return a constant 0.522
     //y = 0.0048x2 - 0.111x + 1.1628
     double dblSpeed =0.0;

     if (distance <=12){
         dblSpeed = 0.522;
     } else
     {
         dblSpeed = 0.0048 *  distance * distance - 0.111* distance + 1.1628 + mSpeedOffset;
     }

     //return mShooter.getSpeed() + mSpeedOffset;
     return dblSpeed;
 }

 private double calcDistance(double boundRectWidth) {
     //returns in FEET
     double m = 2.575502199;
     double b = -0.270426076;
     System.out.println("width: " + boundRectWidth);
     return (640.0 * m / boundRectWidth) + b;
 }

 public void target(boolean calcAim, boolean calcDistance,boolean autoShoot) throws AxisCameraException {
     if (!mAutoTargetStarted){
         mAutoTargetStarted = true;
         mNumIterations = 0;
         if (calcAim) {
             if (calcDistance) {
                 mAutoSpeed = true;
             } else {
                 mAutoSpeed = false;
             }
             if (autoShoot){
                 mAutoShootEnabled = autoShoot;
             }
             calcTarget();
         } else {
//             when this times out it will set the shooter wheel to the last setpoint
             mShooter.prepToShoot();
         }
     }

 }


 private double getScaledIO() {
     double val = 0.0;
     try {
         val = mdsIO.getAnalogIn(1);
     }catch (Exception ex)
     {}
     return (1.0 - 2 * (Enums.ENH_IO_ANALOG_MAX - val) / Enums.ENH_IO_ANALOG_MAX);
 }

 public boolean getAutoTargetSuccess(){
     return mAutoTargetSuccess;
 }

 private void calcTarget() throws AxisCameraException {

     mAutoTargetSuccess = false;
     if (Enums.CAMERA_ACTIVE) {
         try {
             /**
              * Do the image capture with the camera and apply the algorithm described above. This
              * sample will either get images from the camera or from an image file stored in the top
              * level directory in the flash memory on the cRIO. The file name in this case is "10ft2.png"
              *
              */
             outputToUserScreen(DriverStationLCD.Line.kUser3, "          ");
             mShootingEngaged = false;
             BinaryImage thresholdImage;
             Timer autoTimer = new Timer();
             autoTimer.start();
             boolean checked = false;
             ColorImage image = mCamera.getImage();     // comment if using stored images
             Watchdog.getInstance().feed();
             if (mDebugImages || mImageCaptureCt == 0) {
                 System.out.println("Writing colorImage");
                 image.write("/vision/colorImage.png");
             }

             if (Enums.LED_COLOR == 1) {
                 thresholdImage = image.thresholdRGB(Enums.THRESH_UV_RL, Enums.THRESH_UV_RH, Enums.THRESH_UV_GL, Enums.THRESH_UV_GH, Enums.THRESH_UV_BL, Enums.THRESH_UV_BH);   // keep only red objects
             } else if (Enums.LED_COLOR == 0) {
                 System.out.println("Using Green RGB threshold");
                 thresholdImage = image.thresholdRGB(Enums.THRESH_GR_RL, Enums.THRESH_GR_RH, Enums.THRESH_GR_GL, Enums.THRESH_GR_GH, Enums.THRESH_GR_BL, Enums.THRESH_GR_BH);   // keep only red objects
             }

             Watchdog.getInstance().feed();
             if (mDebugImages || mImageCaptureCt == 0) {
                 System.out.println("writing thresholdImage");
                 thresholdImage.write("/vision/thresholdImage.png");
             }

             Watchdog.getInstance().feed();
             BinaryImage convexHullImage = thresholdImage.convexHull(Enums.IMAGE8_CONNECTIVITY);          // fill in occluded rectangles
             if (mDebugImages || mImageCaptureCt == 0) {
                 System.out.println("Writing convexHullImage");
                 convexHullImage.write("/vision/convexHullImage.png");
             }

             Watchdog.getInstance().feed();
             BinaryImage filteredImage = convexHullImage.particleFilter(mcc);           // find filled in rectangles
             if (mDebugImages || mImageCaptureCt == 0) {
                 System.out.println("Writing filteredImage");
                 filteredImage.write("/vision/filteredImage.png");
             }

             Watchdog.getInstance().feed();
             BinaryImage bigObjectsImage = filteredImage.removeSmallObjects(Enums.IMAGE8_CONNECTIVITY, Enums.REM_SMALL_OBJ_ITERATIONS);  // remove small artifacts
             if (mDebugImages || mImageCaptureCt == 0) {
                 System.out.println("Writing bigObjectsImage");
                 bigObjectsImage.write("/vision/bigObjectsImage.png");
                 mImageCaptureCt = 10;
             }
             System.out.println("starting ParticleAnalysis");

             ParticleAnalysisReport[] reports = bigObjectsImage.getOrderedParticleAnalysisReports();  // get list of results
             double changeAngle = 0.0;
             mNumParticlesFound = reports.length;

             if (mNumParticlesFound > 0){
                 //NOTE unlike the vision system, these particles are ordered in a RANDOM fashion
                 int iMin = reports.length;
                 int yMin = 1000;
                 for (int i = reports.length - 1; i >= 0; i--) {                                // print results
                     ParticleAnalysisReport r = reports[i];
                     if (r.boundingRectWidth / r.boundingRectHeight <= 2.0) {
                         if (r.center_mass_y < yMin) {
                             iMin = i;
                             yMin = r.center_mass_y;
                         }
                     }
                     System.out.println("Particle: " + i + ": Center of mass Y: " + r.center_mass_y + "  Center of mass x: " + r.center_mass_x + "  Bounding Rect Width: " + r.boundingRectWidth + "  Bounding Rect Height: " + r.boundingRectHeight);
                 }

                 ParticleAnalysisReport r = reports[iMin];

                 //we need this to be a rectangle with x wider than y
                 if (true) {
                     System.out.println("Processing " + iMin);

                     //crank the speed;
                     mTurretSpeed = 1.0;
                     mShootingEngaged = true;
                     setTurretSP(calcTurretAngleToMove(r.center_mass_x));
                     mDistance = calcDistance(r.boundingRectWidth);
                     mCalcSpeed = calcSpeed(mDistance);
                     mLastAutoTargetTime = autoTimer.get();
                     mAutoTargetSuccess = true;
                     mNumIterations++;
                     //just do the first one

                     if (mAutoSpeed) {
                         mShooter.setSpeedandEngage(mCalcSpeed);
                     } else {
                         mShooter.engageShooter();
                     }

                 }
                 /**
                  * all images in Java must be freed after they are used since they are allocated out
                  * of C data structures. Not calling free() will cause the memory to accumulate over
                  * each pass of this loop.
                  */

             } else {
                 mAutoTargetStarted = false;
                 mAutoTargetSuccess = false;
             }

             filteredImage.free();
             convexHullImage.free();
             bigObjectsImage.free();
             thresholdImage.free();
             image.free();

//         } catch (AxisCameraException ex) {        // this is needed if the camera.getImage() is called
//             ex.printStackTrace();
         } catch (NIVisionException ex) {
             ex.printStackTrace();
         }

     }

 }

 public void updateStatus() {
     mShooter.updateStatus();

     double vltg;
     vltg = (double) ((int) (mTurretVoltage.getVoltage() * 100) / 100.0);

     SmartDashboard.putDouble("TurretPos", vltg);
     SmartDashboard.putBoolean("LS Right", mTurretLSRight.get());
     SmartDashboard.putBoolean("LS Left", mTurretLSLeft.get());
     SmartDashboard.putDouble("AutoTargetTime", mLastAutoTargetTime);
     SmartDashboard.putInt("NumParticles", mNumParticlesFound);
     SmartDashboard.putBoolean("AutoTargetSuccess", mAutoTargetSuccess);
     SmartDashboard.putDouble("Turret Offset",getScaledIO());
     SmartDashboard.putDouble("Speed Offset", mSpeedOffset);
     SmartDashboard.putDouble("Distance: ", mDistance);
     SmartDashboard.putDouble("CalcSpeed: ",mCalcSpeed);
     SmartDashboard.putDouble("Turret SP: ",mTurretVoltageSP);
     SmartDashboard.putBoolean("AutoTurretEngaged: ", mAutoTargetEngaged);
     if (cameraSwivelTilt != null){
         SmartDashboard.putDouble("Camera Tilt",cameraSwivelTilt.get());
     }
     SmartDashboard.putBoolean("Auto Shoot", mAutoShootEnabled);

 }

 private void outputToUserScreen(String strMsg) {
     outputToUserScreen(DriverStationLCD.Line.kUser5, strMsg);
 }

 private void outputToUserScreen(DriverStationLCD.Line line, String strMsg) {
     try {
         System.out.println(strMsg);

         DriverStationLCD driverScreen = DriverStationLCD.getInstance();
         driverScreen.println(line, 1, strMsg.substring(0, Math.min(strMsg.length(), DriverStationLCD.kLineLength)));
         driverScreen.updateLCD();

     } catch (Exception ex) {
         //do nothing
     }

 }
}
