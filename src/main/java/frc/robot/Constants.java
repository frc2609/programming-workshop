// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;
/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {
  public static class ControllerConstants {
    public static final int DriverPort = 0;
  }
  public static class DriveSubsystemConstants {
    public static final int canLeftMotor = 15;
    public static final int canRightMotor = 2;
    public static final double defaultSpeedLimit = 0.35;


    public static final double trackWidth = 0.325; // Meters

    // Drivetrain physical characteristics
    public static final double wheelDiameterMeters = 0.1016; // 4-inch wheels converted to meters
    public static final double gearReduction = 72.0 / 14.0; // Gear reduction ratio (5.14)
    public static final double encoderTicksPerRevolution = 42; // Encoder ticks per motor revolution (NEO motors)

    // Calculated distance per encoder tick
    public static final double distancePerEncoderTick = 
      (Math.PI * wheelDiameterMeters) / (encoderTicksPerRevolution * gearReduction);

    // Maximum drivetrain velocities
    public static final double maxVelocityMetersPerSecond = 6.0; // Max drivetrain velocity in meters/second
    public static final double maxAccelerationMetersPerSecondSquared = 4.0; // Max drivetrain acceleration
  }
}
