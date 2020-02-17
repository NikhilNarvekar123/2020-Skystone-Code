package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.robotcore.external.State;
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

        String[] data = new String[4];
        waitForStart();
        boolean outputting = false;
        double currentSlidePosition = 0.0;

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

          if(gamepad.dpadUpToggle2) {
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

          slideControlManual();
          armControlManual();

/**
          if(gamepad2.left_trigger > 0) {
            lift.setPower(0.4);
          } else if(gamepad2.right_trigger > 0) {
            lift.setPower(-0.4);
          } else {
            lift.setPower(0);
          }
**/

          /** All on-robot servo control */
          servoControl();
          controlBlock();

          /** Telemetry Calls **/
          data[0] = "Lift Position " + lift.getCurrentPosition();
          data[1] = "Lift Desired Position " + liftPos;
          data[2] = "Base Speed " + currentLiftState;
          data[3] = "Slide Position " + slideMotor.getCurrentPosition();
          t(data);
        }
  }

}
