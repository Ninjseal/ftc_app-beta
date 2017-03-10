package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous(name = "AutonomousBeta", group = "AutonomousMode")
//@Disabled
public class AutonomousBeta extends AutonomousMode {

    @Override
    protected void initOpMode() throws InterruptedException {
        initHardware();
    }

    protected void runOp() throws InterruptedException {
        while (!isStopRequested() && gyroSensor.isCalibrating()) {
            sleep(50);
            idle();
        }

        // Init wheels motors
        leftMotorF.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftMotorB.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightMotorF.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightMotorB.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Move forward 5 inches keeping 0 degrees
        gyroDrive(FORWARD_SPEED, 5, 0);

        // Move forward 5 inches keeping 10 degrees
        gyroDrive(FORWARD_SPEED, 5, 10);

        // Turn 90 degrees CW
        gyroTurn(TURN_SPEED, 90);
    }

    protected void exitOpMode() throws InterruptedException {
        stopMotors();
    }

}
