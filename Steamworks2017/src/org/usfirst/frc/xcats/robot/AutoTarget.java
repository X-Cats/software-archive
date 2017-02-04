package org.usfirst.frc.xcats.robot;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class AutoTarget {
	private UsbCamera _camera; 
	private CvSink _cvs;
	private CvSource _outputStream;
	private Mat _mat;
	
	
	
	public AutoTarget(){
		
		try{
			_camera = CameraServer.getInstance().startAutomaticCapture();
			// Set the resolution
			_camera.setResolution(640, 480);

			// Get a CvSink. This will capture Mats from the camera
			_cvs = CameraServer.getInstance().getVideo();
			
			// Setup a CvSource. This will send images back to the Dashboard
			_outputStream = CameraServer.getInstance().putVideo("Rectangle", 640, 480);

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
		Imgproc.rectangle(_mat, new Point(100, 100), new Point(400, 400),
				new Scalar(255, 255, 255), 5);
		// Give the output stream a new image to display
		_outputStream.putFrame(_mat);				
	}
	
//	public void captureImage(String filename){
//		
//		if (_camera == null)
//			return;
//		
//		
//	}
	
	public void updateStatus(){
		//update status here
	}
	

}
