package org.usfirst.frc.xcats.robot;


public class AutonomousStep {
	public enum stepTypes {DRIVE, SHOOT, GRAB, UNGRAB, LIFT, LOWER, LIFT_TO_LOW, WAIT, STOP, RELEASE, DRIVE_DISTANCE,ROTATE};
				
		public String name= "";
		public double stepTime=0;
		public double leftSpeed=0;
		public double rightSpeed=0;
		public stepTypes stepType;
		public double distance=0;

		public AutonomousStep(){
			//blank empty constructor
		}
		
		/* ROTATE command:
		 * distance = degrees to rotate (negative is counterclockwise, positive is clockwise)
		 * time, lefSpeed, rightSpeed, is ignored
		 * 
		 * DRIVE_DISTANCE command:
		 * distance in ft is used
		 * leftSpeed is used to set both drive side speed
		 * time, rightSpeed is ignored
		 * 
		 * DRIVE
		 * time = seconds to move
		 * leftSpeed = speed of left motors
		 * rightSpeed = speed of right motors
		 * distance is ignored
		 * 
		 * LIFT, LOWER
		 * time is the amount of time to wait for the operation to continue
		 * leftSpeed,rightSpeed,distance is ignored		
		*/
		
		
		
		public AutonomousStep(AutonomousStep.stepTypes stepType, String name,double time, double leftSpeed,double rightSpeed, double distance){
			this.name = name;
			this.stepTime = time;
			this.leftSpeed = leftSpeed;
			this.rightSpeed = rightSpeed;
			this.stepType = stepType; 
			this.distance = distance;
		}
}
