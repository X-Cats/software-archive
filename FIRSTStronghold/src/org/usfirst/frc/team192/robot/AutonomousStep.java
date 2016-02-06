package org.usfirst.frc.team192.robot;

public class AutonomousStep {
	public enum stepTypes {DRIVE, GRAB,	UNGRAB, LIFT, LOWER, WAIT, STOP};
				
		public String name= "";
		public double stepTime=0;
		public double leftSpeed=0;
		public double rightSpeed=0;
		public stepTypes stepType;

		
		public AutonomousStep(AutonomousStep.stepTypes stepType, String name,double time, double leftSpeed,double rightSpeed){
			this.name = name;
			this.stepTime = time;
			this.leftSpeed = leftSpeed;
			this.rightSpeed = rightSpeed;
			this.stepType = stepType; 
		}
}
