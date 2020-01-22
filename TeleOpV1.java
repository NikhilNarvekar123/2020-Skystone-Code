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

      /** Auto Intake Toggle **/
      if(gamepad.xToggle1 || gamepad.xToggle2) {
        leftIntake.setPower(0.5);
        rightIntake.setPower(-0.5);
      } else {
        leftIntake.setPower(gamepad1.right_stick_y);
        rightIntake.setPower(-gamepad1.right_stick_y);
      }

      servoControl();
      armControl();

      /** Telemetry Calls **/
      t.log("Lift Position", lift.getCurrentPosition());
      t.log("Lift Desired Position", liftPos);
      t.log("Base Speed: ", DRIVE_COEFF);
      t.display();
    }
  }
}
