package org.firstinspires.ftc.robotcontroller.external.samples;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous(name = "AutonomousModeTest", group = "AutonomousMode")
//@Disabled
public class AutonomousModeTest extends AutonomousMode {

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

        // Move forward for 1 second
        forward(FORWARD_SPEED);
        wait(1.0);
        stopWheels();

        // Throw Ball 1
        throwBall(throwPower, throwDistance);
        wait(1.0);
        // Throw Ball 2
        servoSelector.setPosition(SELECTOR_UP);
        wait(1.0);
        servoSelector.setPosition(SELECTOR_DOWN);
        wait(1.5);
        throwBall(throwPower, throwDistance);
        wait(1.0);

        // Move forward 5 cm keeping 0 degrees
        gyroDrive(FORWARD_SPEED, cmToInch(5), 0);

        // Turn 90 degrees CW
        gyroTurn(TURN_SPEED, 45);
    }

    protected void exitOpMode() throws InterruptedException {
        stopMotors();
    }

}
