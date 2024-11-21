// DriveCommand.java
package frc.robot.commands;

// Import necessary classes
import frc.robot.subsystems.DriveSubsystem;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandPS4Controller;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;

public class DriveCommand extends Command {
    // Subsystems and controller for controlling the robot
    private final DriveSubsystem driveSubsystem; // The robot's drivetrain
    private final CommandPS4Controller controller; // Interface for controller inputs
    private final DriveMode driveMode; // Selected drive mode

    // Enum representing the available driving modes
    public enum DriveMode {
        ARCADE, TANK, ARCADE_RIGHT, ARCADE_SPLIT
    }

    // Constructor to set up the command with required subsystems and drive mode
    public DriveCommand(DriveMode driveMode, DriveSubsystem driveSubsystem, CommandPS4Controller controller) {
        this.driveSubsystem = driveSubsystem;
        this.controller = controller;
        this.driveMode = driveMode;

        // Declare subsystem dependencies to prevent conflicts
        addRequirements(driveSubsystem);
    }

    @Override
    public void execute() {
        // Retrieve joystick inputs from the controller
        double leftSpeed = controller.getLeftY(); // Forward/backward input from the left stick
        double leftRotation = controller.getLeftX(); // Turning input from the left stick
        double rightSpeed = controller.getRightY(); // Forward/backward input from the right stick
        double rightRotation = controller.getRightX(); // Turning input from the right stick

        // Execute the appropriate driving logic based on the selected drive mode
        switch (driveMode) {
            case ARCADE:
                // Arcade drive uses one stick for both speed and turning
                driveSubsystem.arcadeDrive(leftSpeed, leftRotation);
                break;

            case TANK:
                // Tank drive uses separate sticks for left and right speeds
                driveSubsystem.tankDrive(leftSpeed, rightSpeed);
                break;

            case ARCADE_RIGHT:
                // Arcade drive using right stick
                driveSubsystem.arcadeDrive(rightSpeed, rightRotation);
                break;

            case ARCADE_SPLIT:
                // Arcade with left stick speed and right for rotation
                driveSubsystem.arcadeDrive(leftSpeed, rightRotation);

        }
    }

    @Override
    public boolean isFinished() {
        // The command never finishes on its own
        return false;
    }
}
