package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.Hardware;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name="TeleOp V5")
public class TeleOpV1 extends TeleOpSystem {

  /** Main Execution **/
  public void runOpMode() throws InterruptedException {

    /** Init Code */
    super.runOpMode();
    AutoArmThread autoThread = new AutoArmThread(state);
    ManualArmThread manualThread = new ManualArmThread();
    String data[];
    waitForStart();

    /** Starting Auto Thread */
    autoThread.start();

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

      /** Intake Control */
      if(gamepad.xToggle1) {
        leftIntake.setPower(0.5);
        rightIntake.setPower(-0.5);
      } else {
        leftIntake.setPower(gamepad1.right_stick_y);
        rightIntake.setPower(-gamepad1.right_stick_y);
      }

      /** Arm Thread Control */
      if(gamepad2.left_trigger + gamepad2.right_trigger != 0) {
        autoThread.stop();
        manualThread.start();
      } else {
        if(!autoThread.isRunning())
          autoThread.start();
        if(manualThread.isRunning())
          manualThread.stop();
      }

      /** All on-robot servo control */
      servoControl();

      /** Telemetry Calls **/
      String[] data = {"Lift Position " + lift.getCurrentPosition(), "Lift Desired Position " + liftPos, "Base Speed " + DRIVE_COEFF};
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
        while(opModeIsActive() && !exit) {
          if(gamepad2.a || gamepad2.x || gamepad2.y)
            armControlAutomatic(gamepad2.y, gamepad2.a, gamepad2.x);
        }
      } catch(Exception e) {}
    }

    /** Stops the current thread */
    public void stop() {
      exit = true;
    }

    /** Checks if current thread is running */
    public void isRunning() {
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
        while(opModeIsActive() && !exit) {
          armControlManual();
        }
      } catch(Exception e) {}
    }

    /** Stops the thread */
    public void stop() {
      exit = true;
    }

    /** Checks whether or not the current thread is running */
    public void isRunning() {
      return exit;
    }

  }

}
