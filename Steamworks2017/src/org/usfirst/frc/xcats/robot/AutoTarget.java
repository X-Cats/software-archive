package org.usfirst.frc.xcats.robot;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgcodecs.Imgcodecs;



/*
 * This class is designed to work with the webcam and display a targeting grid
 * 
 *     0,0                                                             640, 0
 *      +----------------------------P0-----------------------------------+
 *      |      P4_0     P6_0         .         P7_0               P5_0    |
 *      |                            .                                    |
 *      |                            .                                    |
 *      |                            .                                    |
 *      |                            .                                    |
 *      |                            .          P7                        |
 *      |                P6  .    "  P2  "     .                          |
 *      |      P4 .     "                              "     .    P5      |
 *      | .   "                                                    "    . |
 *      |P1                                                             P3|
 *      |                                                                 |
 *      +-----------------------------------------------------------------+
 *     0, 480                                                          640, 480
 *
 *     
 *     We need 3 lines on the display
 *     >  P0 is the center point of the gear target and where the rope will hang
 *     >  P2 is the bottom of the gear target as it recedes into the distance
 *     >  P1 is the left side bottom of the inside of the tape when the gear is on the target
 *     >  P3 is the right side bottom of the inside of the tape when the gear is on the target
 *     
 *     2 more when we are in position to eject
 *     > P4 is left inside when we are in range to eject
 *     > P5 is right inside when we are in range to eject
 *     
 *     if we draw these lines on the screen, they can be used to guide the robot into position
 *     in particular, there is no good visual reference on the field for the P0-P2 line, 
 *     it is halfway between the tapes as represented by the verticals at P1 and P3
 *     
 */

public class AutoTarget {
	private UsbCamera _camera; 
	private CvSink _cvs;
	private CvSource _outputStream;
	private Mat _mat;
	private Point _P0;
	private Point _P1;
	private Point _P2;
	private Point _P3;
	private Point _P4, _P4_0;
	private Point _P5, _P5_0;
	private Point _P6, _P6_0;
	private Point _P7, _P7_0;
	private Scalar _lineColor = new Scalar(0,255,255);
	private Scalar _ejectColor = new Scalar(0,0,255);
	
	public AutoTarget(UsbCamera camera){
		
		try{
			_camera = CameraServer.getInstance().startAutomaticCapture();
			//_camera = camera;
			// Set the resolution
			_camera.setResolution(640, 480);
			//_camera.setFPS(5);

			// Get a CvSink. This will capture Mats from the camera
			_cvs = CameraServer.getInstance().getVideo();
			
			// Setup a CvSource. This will send images back to the Dashboard
			_outputStream = CameraServer.getInstance().putVideo("XCATS", 640, 480);

			// Mats are very memory expensive. Lets reuse this Mat.
			_mat = new Mat();
			
			int inRangeDeltaX;
			int outRangeDeltaX;
			int P0x;
			int P1y;
			int P2y;
			int P4y;
			if (Enums.IS_FINAL_ROBOT){
				
			} else{
				inRangeDeltaX = 150;
				outRangeDeltaX = 157;
				P0x = 320;
				P1y = 420;
				P2y = 280;
				P4y = 320;
			}

			_P0 	= new Point(P0x,0);
			_P1 	= new Point(0,P1y);
			_P2 	= new Point(P0x,P2y);
			_P3 	= new Point(640,P1y);
			
			_P4 	= new Point(P0x-inRangeDeltaX,P4y);
			_P4_0 = new Point(P0x-inRangeDeltaX,0);
			_P5 	= new Point(P0x+inRangeDeltaX,P4y);
			_P5_0 = new Point(P0x+inRangeDeltaX,0);		
			
			_P6 	= new Point(P0x-outRangeDeltaX,P4y);			
			_P6_0 = new Point(P0x-outRangeDeltaX,0);
			_P7 	= new Point(P0x+outRangeDeltaX,P4y);
			_P7_0 = new Point(P0x+outRangeDeltaX,0);		

			
//			SmartDashboard.putNumber("P1x", 100);
//			SmartDashboard.putNumber("P1y", 100);
//			SmartDashboard.putNumber("P2x", 400);
//			SmartDashboard.putNumber("P2y", 500);
			
		}
		catch (Exception e)
		{
			System.out.println(e);
			e.printStackTrace();
		}

	}
	
	public AutoTarget(UsbCamera camera,boolean noDashboard){
		
		try{
			//pass the shared camera in.
			//_camera = camera;
			_camera = CameraServer.getInstance().startAutomaticCapture();
			// Set the resolution
			_camera.setResolution(640, 480);
			//_camera.setFPS(5);

			// Get a CvSink. This will capture Mats from the camera
			_cvs = CameraServer.getInstance().getVideo();
			
			// Mats are very memory expensive. Lets reuse this Mat.
			_mat = new Mat();
	
		}
		catch (Exception e)
		{
			System.out.println(e);
			e.printStackTrace();
		}

		
		
	}

	public void processImage(){
		// Tell the CvSink to grab a frame from the camera and put it
		// in the source mat.  If there is an error notify the output.
		if (_cvs.grabFrame(_mat) == 0) {
			// Send the output the error.
			_outputStream.notifyError(_cvs.getError());
			// skip the rest of the current iteration
			return;
	
		}
		// Put a rectangle on the image
//		double P1x,P1y,P2x,P2y;
//		P1x = SmartDashboard.getNumber("P1x", 50);
//		P1y = SmartDashboard.getNumber("P1y", 50);
//		P2x = SmartDashboard.getNumber("P2x", 450);
//		P2y = SmartDashboard.getNumber("P2y", 550);
		
		//Draw the center line, this is where we want the gear and the rope
		Imgproc.line(_mat, _P0, _P2, _lineColor, 5 , 8, 0);

		//Draw the P1 to P2 segment
		Imgproc.line(_mat, _P1, _P2, _lineColor, 3 , 8, 0);

		//Draw the P2 to P3 segment
		Imgproc.line(_mat, _P2, _P3, _lineColor, 3 , 8, 0);
		
		//Draw the P4 segment (to top of screen)
		Imgproc.line(_mat, _P4, _P4_0, _ejectColor, 3 , 8, 0);
		
		//Draw the P5 segment (to top of screen)
		Imgproc.line(_mat, _P5, _P5_0, _ejectColor, 3 , 8, 0);

//		//Draw the P4 segment (to top of screen)
//		Imgproc.line(_mat, _P6, _P6_0, _ejectColor, 3 , 8, 0);
//		
//		//Draw the P5 segment (to top of screen)
//		Imgproc.line(_mat, _P7, _P7_0, _ejectColor, 3 , 8, 0);
		
		
		
//		Imgproc.rectangle(_mat, new Point(120, 100), new Point(450, 250),
//				new Scalar(0, 0, 255), 5);
//		Imgproc.rectangle(_mat, new Point(P1x, P1y), new Point(P2x, P2y),
//				new Scalar(0, 255, 255), 5);
		
		// Give the output stream a new image to display
		_outputStream.putFrame(_mat);				
	}
	
	public void captureImage(){
		
		String filename="";
		
		if (_camera == null)
		{
			System.out.println("The camera server is null");
			return;
		}

		if (_cvs.grabFrame(_mat) == 0) {
			// Send the output the error.
			System.out.println("Cannot get output in AutoTarget.captureImage");
			// skip the rest of the current iteration
			return;	
		}
		if (filename.length() == 0){
			Date date = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			filename = "/home/lvuser/"+dateFormat.format(date)+".jpg";			
		}
		Imgcodecs.imwrite(filename, _mat);
		try {
			GearPlacementVision gpv = new GearPlacementVision();
			VisionData visionData = gpv.processImage(_mat);
		} catch (Exception e){
			System.out.println("Error in Autotarget.CaptureImage");
			e.printStackTrace();
		}
				
	}
	
	public void updateStatus(){
		//update status here
	}
}
