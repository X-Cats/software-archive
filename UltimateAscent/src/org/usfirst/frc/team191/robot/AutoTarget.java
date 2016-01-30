/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

/**
 *
 * @author mary farmer
 */
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStationEnhancedIO;
import edu.wpi.first.wpilibj.DriverStationLCD;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Watchdog;
import edu.wpi.first.wpilibj.camera.AxisCamera;
import edu.wpi.first.wpilibj.camera.AxisCameraException;
import edu.wpi.first.wpilibj.image.BinaryImage;
import edu.wpi.first.wpilibj.image.ColorImage;
import edu.wpi.first.wpilibj.image.CriteriaCollection;
import edu.wpi.first.wpilibj.image.NIVision.MeasurementType;
import edu.wpi.first.wpilibj.image.NIVisionException;
import edu.wpi.first.wpilibj.image.ParticleAnalysisReport;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
 

/**
 *
 * @author xcats
 */
public class AutoTarget {

    private AxisCamera mCamera;
    private CriteriaCollection mcc;      // the criteria for doing the particle filter operation
    private boolean mAutoTargetEngaged = false;
    private boolean mDebugImages = Enums.DEBUG_CAMERA_IMAGES;  //false for competition
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
    
    private boolean mSpeedIsGood = false;
    private boolean mAngleIsGood = false;

    public void initCamera() {

    }

    public AutoTarget() {

        //This is the enhanced IO on the driverstation
        mdsIO = DriverStation.getInstance().getEnhancedIO();

        try {

            //Shooter
           // mShooter = new Shooter();

            if (Enums.CAMERA_ACTIVE) {
                //DO NOT FORGET: when using the servo, connecting the PWM requires the jumper to be present on the two pins next to the 3 PWM pins for the power to be properly set
//                Timer.delay(2.0);
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

    }


    public void setSpeedOffset(double offset) {
        mSpeedOffset = offset;
    }

    public void setAutoShoot(boolean autoShoot) {
        mAutoShootEnabled = autoShoot;
    }

    public void setAutoSpeed(boolean autocalcSpeed) {
        mAutoSpeed = autocalcSpeed;
    }
    
    public void disable() {
        //return to init positions
        mImageCaptureCt = 10;

        if (mShooter != null) {
            mShooter.disable();
        }
        mAutoTargetEngaged = false;
    }

    public void calcSpeedOffset() {
        //use the current encoder average to calculate a speed offset
    }

    private double calcTurretAngleToMove(double xPos) {
        double deltaX;
        deltaX = xPos - 320;  //320 is half of 640 px. camera image
        deltaX *= (Enums.PIXEL_TO_T_ROTATION);

        return deltaX ;
    }

    private double calcSpeed(double distance) {
        //This function is the result of empiracle mapping of distance to speed setpoint
//        return 0.65;
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
//        if (!mAutoTargetStarted){
//            mAutoTargetStarted = true;
//            mNumIterations = 0;
//            if (calcAim) {
//                if (calcDistance) {
//                    mAutoSpeed = true;
//                } else {
//                    mAutoSpeed = false;
//                }
//                if (autoShoot){
//                    mAutoShootEnabled = autoShoot;
//                }
//                calcTarget();
//            } else {
////                when this times out it will set the shooter wheel to the last setpoint
//                mShooter.prepToShoot();
//            }
//        }

        calcTarget();
    }

    public boolean processTargetLocation(ParticleAnalysisReport r){
        SmartDashboard.putNumber("CenterX", r.center_mass_x);
        SmartDashboard.putNumber("CenterY", r.center_mass_y);
        SmartDashboard.putNumber("Height", r.boundingRectHeight);
        SmartDashboard.putNumber("Width", r.boundingRectWidth);
        
        if (r.center_mass_x > 260 && r.center_mass_x < 380 && r.center_mass_y < 260 && r.center_mass_y > 160)
            return true;
        else
            return false;
        
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
                        if (r.center_mass_x > 260 && r.center_mass_x < 380) {
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

                        mShootingEngaged = true;
//                        setTurretSP(calcTurretAngleToMove(r.center_mass_x));
            //            mDistance = calcDistance(r.boundingRectWidth);
            //            mCalcSpeed = calcSpeed(mDistance);
                        SmartDashboard.putBoolean("targetLocation", processTargetLocation(r));
                        mLastAutoTargetTime = autoTimer.get();
                        mAutoTargetSuccess = true;
                        mNumIterations++;
                        //just do the first one

//                        if (mAutoSpeed) {
//                            mShooter.setSpeedandEngage(mCalcSpeed);
//                        } else {
//                            mShooter.engageShooter();
//                        }

                    }
                    else
                        System.out.println("no targets found");

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

//            } catch (AxisCameraException ex) {        // this is needed if the camera.getImage() is called
//                ex.printStackTrace();
            } catch (NIVisionException ex) {
                ex.printStackTrace();
            }

        }

    }

    public void updateStatus() {
//        mShooter.updateStatus();
//
//        double vltg;
//        vltg = (double) ((int) (mTurretVoltage.getVoltage() * 100) / 100.0);
//
//        SmartDashboard.putDouble("TurretPos", vltg);
//        SmartDashboard.putBoolean("LS Right", mTurretLSRight.get());
//        SmartDashboard.putBoolean("LS Left", mTurretLSLeft.get());
//        SmartDashboard.putDouble("AutoTargetTime", mLastAutoTargetTime);
//        SmartDashboard.putInt("NumParticles", mNumParticlesFound);
//        SmartDashboard.putBoolean("AutoTargetSuccess", mAutoTargetSuccess);
//        SmartDashboard.putDouble("Turret Offset",getScaledIO());
//        SmartDashboard.putDouble("Speed Offset", mSpeedOffset);
//        SmartDashboard.putDouble("Distance: ", mDistance);
//        SmartDashboard.putDouble("CalcSpeed: ",mCalcSpeed);
//        SmartDashboard.putDouble("Turret SP: ",mTurretVoltageSP);
//        SmartDashboard.putBoolean("AutoTurretEngaged: ", mAutoTargetEngaged);
//        if (cameraSwivelTilt != null){
//            SmartDashboard.putDouble("Camera Tilt",cameraSwivelTilt.get());
//        }
//        SmartDashboard.putBoolean("Auto Shoot", mAutoShootEnabled);

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
