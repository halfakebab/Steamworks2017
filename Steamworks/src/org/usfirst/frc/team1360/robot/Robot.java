
package org.usfirst.frc.team1360.robot;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.usfirst.frc.team1360.auto.AutonControl;
import org.usfirst.frc.team1360.auto.tracking.PositionTracker;
import org.usfirst.frc.team1360.robot.IO.HumanInput;
import org.usfirst.frc.team1360.robot.IO.RobotOutput;
import org.usfirst.frc.team1360.robot.IO.SensorInput;
import org.usfirst.frc.team1360.robot.teleop.TeleopControl;
import org.usfirst.frc.team1360.robot.util.OrbitCamera;
import org.usfirst.frc.team1360.server.Connection;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {	
	private static Robot instance;
	
	private RobotOutput robotOutput;
	private HumanInput humanInput;
	private SensorInput sensorInput;
	private TeleopControl teleopControl;
	private AutonControl autonControl;
	private OrbitCamera camera;
	private Connection connection;
	private PositionTracker pt;
	
	public Robot()
	{
		instance = this;
	}
	
    public void robotInit()
    {
    	System.out.println("Nick Mertin - GUI Test Code");
		this.connection = new Connection(5801);
    	this.robotOutput = RobotOutput.getInstance();
    	this.humanInput = HumanInput.getInstance();
    	this.teleopControl = TeleopControl.getInstance();
    	this.sensorInput = SensorInput.getInstance();
    	this.autonControl = AutonControl.getInstance();
    	this.sensorInput.reset();
    	
    	camera = new OrbitCamera("10.13.60.3", "Axis Camera");
    	pt = PositionTracker.getInstance();
    }
    
    public static Robot getInstance()
    {
    	return instance;
    }
    
    public Connection getConnection()
    {
    	return connection;
    }

    public void autonomousInit() 
    {
    	this.autonControl.initialize();
    	this.sensorInput.reset();
    }

    public void disabledInit()
    {
    	this.robotOutput.stopAll();
    	this.teleopControl.disable();
    	this.sensorInput.calculate();
    	try {
        	pt.stop();
			pt.stopLogging();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void disabledPeriodic()
    {
    	this.sensorInput.calculate();
    	this.autonControl.updateModes();
    }

    public void autonomousPeriodic()
    {
    	sensorInput.calculate();
    	autonControl.runCycle();
    	this.sensorInput.calculate();
		SmartDashboard.putNumber("NavX Yaw", this.sensorInput.getAHRSYaw());
    }
    
    public void teleopInit()
    {
    	pt.start();
    	pt.startLogging(new File("/tmp/pt.csv"));
    }

    public void teleopPeriodic()
    {
        this.sensorInput.calculate();
        this.teleopControl.runCycle();
    }
 
}
