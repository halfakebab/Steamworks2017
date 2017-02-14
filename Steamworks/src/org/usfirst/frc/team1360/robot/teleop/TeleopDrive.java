package org.usfirst.frc.team1360.robot.teleop;

import org.usfirst.frc.team1360.robot.IO.HumanInput;
import org.usfirst.frc.team1360.robot.IO.RobotOutput;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TeleopDrive implements TeleopComponent {
	
	private static TeleopDrive instance;
	private HumanInput humanInput;
	private RobotOutput robotOutput;
	private boolean isShifted = false;
	
	private TeleopDrive()
	{
		humanInput = HumanInput.getInstance();
		robotOutput = RobotOutput.getInstance();
	}

	public static TeleopDrive getInstance()
	{
		if (instance == null)
			instance = new TeleopDrive();
		
		return instance;
	}
	
	public void calculate() {
		double speed = humanInput.getDriveRight() - humanInput.getDriveLeft();
		double turn = humanInput.getTurn();
		boolean shift = humanInput.getShiftSpeed();
		
		if(shift && !isShifted)
		{
			isShifted = true;
			robotOutput.shiftSpeed(true);
		}
		else if (shift && isShifted)
		{
			isShifted = false;
			robotOutput.shiftSpeed(false);
		}
		
		if(Math.abs(turn) < 0.2) turn = 0;
		
		robotOutput.arcadeDrive(speed, turn);
	}

	public void disable() {
		robotOutput.tankDrive(0, 0);
		
	}

}
