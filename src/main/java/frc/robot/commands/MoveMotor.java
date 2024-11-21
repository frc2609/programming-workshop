package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Motor;

public class MoveMotor extends Command {
    private Motor motor;
    public MoveMotor(Motor motor){
        this.motor = motor;

        addRequirements(motor);
    }

    @Override
    public void execute() {
        motor.runMotor();
    }

    @Override
    public void end(boolean interrupted) {
        motor.stopMotor();
    }
}
