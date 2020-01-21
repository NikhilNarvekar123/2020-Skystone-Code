package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.Hardware;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name="TeleOp V1")
public class TeleOpV1 extends TeleopSystem {

  /** Main Execution */
  public void runOpMode() throws InterruptedException {

    super.runOpMode();

    int sum = 2;
    boolean cond = false;

    while(opModeIsActive()) {

      // Update state of both gamepads
      gamepad.update1();
      gamepad.update2();

      /** Mecanum Drive **/
      turn = (gamepad1.right_trigger - gamepad1.left_trigger)  * TURN_COEFF;
      setDirectionVector(gamepad1.left_stick_x, gamepad1.left_stick_y);

      /* Speed Toggle */
      if(gamepad1.left_bumper) { DRIVE_COEFF = 0.3; TURN_COEFF = 0.5; }
      else { DRIVE_COEFF = 1; TURN_COEFF = 0.8; }

      motorLF.setPower(DRIVE_COEFF * (dy - dx - turn));
      motorLB.setPower(DRIVE_COEFF * (dy + dx - turn));
      motorRF.setPower(DRIVE_COEFF * (-dy - dx - turn));
      motorRB.setPower(DRIVE_COEFF * (-dy + dx - turn));


      /** Servo Toggles **/
      if(gamepad.yToggle1)
        blockServo.setPosition(0.13);
      else
        blockServo.setPosition(0.54);

      if(gamepad.xToggle1 || gamepad.xToggle2) {
        leftIntake.setPower(0.5);
        rightIntake.setPower(-0.5);
      } else {
        leftIntake.setPower(gamepad2.right_stick_y);
        rightIntake.setPower(-gamepad2.right_stick_y);
      }

      if(gamepad2.y)
        capServo.setPosition(1);
      else
        capServo.setPosition(0.3);

      if(gamepad2.left_bumper) {
        leftGrabber.setPosition(0.5);
        rightGrabber.setPosition(0.67);
      }
      else if(gamepad2.right_bumper) {
        leftGrabber.setPosition(0.36);
        rightGrabber.setPosition(0.8);
      }

      if(gamepad.dpadDownToggle1 || gamepad.dpadDownToggle2) {
        leftFound.setPosition(0.7);
        rightFound.setPosition(0.24);
      } else {
        leftFound.setPosition(0.35);
        rightFound.setPosition(0.65);
      }


      /** Lift System **/
      double up = gamepad2.right_trigger;
      double down = gamepad2.left_trigger;
      boolean maintain = gamepad2.a;

      if(up > 0) {
        if(liftPos + rangeScaler(up) < 904)
          liftPos += rangeScaler(up);
        else if(liftPos + rangeScaler(up) >= 904)
          liftPos = 904;
      }

      if(down > 0) {
        if(liftPos - rangeScaler(down) > 0)
          liftPos -= rangeScaler(down);
        else if(liftPos - rangeScaler(down) <= 0)
          liftPos = 0;
      }

      if(down == 0 && up == 0) {
        liftPos -= rangeScaler(up);
      }

      if(!maintain) {
        lift.setTargetPosition((int)Math.round(liftPos));
        lift.setPower(0.2);
        lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
      } else {
        liftPos = 200;
      }


      /** Telemetry Calls **/
      telemetry.addData("Lift Position: ", lift.getCurrentPosition());
      telemetry.addData("Lift Desired Position: ", liftPos);
      telemetry.addData("Base Speed: " , DRIVE_COEFF);
      telemetry.addData("Auto Intake On? ", (gamepad.xToggle1 || gamepad.xToggle2));
      telemetry.update();
    }
  }
}
