package org.usfirst.frc.xcats.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Navx {
	private AHRS ahrs;
	private RobotControls _controls;
	public boolean displayVariables = false;
	public String navxMode = "";
	public double navxRotateDistance;
	private double _degrees = 0;
	private double _startSpeed = 0.25;
	private double  _speed =0.25;
	private double _tolerance=1;
	private float _startingYaw=0;
	public Navx (RobotControls controls)
	{
		_controls = controls;
		try {
			/***********************************************************************
			 * navX-MXP:
			 * - Communication via RoboRIO MXP (SPI, I2C, TTL UART) and USB.            
			 * - See http://navx-mxp.kauailabs.com/guidance/selecting-an-interface.
			 * 
			 * navX-Micro:
			 * - Communication via I2C (RoboRIO MXP or Onboard) and USB.
			 * - See http://navx-micro.kauailabs.com/guidance/selecting-an-interface.
			 * 
			 * Multiple navX-model devices on a single robot are supported.
			 ************************************************************************/
			ahrs = new AHRS(SPI.Port.kMXP);
		} catch (RuntimeException ex ) {
			DriverStation.reportError("Error instantiating navX MXP:  " + ex.getMessage(), true);
		}
	}
	public void updateStatus(){
		if(displayVariables){
			/* Display 6-axis Processed Angle Data                                      */
			SmartDashboard.putBoolean(  "IMU_Connected",        ahrs.isConnected());
			SmartDashboard.putBoolean(  "IMU_IsCalibrating",    ahrs.isCalibrating());
			SmartDashboard.putNumber(   "IMU_Yaw",              ahrs.getYaw());
			SmartDashboard.putNumber(   "IMU_Pitch",            ahrs.getPitch());
			SmartDashboard.putNumber(   "IMU_Roll",             ahrs.getRoll());


			/* Display tilt-corrected, Magnetometer-based heading (requires             */
			/* magnetometer calibration to be useful)                                   */

			SmartDashboard.putNumber(   "IMU_CompassHeading",   ahrs.getCompassHeading());

			/* Display 9-axis Heading (requires magnetometer calibration to be useful)  */
			SmartDashboard.putNumber(   "IMU_FusedHeading",     ahrs.getFusedHeading());

			/* These functions are compatible w/the WPI Gyro Class, providing a simple  */
			/* path for upgrading from the Kit-of-Parts gyro to the navx MXP            */

			SmartDashboard.putNumber(   "IMU_TotalYaw",         ahrs.getAngle());
			SmartDashboard.putNumber(   "IMU_YawRateDPS",       ahrs.getRate());

			/* Display Processed Acceleration Data (Linear Acceleration, Motion Detect) */

			SmartDashboard.putNumber(   "IMU_Accel_X",          ahrs.getWorldLinearAccelX());
			SmartDashboard.putNumber(   "IMU_Accel_Y",          ahrs.getWorldLinearAccelY());
			SmartDashboard.putBoolean(  "IMU_IsMoving",         ahrs.isMoving());
			SmartDashboard.putBoolean(  "IMU_IsRotating",       ahrs.isRotating());

			/* Display estimates of velocity/displacement.  Note that these values are  */
			/* not expected to be accurate enough for estimating robot position on a    */
			/* FIRST FRC Robotics Field, due to accelerometer noise and the compounding */
			/* of these errors due to single (velocity) integration and especially      */
			/* double (displacement) integration.                                       */

			SmartDashboard.putNumber(   "Velocity_X",           ahrs.getVelocityX());
			SmartDashboard.putNumber(   "Velocity_Y",           ahrs.getVelocityY());
			SmartDashboard.putNumber(   "Displacement_X",       ahrs.getDisplacementX());
			SmartDashboard.putNumber(   "Displacement_Y",       ahrs.getDisplacementY());

			/* Display Raw Gyro/Accelerometer/Magnetometer Values                       */
			/* NOTE:  These values are not normally necessary, but are made available   */
			/* for advanced users.  Before using this data, please consider whether     */
			/* the processed data (see above) will suit your needs.                     */

			SmartDashboard.putNumber(   "RawGyro_X",            ahrs.getRawGyroX());
			SmartDashboard.putNumber(   "RawGyro_Y",            ahrs.getRawGyroY());
			SmartDashboard.putNumber(   "RawGyro_Z",            ahrs.getRawGyroZ());
			SmartDashboard.putNumber(   "RawAccel_X",           ahrs.getRawAccelX());
			SmartDashboard.putNumber(   "RawAccel_Y",           ahrs.getRawAccelY());
			SmartDashboard.putNumber(   "RawAccel_Z",           ahrs.getRawAccelZ());
			SmartDashboard.putNumber(   "RawMag_X",             ahrs.getRawMagX());
			SmartDashboard.putNumber(   "RawMag_Y",             ahrs.getRawMagY());
			SmartDashboard.putNumber(   "RawMag_Z",             ahrs.getRawMagZ());
			SmartDashboard.putNumber(   "IMU_Temp_C",           ahrs.getTempC());
			SmartDashboard.putNumber(   "IMU_Timestamp",        ahrs.getLastSensorTimestamp());

			/* Omnimount Yaw Axis Information                                           */
			/* For more info, see http://navx-mxp.kauailabs.com/installation/omnimount  */
			AHRS.BoardYawAxis yaw_axis = ahrs.getBoardYawAxis();
			SmartDashboard.putString(   "YawAxisDirection",     yaw_axis.up ? "Up" : "Down" );
			SmartDashboard.putNumber(   "YawAxis",              yaw_axis.board_axis.getValue() );

			/* Sensor Board Information                                                 */
			SmartDashboard.putString(   "FirmwareVersion",      ahrs.getFirmwareVersion());

			/* Quaternion Data                                                          */
			/* Quaternions are fascinating, and are the most compact representation of  */
			/* orientation data.  All of the Yaw, Pitch and Roll Values can be derived  */
			/* from the Quaternions.  If interested in motion processing, knowledge of  */
			/* Quaternions is highly recommended.                                       */
			SmartDashboard.putNumber(   "QuaternionW",          ahrs.getQuaternionW());
			SmartDashboard.putNumber(   "QuaternionX",          ahrs.getQuaternionX());
			SmartDashboard.putNumber(   "QuaternionY",          ahrs.getQuaternionY());
			SmartDashboard.putNumber(   "QuaternionZ",          ahrs.getQuaternionZ());

			/* Connectivity Debugging Support                                           */
			SmartDashboard.putNumber(   "IMU_Byte_Count",       ahrs.getByteCount());
			SmartDashboard.putNumber(   "IMU_Update_Count",     ahrs.getUpdateCount());
		}



		switch(navxMode){
		case "rotate": rotateContinuous();
		break;
		default:

			break;
		}
//		SmartDashboard.putString("navxMode", navxMode);

	}
	public void resetStatus(){
		ahrs.resetDisplacement();
	}
	public float getYaw(){
//		return ahrs.getYaw() - _startingYaw;
		return ahrs.getYaw();
	}
	public void zeroYaw(){
		ahrs.reset();
		ahrs.zeroYaw();
//		_startingYaw = ahrs.getYaw();
	}
	public float getCompassHeading(){
		return ahrs.getCompassHeading();
	}
	public void rotate(double degrees){
		
		if(!navxMode.equals("")){
			return;			
		}
		
		
		navxMode = "rotate";
		double direction=1;
		_degrees = degrees;
		zeroYaw();

		//deltaYaw = _initialYaw + _controls.getNavx().getYaw();
		//SmartDashboard.putNumber("deltaYaw", deltaYaw);
		// 
		direction = (degrees > 0 ? -1 : 1);
		_speed = direction * Math.abs(_startSpeed);	
		_controls.getDrive().set(_speed, _speed, - _speed, -_speed);

		
	}
	private void rotateContinuous(){

		if(navxMode.equals("rotate")){

			if(Math.abs(getYaw()) > Math.abs(_degrees)){
				_speed=-_speed/1.5;
				_controls.getDrive().set(_speed, _speed, -_speed, -_speed);
				if(Math.abs(getYaw())-Math.abs(_degrees)<_tolerance){
					navxMode = "";
				}
			}
		}	
		
	}
	
	public boolean isOperating(){
		if(navxMode.equals("")){
			return false;
		}else{
			return true;
		}
	}
}

