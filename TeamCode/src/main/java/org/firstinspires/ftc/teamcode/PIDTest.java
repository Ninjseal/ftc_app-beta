package org.firstinspires.ftc.teamcode;

/**
 * Created by Marius on 3/9/2017.
 */

public class PIDTest {
/*
    void moveBase(int speed) {
        motor[LeftBase] = speed;
        motor[RightBase] = speed;
    }

    // reading: ticks -- 627.2 ticks/revolution
    // 1 turn of the wheels -- 4 inch wheels = 627.2 *2 ticks
    // 4*pi inches = 627.2*2 ticks
    // ticks/inch = 627.2*2/(4*pi) = 99.82198

    // when my error is 30
    // the actual error in inches = 0.3

    int inchToTicks(float inch) {
        int ticks;
        ticks = (int) (inch * 99.82198);
        return ticks;

    }

    void PIDBaseControl(int target, int waitTime) {

        float Kp = .2f;
        int error;  // error = target - current
        int proportion;
        int finalPower;
        boolean timerBool = true;

        nMotorEncoder[LeftBase] = 0;  // Init wheels encoders
        nMotorEncoder[RightBase] = 0;


        while(?) {  <-- still needs to be filled out
            error = inchToTicks(target) - (nMotorEncoder[LeftBase]+nMotorEncoder[RightBase]);

            proportion = (int) (Kp*error);

            finalPower = proportion; // proportion + derivative + integral

            moveBase(finalPower);

            wait1Msec(40);

            if(error < 30) {
                timerBool = false;
            }

            if(timerBool) {
                ClearTimer(T1);
            }
        }
    }

    task main() {
        PIDBaseControl(12, 10);

        while(true) {
            wait1Msec(20);
        }
    }
*/
}
