package frc.robot.subsystems;

// Import necessary classes for motor control, odometry, and dashboard display
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;


// import com.pathplanner.lib.util.SparkClosedLoopController;
// import com.pathplanner.lib.util.ReplanningConfig;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.config.RobotConfig;
import com.pathplanner.lib.controllers.PPHolonomicDriveController;
import com.pathplanner.lib.controllers.PPLTVController;
import com.pathplanner.lib.util.PathPlannerLogging;


import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.DifferentialDriveKinematics;
import edu.wpi.first.math.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.DriveSubsystemConstants;
import com.studica.frc.AHRS;

public class DriveSubsystem extends SubsystemBase {
    // Motor controllers for the drivetrain
    private final SparkMax leftMotor;
    private final SparkMax rightMotor;

    // Encoders for tracking wheel positions and velocities
    private final RelativeEncoder leftEncoder;
    private final RelativeEncoder rightEncoder;

    // DifferentialDrive for arcade and tank driving
    private final DifferentialDrive differentialDrive;

    // Kinematics and odometry for tracking position
    private final DifferentialDriveKinematics kinematics;
    private final DifferentialDriveOdometry odometry;

    // Variable for the robot's current pose
    private Pose2d currentPose;

    // Speed limit scaling factor
    private double speedLimit;

    private final AHRS gyro;

    // Default speed limit
    private final double defaultSpeedLimit = DriveSubsystemConstants.defaultSpeedLimit;
    

    private double leftDistance = 0.0;
    private double rightDistance = 0.0;

    // Encoder conversion factor for meters
    private static final double DISTANCE_PER_ENCODER_TICK = 
        (Math.PI * DriveSubsystemConstants.wheelDiameterMeters) /
        (DriveSubsystemConstants.encoderTicksPerRevolution * DriveSubsystemConstants.gearReduction);

    public DriveSubsystem() {
        gyro = new AHRS(AHRS.NavXComType.kMXP_SPI, 100);
        gyro.zeroYaw();

        // Initialize motor controllers
        leftMotor = new SparkMax(DriveSubsystemConstants.canLeftMotor, MotorType.kBrushless);
        rightMotor = new SparkMax(DriveSubsystemConstants.canRightMotor, MotorType.kBrushless);

        // Initialize encoders
        leftEncoder = leftMotor.getEncoder();
        rightEncoder = rightMotor.getEncoder();
        leftEncoder.setPosition(0);
        rightEncoder.setPosition(0);

        // Initialize DifferentialDrive
        differentialDrive = new DifferentialDrive(leftMotor, rightMotor);

        // Initialize kinematics and odometry
        kinematics = new DifferentialDriveKinematics(DriveSubsystemConstants.trackWidth);
        odometry = new DifferentialDriveOdometry(
            gyro.getRotation2d(), // Use gyro angle
            0, // Initial left encoder distance
            0  // Initial right encoder distance
        );

        // Set initial pose and speed limit
        currentPose = new Pose2d();
        speedLimit = defaultSpeedLimit;

        // Display speed limit on SmartDashboard
        SmartDashboard.putNumber("Speed Limit", speedLimit);

        // Configure PathPlanner integration
        try{
            RobotConfig config = RobotConfig.fromGUISettings();
            AutoBuilder.configure(
                this::getPose,                // Pose supplier
                this::resetPose,              // Pose reset consumer
                this::getRobotRelativeSpeeds, // ChassisSpeeds supplier
                (speeds, feedforwards) -> driveRobotRelative(speeds),     // ChassisSpeeds consumer
                new PPLTVController(0.02),       // Path replanning configuration
                config, // Red alliance path-flipping logic
                () -> {
                    var alliance = DriverStation.getAlliance();
                    if (alliance.isPresent()) {
                      return alliance.get() == DriverStation.Alliance.Red;
                    }
                    return false;
                },
                this
            );
        }catch(Exception e){
            DriverStation.reportError("Failed to load PathPlanner config and configure AutoBuilder", e.getStackTrace());
        }
        
    }

    /**
     * Configures PathPlanner for autonomous path-following.
     */
 

    /**
     * Determines whether paths should be flipped for the red alliance.
     */
    private boolean shouldFlipPathsForRedAlliance() {
        return DriverStation.getAlliance().orElse(DriverStation.Alliance.Blue) == DriverStation.Alliance.Red; // Check if red alliance
    }

    /**
     * Gets the robot's current pose.
     */
    public Pose2d getPose() {
        return currentPose;
    }

    /**
     * Resets the robot's pose to a specific pose.
     */
    public void resetPose(Pose2d pose) {
        gyro.zeroYaw(); // Reset gyro
        leftEncoder.setPosition(0);
        rightEncoder.setPosition(0);

        odometry.resetPosition(
            gyro.getRotation2d(), // Current gyro rotation
            0, // Reset left encoder
            0, // Reset right encoder
            pose
        );

        currentPose = pose;
    }

    /**
     * Returns the current robot-relative chassis speeds.
     */
    public ChassisSpeeds getRobotRelativeSpeeds() {
        return new ChassisSpeeds(
            leftEncoder.getVelocity() * DISTANCE_PER_ENCODER_TICK, // Left velocity in meters/second
            rightEncoder.getVelocity() * DISTANCE_PER_ENCODER_TICK, // Right velocity in meters/second
            Units.degreesToRadians(gyro.getRate()) // Angular velocity in radians/second
        );
    }

    /**
     * Drives the robot using chassis speeds.
     */
    public void driveRobotRelative(ChassisSpeeds speeds) {
        var wheelSpeeds = kinematics.toWheelSpeeds(speeds);
        double leftSpeed = wheelSpeeds.leftMetersPerSecond;
        double rightSpeed = wheelSpeeds.rightMetersPerSecond;

        // Scale wheel speeds and apply to motors
        leftMotor.set(leftSpeed / DriveSubsystemConstants.maxVelocityMetersPerSecond);
        rightMotor.set(rightSpeed / DriveSubsystemConstants.maxVelocityMetersPerSecond);
    }

    @Override
    public void periodic() {
        leftDistance = leftEncoder.getPosition() * DISTANCE_PER_ENCODER_TICK;
        rightDistance = rightEncoder.getPosition() * DISTANCE_PER_ENCODER_TICK;
        

        // Update odometry with current encoder readings
        currentPose = odometry.update(
            gyro.getRotation2d(), // Current gyro angle
            leftDistance,
            rightDistance
        );

        // Display current pose and gyro angle on SmartDashboard
        SmartDashboard.putNumber("Gyro Angle", gyro.getAngle());
        SmartDashboard.putNumber("leftv",leftEncoder.getVelocity() * DISTANCE_PER_ENCODER_TICK);
        SmartDashboard.putNumber("rightv",rightEncoder.getVelocity() * DISTANCE_PER_ENCODER_TICK);
        SmartDashboard.putString("Robot Pose", currentPose.toString());

        // Update speed limit if it has changed on SmartDashboard
        double newSpeedLimit = SmartDashboard.getNumber("Speed Limit", defaultSpeedLimit);
        if (newSpeedLimit != speedLimit) {
            speedLimit = newSpeedLimit;
            System.out.println("Speed Limit updated to: " + speedLimit);
        }
    }

    /**
     * Arcade-style driving.
     */
    public void arcadeDrive(double speed, double rotation) {
        differentialDrive.arcadeDrive(speed * speedLimit, rotation * speedLimit);
    }

    /**
     * Tank-style driving.
     */
    public void tankDrive(double leftSpeed, double rightSpeed) {
        differentialDrive.tankDrive(leftSpeed * speedLimit, rightSpeed * speedLimit);
    }

    /**
     * Stops the drivetrain.
     */
    public void stop() {
        leftMotor.stopMotor();
        rightMotor.stopMotor();
    }
}
