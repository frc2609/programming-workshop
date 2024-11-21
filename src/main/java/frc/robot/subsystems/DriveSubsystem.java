package frc.robot.subsystems;

// Import necessary classes for motor control and dashboard display
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.DriveSubsystemConstants;;

public class DriveSubsystem extends SubsystemBase {

    // Default speed limit for driving, used to scale motor outputs
    private final double defaultSpeedLimit = DriveSubsystemConstants.defaultSpeedLimit;

    // Motor controllers for the left and right sides of the drivetrain
    private final CANSparkMax leftMotor;
    private final CANSparkMax rightMotor;

    // DifferentialDrive handles arcade and tank drive logic
    private final DifferentialDrive differentialDrive;

    // Variable to store the current speed limit
    private double speedLimit;

    // Constructor to initialize the subsystem and hardware components
    public DriveSubsystem() {
        // Initialize the left and right motors
        leftMotor = new CANSparkMax(DriveSubsystemConstants.canLeftMotor, MotorType.kBrushless);
        rightMotor = new CANSparkMax(DriveSubsystemConstants.canRightMotor, MotorType.kBrushless);

        // Create a DifferentialDrive object to manage motor control
        differentialDrive = new DifferentialDrive(leftMotor, rightMotor);

        // Enable motor safety to stop motors if commands are delayed
        differentialDrive.setSafetyEnabled(true);

        // Set the initial speed limit for the motors
        speedLimit = defaultSpeedLimit;

        // Display the speed limit on the SmartDashboard for tuning
        SmartDashboard.putNumber("Speed Limit", speedLimit);
    }

    @Override
    public void periodic() {
        // Read the speed limit value from the SmartDashboard
        double newSpeedLimit = SmartDashboard.getNumber("Speed Limit", defaultSpeedLimit);

        // If the speed limit has changed, update the variable and print a message
        if (newSpeedLimit != speedLimit) {
            speedLimit = newSpeedLimit;
            System.out.println("Speed Limit updated to: " + speedLimit);
        }
    }

    // Implements arcade-style driving using speed (forward/backward) and rotation
    public void arcadeDrive(double speed, double rotation) {
        differentialDrive.arcadeDrive(speed * speedLimit, rotation * speedLimit);
    }

    // Implements tank-style driving using separate speeds for left and right motors
    public void tankDrive(double leftSpeed, double rightSpeed) {
        differentialDrive.tankDrive(leftSpeed * speedLimit, rightSpeed * speedLimit);
    }

    // Stops both motors by setting their output to zero
    public void stop() {
        leftMotor.stopMotor();
        rightMotor.stopMotor();
    }
}
