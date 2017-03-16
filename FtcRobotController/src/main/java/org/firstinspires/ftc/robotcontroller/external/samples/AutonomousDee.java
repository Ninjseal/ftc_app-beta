package org.firstinspires.ftc.robotcontroller.external.samples;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous(name = "AutonomousDee", group = "AutonomousMode")
//@Disabled
public class AutonomousDee extends AutonomousMode {

    @Override
    protected void initOpMode() throws InterruptedException {
        initHardware();
    }

    protected void runOp() throws InterruptedException {
        /*while (!isStopRequested() && gyroSensor.isCalibrating()) {
            sleep(50);
            idle();
        }*/

        // Init wheels motors
        leftMotorF.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftMotorB.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightMotorF.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightMotorB.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Move forward 20 cm keeping 0 degrees
        encoderDrive(FORWARD_SPEED, 5, 5, 3);

    }

    protected void exitOpMode() throws InterruptedException {
        stopMotors();
    }

}
