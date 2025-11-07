// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

// Import necessary classes and packages
import frc.robot.Constants.ControllerConstants;
import frc.robot.commands.MoveMotor;
import frc.robot.subsystems.Motor;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandPS4Controller;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;

/**
 * The RobotContainer class serves as the main configuration for the robot.
 * It declares subsystems, commands, and mappings between triggers and commands.
 * Command-based is a "declarative" paradigm where logic is structured into commands
 * and subsystems, leaving little logic in Robot.java's periodic methods.
 */
public class RobotContainer {
    // Declare the robot's subsystems
    private final Motor motorSubsystem = new Motor(); // Subsystem to control a motor

    // Declare commands
    private final MoveMotor motorCommand = new MoveMotor(motorSubsystem); // Command to move a motor

    // Controller for driver input
    private final CommandXboxController driverController =
        new CommandXboxController(ControllerConstants.DriverPort);

    /**
     * The constructor for the RobotContainer.
     * This initializes subsystems, commands, and controller mappings.
     */
    public RobotContainer() {
        // Configure trigger bindings between controller inputs and commands
        configureBindings();
        // Configure default commands for subsystems
        configureDefaultCommands();
    }

    /**
     * Define mappings between controller triggers and commands.
     * Triggers can be created directly using predicates or through named factories
     * like {@link CommandXboxController}.
     */
    private void configureBindings() {
        driverController.b().whileTrue(motorCommand);
    }

    /**
     * Configure default commands for subsystems. These commands run automatically
     * when no other commands are actively controlling the subsystem.
     */
    private void configureDefaultCommands() {
        // Set the DriveSubsystem's default command to use arcade driving
    }

    // Get the command to run during the autonomous period.
    public Command getAutonomousCommand() {
        // Return an example autonomous command
        return null;
    }
}
