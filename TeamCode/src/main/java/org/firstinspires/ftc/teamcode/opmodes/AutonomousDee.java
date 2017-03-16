package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "AutonomousDee", group = "AutonomousMode")
//@Disabled
public class AutonomousDee extends AutonomousMode {

    @Override
    protected void initOpMode() throws InterruptedException {
        initHardware();
    }

    protected void runOp() throws InterruptedException {
        while (!isStopRequested() && gyroSensor.isCalibrating()) {
            sleep(50);
            idle();
        }

        // Move forward 20 cm keeping 0 degrees
        gyroDrive(FORWARD_SPEED, cmToInch(20), 0);

    }

    protected void exitOpMode() throws InterruptedException {
        stopMotors();
    }

}
