package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import java.util.ArrayList;
import java.lang.reflect.Array;
import com.qualcomm.robotcore.util.Hardware;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name="TeleOp V7")
public class TeleOpV1 extends TeleOpSystem {


  /** Main Execution **/
  public void runOpMode() throws InterruptedException {

        /** Init Code */
        super.runOpMode();
        //AutoArmThread autoThread = new AutoArmThread(currentState);
        //ManualArmThread manualThread = new ManualArmThread();

        String[] data = new String[4];
        waitForStart();
        boolean outputting = false;
        double currentSlidePosition = 0.0;

        /** Starting Auto Thread */
        //manualThread.start();

        /** Main Loop */
        while(opModeIsActive()) {

          /** Update state of both gamepads */
          gamepad.update1();
          gamepad.update2();

          /** Mecanum Drive */
          turn = (gamepad1.right_trigger - gamepad1.left_trigger)  * TURN_COEFF;
          setDirectionVector(gamepad1.left_stick_x, gamepad1.left_stick_y);

          /* Speed Toggle */
          if(gamepad1.left_bumper) {
                DRIVE_COEFF = LINEAR_COEFF_MIN;
                TURN_COEFF = TURN_COEFF_MIN;
          } else if(gamepad1.right_bumper) {
                DRIVE_COEFF = LINEAR_COEFF_MAX;
                TURN_COEFF = TURN_COEFF_NORM;
          } else {
                DRIVE_COEFF = LINEAR_COEFF_NORM;
                TURN_COEFF = TURN_COEFF_NORM;
          }

          /** Power Calculation */
          motorLF.setPower(DRIVE_COEFF * (dy - dx - turn));
          motorLB.setPower(DRIVE_COEFF * (dy + dx - turn));
          motorRF.setPower(DRIVE_COEFF * (-dy - dx - turn));
          motorRB.setPower(DRIVE_COEFF * (-dy + dx - turn));

          if(gamepad.dpadDownToggle2) {
            rightIntakeDeploy.setPosition(0.55);
            leftIntakeDeploy.setPosition(0);
          } else if(!outputting) {
            rightIntakeDeploy.setPosition(0.11);
            leftIntakeDeploy.setPosition(0.4);
          }

          /** Intake Control */
          if(gamepad.xToggle1) {
            leftIntake.setPower(0.5);
            rightIntake.setPower(-0.5);
          } else if(gamepad.rightStickToggle1) {
            outputting = true;
            rightIntakeDeploy.setPosition(0.26);
            leftIntakeDeploy.setPosition(0.21);
            sleep(400);
            leftIntake.setPower(-1);
            rightIntake.setPower(1);
            sleep(400);
            gamepad.rightStickToggle1 = false;
          } else {
            leftIntake.setPower(gamepad1.right_stick_y);
            rightIntake.setPower(-gamepad1.right_stick_y);
            outputting = false;
          }

          // Manual Output
          armControlManual();
          controlBlock();

/**
          currentSlidePosition = slideMotor.getCurrentPosition();
          if(currentSlidePosition < 0)
            slideMotor.setPower(-gamepad2.right_stick_y * 0.7);
          else
            slideMotor.setPower(-Math.abs(gamepad2.right_stick_y) * 0.4);
   **/

          slideControlManual();

          /** All on-robot servo control */
          servoControl();

          /** Telemetry Calls **/
          data[0] = "Lift Position " + lift.getCurrentPosition();
          data[1] = "Lift Desired Position " + liftPos;
          data[2] = "Base Speed " + DRIVE_COEFF;
          data[3] = "Slide Position " + slideMotor.getCurrentPosition();
          t(data);
        }
  }

  /** The class contains the code for the automatic arm thread. This utilizes a state-machine
   *  to move the arm between different positions with the single press of a button.
   */
  private class AutoArmThread extends Thread {

        /** Vars **/
        private TeleOpStateMachine state;
        private boolean exit;

        public AutoArmThread(TeleOpStateMachine state) {
          this.state = state;
          exit = false;
        }

        /** The main thread loop */
        @Override
        public void run() {
          try {
                while(opModeIsActive() && !isInterrupted()) {
                  if(gamepad2.a || gamepad2.x || gamepad2.y)
                        armControlAutomatic(gamepad2.y, gamepad2.a, gamepad2.x);
                  else
                    activateLift(200, 0.2);
                }
          } catch(Exception e) {}
        }

        /** Stops the current thread */
        public void stopThread() {
          exit = true;
        }

        /** Checks if current thread is running */
        public boolean isRunning() {
          return !exit;
        }

  }

  /** The class contains the code for the lift arm to manually controlled by a driver. This runs in its
   *  own separate thread.
   */
  private class ManualArmThread extends Thread {

        /** Vars */
        private boolean exit;

        public ManualArmThread() {
          exit = false;
        }

        /** Main thread loop */
        @Override
        public void run() {
          try {
                while(opModeIsActive() && !isInterrupted()) {
                  armControlManual();
                  //controlBlock(gamepad2.right_bumper, gamepad2.left_bumper);
                }
            } catch(Exception e) {}
        }

        /** Stops the thread */
        public void stopThread() {
          exit = true;
        }

        /** Checks whether or not the current thread is running */
        public boolean isRunning() {
          return exit;
        }
    }

}
