package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "DriverFinal", group = "TeleOp")
//@Disabled
public class DriverFinal extends OpMode {

    // Motors
    private DcMotor cappingMotor = null;
    private DcMotor throwMotor = null;
    private DcMotor vacuumMotor = null;
    private DcMotor leftMotorF = null;
    private DcMotor leftMotorB = null;
    private DcMotor rightMotorF = null;
    private DcMotor rightMotorB = null;
    // Servos
    private Servo servoBox = null;
    private Servo servoSelector = null;
    private Servo servoClaw = null;
    private Servo servoBeacon = null;
    private Servo servoCapping = null;
    // Constants
    private static final double SELECTOR_UP = 1.0;
    private static final double SELECTOR_DOWN = 0.3;
    private static final double BOX_UP = 0.0;
    private static final double BOX_DOWN = 0.6;
    private static final double MID_SERVO = 0.5;
    private static final double CLAW_UP = 1.0;
    private static final double CLAW_DOWN = 0.0;
    private static final double CAP_UP = 1.0;
    private static final double CAP_DOWN = 0.0;
    private static final double BEACON_LEFT = 0.0;
    private static final double BEACON_RIGHT = 1.0;
    private double clipValue = 0.7;
    // Additional helper variables
    private double leftWheelsPower = 0, rightWheelsPower = 0;

    private double deadzone = 0.1;
    private int throwDistance = 1565;

    private double throwPower = 1;
    private double vacuumPower = 0.9;
    private double cappingPower = 0.8;

    @Override
    public void init() {
        // Map the motors
        cappingMotor = hardwareMap.dcMotor.get("capping");
        throwMotor = hardwareMap.dcMotor.get("throw");
        vacuumMotor = hardwareMap.dcMotor.get("vacuum");
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
        // Set wheel motor directions
        leftMotorF.setDirection(DcMotor.Direction.FORWARD);
        leftMotorB.setDirection(DcMotor.Direction.FORWARD);
        rightMotorF.setDirection(DcMotor.Direction.REVERSE);
        rightMotorB.setDirection(DcMotor.Direction.REVERSE);
        // Set the stopping method for wheels
        leftMotorF.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        leftMotorB.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rightMotorF.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rightMotorB.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        // Set the throw motor
        throwMotor.setDirection(DcMotor.Direction.REVERSE);
        throwMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        throwMotor.setMaxSpeed(1120);
        // Set the capping mechanism direction
        cappingMotor.setDirection(DcMotor.Direction.FORWARD);
        // Set the vacuum mechanism direction
        vacuumMotor.setDirection(DcMotor.Direction.FORWARD);
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
        cappingMotor.setPower(0);
        vacuumMotor.setPower(0);
        // Initialize servo positions
        servoClaw.setPosition(MID_SERVO);
        servoCapping.setPosition(MID_SERVO);
        servoBox.setPosition(BOX_DOWN);
        servoSelector.setPosition(SELECTOR_DOWN);
        servoBeacon.setPosition(MID_SERVO);

        telemetry.addData("Say", "Hello Driver!");
        telemetry.addData("Status", "Initialized");
        telemetry.addData(">", "Press Start to run the motors.");
        telemetry.update();
    }

    @Override
    public void loop() {

        // Update joystick values
        if (clipValue == 0.7) {
            leftWheelsPower = gamepad1.left_stick_y;
            rightWheelsPower = gamepad1.right_stick_y;
        } else {
            leftWheelsPower = -gamepad1.right_stick_y;
            rightWheelsPower = -gamepad1.left_stick_y;
        }

        // Check the deadzone
        if (Math.abs(leftWheelsPower) < deadzone) leftWheelsPower = 0;
        if (Math.abs(rightWheelsPower) < deadzone) rightWheelsPower = 0;

        if(leftWheelsPower <= -clipValue ) {
            leftWheelsPower = -clipValue;
        }
        if(rightWheelsPower <= -clipValue) {
            rightWheelsPower = -clipValue;
        }
        if(rightWheelsPower >= clipValue) {
            rightWheelsPower = clipValue;
        }
        if(leftWheelsPower >= clipValue) {
            leftWheelsPower = clipValue;
        }
        leftMotorF.setPower(leftWheelsPower);
        leftMotorB.setPower(leftWheelsPower);
        rightMotorF.setPower(rightWheelsPower);
        rightMotorB.setPower(rightWheelsPower);

        // Throw mechanism
        if (gamepad2.a) {
            throwBall(throwPower, throwDistance);  // Throw ball using encoder
        }
        // Throw-safety mechanism
        if(gamepad2.right_bumper) {
            throwMotor.setPower(0.2);
        } else {
            throwMotor.setPower(0.0);
        }

        // Vacuum mechanism
        if (gamepad1.right_bumper) {
            vacuumMotor.setPower(vacuumPower);
        } else if (gamepad1.left_bumper) {
            vacuumMotor.setPower(-vacuumPower);
        } else {
            vacuumMotor.setPower(0);  // Stop the motor
        }
        // Capping mechanism
        if (gamepad1.dpad_up) {
            cappingMotor.setPower(cappingPower);
        } else if (gamepad1.dpad_down) {
            cappingMotor.setPower(-cappingPower);
        } else {
            cappingMotor.setPower(0);  // Stop the motor
        }

        // Servo Box Mechanism
        if (gamepad2.dpad_down) {
            servoBox.setPosition(BOX_DOWN);  // Servo Box DOWN position
        } else if (gamepad2.dpad_up) {
            servoBox.setPosition(BOX_UP);  // Servo Box UP position
        }
        // Servo Selector Mechanism
        if (gamepad2.b) {
            servoSelector.setPosition(SELECTOR_UP);  // Servo Selector UP position
        } else {
            servoSelector.setPosition(SELECTOR_DOWN);  // Servo Selector DOWN position
        }
        // Servo Claw Mechanism
        if (gamepad1.a) {
            servoClaw.setPosition(CLAW_UP);  // Servo Claw UP position
        } else if (gamepad1.b) {
            servoClaw.setPosition(CLAW_DOWN);  // Servo Claw DOWN position
        } else {
            servoClaw.setPosition(MID_SERVO);
        }
        // Servo Cap Mechanism
        if(gamepad1.left_trigger > 0.5) {
            servoCapping.setPosition(CAP_UP);
        } else if(gamepad1.right_trigger > 0.5) {
            servoCapping.setPosition(CAP_DOWN);
        } else {
            servoCapping.setPosition(MID_SERVO);
        }

        // Servo Beacon Mechanism
        if(gamepad1.dpad_left) {
            servoBeacon.setPosition(BEACON_LEFT);
        } else if(gamepad1.dpad_right) {
            servoBeacon.setPosition(BEACON_RIGHT);
        }

        // Prepare robot for end-game
        if (gamepad1.x) {
            clipValue = 0.3;  // Set the clip value to .3
        } else if (gamepad1.y) {
            clipValue = 0.7;  // Set the clip value to .7
        }

        telemetry.addData("leftWheelsPower", leftWheelsPower);
        telemetry.addData("rightWheelsPower", rightWheelsPower);
        telemetry.addData(">", "Press Stop to end test.");
        telemetry.update();
    }

    @Override
    public void stop() {
        stopMotors();  // Stop all the motors
    }

    // This function makes the robot throw a ball using encoder, then rearm
    public void throwBall(double power, int distance) {
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

    // This function stops all the motors of the robot
    public void stopMotors() {
        throwMotor.setPower(0);
        vacuumMotor.setPower(0);
        cappingMotor.setPower(0);
        leftMotorF.setPower(0);
        leftMotorB.setPower(0);
        rightMotorF.setPower(0);
        rightMotorB.setPower(0);
    }
    //StopMotors

}