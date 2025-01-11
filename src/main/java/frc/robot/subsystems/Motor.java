package frc.robot.subsystems;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;


import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Motor extends SubsystemBase{
    SparkMax driveMotor = new SparkMax(10, MotorType.kBrushless);

    public Motor() {
    }

    public void runMotor(){
        driveMotor.set(.5);
    }

    public void stopMotor() {
        driveMotor.stopMotor();
    }
}
