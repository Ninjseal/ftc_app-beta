package org.firstinspires.ftc.teamcode.opmodes;

import android.graphics.Color;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public abstract class AutonomousMode extends LinearOpMode {

    // Motors
    protected DcMotor throwMotor = null;
    protected DcMotor leftMotorF = null;
    protected DcMotor leftMotorB = null;
    protected DcMotor rightMotorF = null;
    protected DcMotor rightMotorB = null;
    // Servos
    protected Servo servoBox = null;
    protected Servo servoSelector = null;
    protected Servo servoClaw = null;
    protected Servo servoBeacon = null;
    protected Servo servoCapping = null;
    // Sensors
    protected ModernRoboticsI2cGyro gyroSensor = null;
    protected ColorSensor colorSensor = null;
    protected OpticalDistanceSensor odsSensor = null;
    protected ModernRoboticsI2cRangeSensor rangeSensor = null;
    // Constants
    protected static final double     COUNTS_PER_MOTOR_REV    = 1440 ;    // eg: TETRIX Motor Encoder
    protected static final double     DRIVE_GEAR_REDUCTION    = 2.0 ;     // This is < 1.0 if geared UP
    protected static final double     WHEEL_DIAMETER_INCHES   = 4.0 ;     // For figuring circumference
    protected static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);

    protected static final double SELECTOR_UP = 1.0;
    protected static final double SELECTOR_DOWN = 0.3;
    protected static final double BOX_UP= 0.0;
    protected static final double MID_SERVO = 0.5;
    protected static final double BEACON_LEFT = 0.0;
    protected static final double BEACON_RIGHT = 1.0;
    protected static final double FORWARD_SPEED = 0.7;
    protected static final double TURN_SPEED = 0.2;
    protected static final double HEADING_THRESHOLD = 1.0;
    protected static final double P_TURN_COEFF = 0.1;
    protected static final double P_DRIVE_COEFF = 0.15;
    // Additional helper variables
    protected double leftWheelsPower = 0, rightWheelsPower = 0;
    protected int throwDistance = 1565;
    protected double throwPower = 1;
    protected ElapsedTime runtime = new ElapsedTime();

        @Override
        public void runOpMode() throws InterruptedException {
            initOpMode();

            telemetry.addData("Status", "Ready to run");    //
            telemetry.update();

            // Wait for Start button to be pressed
            waitForStart();
        try {
            runOp();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            exitOpMode();
        }
    }

    protected abstract void initOpMode() throws InterruptedException;
    protected abstract void runOp() throws InterruptedException;
    protected abstract void exitOpMode() throws InterruptedException;

    public void initHardware() {
        // Map the motors
        throwMotor = hardwareMap.dcMotor.get("throw");
        leftMotorF = hardwareMap.dcMotor.get("left_drive_front");
        leftMotorB = hardwareMap.dcMotor.get("left_drive_back");
        rightMotorF = hardwareMap.dcMotor.get("right_drive_front");
        rightMotorB = hardwareMap.dcMotor.get("right_drive_back");
        // Map the servos
        servoClaw = hardwareMap.servo.get("furca");
        servoBox = hardwareMap.servo.get("box");
        servoSelector = hardwareMap.servo.get("selector");
        servoBeacon = hardwareMap.servo.get("beacon");
        servoCapping = hardwareMap.servo.get("clapita");
        // Map the sensors
        gyroSensor = (ModernRoboticsI2cGyro) hardwareMap.gyroSensor.get("gyro");
        rangeSensor = hardwareMap.get(ModernRoboticsI2cRangeSensor.class, "range");
        colorSensor = hardwareMap.colorSensor.get("color");
        odsSensor = hardwareMap.opticalDistanceSensor.get("ods");
        // Set the wheel motors
        leftMotorF.setDirection(DcMotor.Direction.REVERSE);
        leftMotorB.setDirection(DcMotor.Direction.REVERSE);
        rightMotorF.setDirection(DcMotor.Direction.FORWARD);
        rightMotorB.setDirection(DcMotor.Direction.FORWARD);
        leftMotorF.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftMotorB.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightMotorF.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightMotorB.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        // Set the throw motor
        throwMotor.setDirection(DcMotor.Direction.REVERSE);
        throwMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        throwMotor.setMaxSpeed(1120);
        // Set servo directions
        servoClaw.setDirection(Servo.Direction.FORWARD);
        servoBox.setDirection(Servo.Direction.FORWARD);
        servoSelector.setDirection(Servo.Direction.FORWARD);
        servoBeacon.setDirection(Servo.Direction.FORWARD);
        servoCapping.setDirection(Servo.Direction.FORWARD);
        // Set the motors power to 0
        leftMotorF.setPower(0);
        leftMotorB.setPower(0);
        rightMotorF.setPower(0);
        rightMotorB.setPower(0);
        throwMotor.setPower(0);
        // Initialize servo positions
        servoClaw.setPosition(MID_SERVO);
        servoCapping.setPosition(MID_SERVO);
        servoBox.setPosition(BOX_UP);
        servoSelector.setPosition(SELECTOR_DOWN);
        servoBeacon.setPosition(MID_SERVO);
        // Calibrate gyro
        gyroSensor.calibrate();
    }

    // Autonomous function for following the line to the beacon
    protected void followLine() {
        // Line variable
        double PERFECT_COLOR_VALUE = 0.04;
        double followPower = -0.2;
        // Distance variable
        double dist_init = 20;
        // Flag variable
        boolean finish = false;
        // Motor power variables
        double powerLeftMotorB = 0.0;
        double powerLeftMotorF = 0.0;
        double powerRightMotorB = 0.0;
        double powerRightMotorF = 0.0;

        while(!finish) {
            double correction = (PERFECT_COLOR_VALUE - odsSensor.getLightDetected()) * 5;

            // Set the powers so they are no less than .075 and apply to correction
            if (correction <= 0) {
                powerLeftMotorB = followPower - correction;
                powerLeftMotorF = followPower - correction;
                powerRightMotorB = followPower;
                powerRightMotorF = followPower;
            } else {
                powerLeftMotorB = followPower;
                powerLeftMotorF = followPower;
                powerRightMotorB = followPower + correction;
                powerRightMotorF = followPower + correction;
            }

            // Set the powers to the motors
            leftMotorB.setPower(Range.clip(powerLeftMotorB, -1, 1));
            leftMotorF.setPower(Range.clip(powerLeftMotorF, -1, 1));
            rightMotorB.setPower(Range.clip(powerRightMotorB, -1, 1));
            rightMotorF.setPower(Range.clip(powerRightMotorF, -1, 1));
            if(rangeSensor.getDistance(DistanceUnit.CM) <= dist_init) {
                finish = true;
            }
        }
        stopWheels();
    }
    //FollowLine

    // Autonomous function for detecting the right color and pressing the button
    protected void captureBeacon(boolean teamRed) {
        // Color variables
        double red = 0.0;
        double green = 0.0;
        double blue = 0.0;
        float hsvValues[] = {0F, 0F, 0F};
        final float values[] = hsvValues;
        // Distance variables
        double dist_init = 15;
        double dist_fin = 7;
        // Flag variables
        boolean done = false;
        boolean first_time = true;

        while (opModeIsActive() && !done) {
            red = colorSensor.red();
            green = colorSensor.green();
            blue = colorSensor.blue();

            Color.RGBToHSV(colorSensor.red() * 8, colorSensor.green() * 8, colorSensor.blue() * 8, hsvValues);
            forward(FORWARD_SPEED);
            if (rangeSensor.getDistance(DistanceUnit.CM) <= dist_init) {

                if (first_time == true) {
                    first_time = false;
                    stopWheels();

                    if (red > blue && red > green && teamRed) {
                        servoBeacon.setPosition(BEACON_RIGHT);
                    } else if (blue > red && blue > green && !teamRed) {
                        servoBeacon.setPosition(BEACON_LEFT);
                    }

                    wait(1.0);
                }

                if (rangeSensor.getDistance(DistanceUnit.CM) <= dist_fin) {
                    wait(0.5);
                    forward(-FORWARD_SPEED);
                    done = true;
                }

            }
        }
        wait(0.5);
        stopWheels();
    }
    //CaptureBeacon

    // Wait for a number of seconds
    protected void wait(double seconds) {
        runtime.reset();
        while (opModeIsActive() && (runtime.seconds() < seconds)) {
            telemetry.addData("Time Elapsed: ", "%2.5f S Elapsed", runtime.seconds());
            telemetry.update();
        }
    }
    //Wait

    // Gyro related autonomous functions
    protected void gyroDrive ( double speed, double distance, double angle) {
        int     newLeftTarget;
        int     newRightTarget;
        int     moveCounts;
        double  max;
        double  error;
        double  steer;
        double  leftSpeed;
        double  rightSpeed;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            moveCounts = (int)(distance * COUNTS_PER_INCH);
            newLeftTarget = leftMotorF.getCurrentPosition() + moveCounts;
            newRightTarget = rightMotorF.getCurrentPosition() + moveCounts;

            // Set Target and Turn On RUN_TO_POSITION
            leftMotorF.setTargetPosition(newLeftTarget);
            rightMotorF.setTargetPosition(newRightTarget);
            leftMotorB.setTargetPosition(newLeftTarget);
            rightMotorB.setTargetPosition(newRightTarget);

            leftMotorF.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightMotorF.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            leftMotorB.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightMotorB.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // start motion.
            speed = Range.clip(Math.abs(speed), 0.0, 1.0);
            forward(speed);

            // keep looping while we are still active, and BOTH motors are running.
            while (opModeIsActive() &&
                    (leftMotorF.isBusy() && rightMotorF.isBusy() && leftMotorB.isBusy() && rightMotorB.isBusy())) {

                // adjust relative speed based on heading error.
                error = getError(angle);
                steer = getSteer(error, P_DRIVE_COEFF);

                // if driving in reverse, the motor correction also needs to be reversed
                if (distance < 0)
                    steer *= -1.0;

                leftSpeed = speed - steer;
                rightSpeed = speed + steer;

                // Normalize speeds if any one exceeds +/- 1.0;
                max = Math.max(Math.abs(leftSpeed), Math.abs(rightSpeed));
                if (max > 1.0)
                {
                    leftSpeed /= max;
                    rightSpeed /= max;
                }

                move(leftSpeed,rightSpeed);

                // Display drive status for the driver.
                telemetry.addData("Err/St",  "%5.1f/%5.1f",  error, steer);
                telemetry.addData("Target",  "%7d:%7d",      newLeftTarget,  newRightTarget);
                telemetry.addData("Actual",  "%7d:%7d",      leftMotorF.getCurrentPosition(),
                        rightMotorF.getCurrentPosition());
                telemetry.addData("Speed",   "%5.2f:%5.2f",  leftSpeed, rightSpeed);
                telemetry.update();
            }

            // Stop all motion;
            stopWheels();

            // Turn off RUN_TO_POSITION
            leftMotorF.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            leftMotorB.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rightMotorF.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rightMotorB.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }
    //GyroDrive

    protected void gyroTurn (double speed, double angle) {

        // Keep looping while we are still active, and not on heading.
        while (opModeIsActive() && !onHeading(speed, angle, P_TURN_COEFF)) {
            // Update telemetry & Allow time for other processes to run.
            telemetry.update();
        }
    }
    //GyroTurn

    private boolean onHeading(double speed, double angle, double PCoeff) {
        double   error ;
        double   steer ;
        boolean  onTarget = false ;
        double leftSpeed;
        double rightSpeed;

        // determine turn power based on +/- error
        error = getError(angle);

        if (Math.abs(error) <= HEADING_THRESHOLD) {
            steer = 0.0;
            leftSpeed  = 0.0;
            rightSpeed = 0.0;
            onTarget = true;
        }
        else {
            steer = getSteer(error, PCoeff);
            rightSpeed  = speed * steer;
            leftSpeed   = -rightSpeed;
        }

        // Send desired speeds to motors.
        move(leftSpeed, rightSpeed);

        return onTarget;
    }

    private double getError(double targetAngle) {
        double robotError;

        // calculate error in -179 to +180 range  (
        robotError = targetAngle - gyroSensor.getIntegratedZValue();
        while (robotError > 180)  robotError -= 360;
        while (robotError <= -180) robotError += 360;
        return robotError;
    }

    private double getSteer(double error, double PCoeff) {
        return Range.clip(error * PCoeff, -1, 1);
    }
    //Gyro

    // Move the robot based on left and right powers
    protected void move(double leftWheelsPower, double rightWheelsPower) {
        leftMotorF.setPower(Range.clip(leftWheelsPower, -1, 1));
        leftMotorB.setPower(Range.clip(leftWheelsPower, -1, 1));
        rightMotorF.setPower(Range.clip(rightWheelsPower, -1, 1));
        rightMotorB.setPower(Range.clip(rightWheelsPower, -1, 1));
    }
    //Move

    // Move the robot forward or backward
    protected void forward(double power) {
        leftMotorF.setPower(power);
        leftMotorB.setPower(power);
        rightMotorF.setPower(power);
        rightMotorB.setPower(power);
    }
    //Forward

    // Strafe right for + vals and left for - vals
    protected void strafe(double power) {
        leftMotorF.setPower(power);
        leftMotorB.setPower(-power);
        rightMotorF.setPower(-power);
        rightMotorB.setPower(power);
    }
    //Strafe

    // Turn the robot CW for + vals and CCW for - vals
    protected void turn(double power) {
        leftMotorF.setPower(power);
        leftMotorB.setPower(power);
        rightMotorF.setPower(-power);
        rightMotorB.setPower(-power);
    }
    //Turn

    // This function makes the robot throw a ball using encoder, then rearm
    protected void throwBall(double power, int distance) {
        throwMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        throwMotor.setTargetPosition(distance);

        throwMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        throwMotor.setPower(power);

        while(throwMotor.isBusy()) {
            //wait until target position is reached
        }

        throwMotor.setPower(0);
        throwMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
    //ThrowBall

    // This function stops all the wheels of the robot
    protected void stopWheels() {
        leftMotorF.setPower(0);
        leftMotorB.setPower(0);
        rightMotorF.setPower(0);
        rightMotorB.setPower(0);
    }
    //StopWheels

    // This function stops all the motors of the robot
    protected void stopMotors() {
        throwMotor.setPower(0);
        leftMotorF.setPower(0);
        leftMotorB.setPower(0);
        rightMotorF.setPower(0);
        rightMotorB.setPower(0);
    }
    //StopMotors

}
