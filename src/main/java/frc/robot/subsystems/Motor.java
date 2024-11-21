package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;


import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Motor extends SubsystemBase{
    CANSparkMax driveMotor = new CANSparkMax(0, MotorType.kBrushless);

    public Motor() {
    }

    public void runMotor(){
        driveMotor.set(.5);
    }

    public void stopMotor() {
        driveMotor.stopMotor();
    }
}
