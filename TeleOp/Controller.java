package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Hardware;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;


public class Controller extends LinearOpMode {

  /** Allows for toggle control during TeleOp. Flexible design means that
    * toggle controls are optional (based on variable states).
    */

  /* Constant */
  final int DEBOUNCING_NUMBER_OF_SAMPLES = 10;

  /* Button Counters and State Variables */
  int aCount1, bCount1, xCount1, yCount1;
  int aCount2, bCount2, xCount2, yCount2;
  boolean aToggle1, bToggle1, xToggle1, yToggle1;
  boolean aToggle2, bToggle2, xToggle2, yToggle2;

  /** Joystick Button Counter and State Variable */
  int rightStick1, leftStick1;
  int rightStick2, leftStick2;
  boolean rightStickToggle1, leftStickToggle1;
  boolean rightStickToggle2, leftStickToggle2;

  /* Dpad Counters and State Variables */
  int dpadDownCount1, dpadUpCount1;
  int dpadDownCount2, dpadUpCount2;
  boolean dpadDownToggle1, dpadUpToggle1;
  boolean dpadDownToggle2, dpadUpToggle2;

  /* Bumper Counters and State Variables */
  int leftBumperCount1, rightBumperCount1;
  int leftBumperCount2, rightBumperCount2;
  boolean leftBumperToggle1, rightBumperToggle1;
  boolean leftBumperToggle2, rightBumperToggle2;

  Gamepad gamepad1, gamepad2;

  public Controller(Gamepad g1, Gamepad g2) {
    gamepad1 = g1;
    gamepad2 = g2;
  }

  /* Handles toggle control for Gamepad 1 */
  public void update1() {

    if(gamepad1.a)
      aCount1++;
    else
      aCount1 = 0;
    if(aCount1 == DEBOUNCING_NUMBER_OF_SAMPLES)
      aToggle1 = !aToggle1;

    if(gamepad1.b)
      bCount1++;
    else
      bCount1 = 0;
    if(bCount1 == DEBOUNCING_NUMBER_OF_SAMPLES)
      bToggle1 = !bToggle1;

    if(gamepad1.x)
      xCount1++;
    else
      xCount1 = 0;
    if(xCount1 == DEBOUNCING_NUMBER_OF_SAMPLES)
      xToggle1 = !xToggle1;

    if(gamepad1.y)
      yCount1++;
    else
      yCount1 = 0;
    if(yCount1 == DEBOUNCING_NUMBER_OF_SAMPLES)
      yToggle1 = !yToggle1;

    if(gamepad1.dpad_down)
      dpadDownCount1++;
    else
      dpadDownCount1 = 0;
    if(dpadDownCount1 == DEBOUNCING_NUMBER_OF_SAMPLES)
      dpadDownToggle1 = !dpadDownToggle1;

    if(gamepad1.dpad_up)
      dpadUpCount1++;
    else
      dpadUpCount1 = 0;
    if(dpadUpCount1 == DEBOUNCING_NUMBER_OF_SAMPLES)
      dpadUpToggle1 = !dpadUpToggle1;

    if(gamepad1.left_bumper)
      leftBumperCount1++;
    else
      leftBumperCount1 = 0;
    if(leftBumperCount1 == DEBOUNCING_NUMBER_OF_SAMPLES)
      leftBumperToggle1 = !leftBumperToggle1;

    if(gamepad1.right_bumper)
      rightBumperCount1++;
    else
      rightBumperCount1 = 0;
    if(rightBumperCount1 == DEBOUNCING_NUMBER_OF_SAMPLES)
      rightBumperToggle1 = !rightBumperToggle1;

    if(gamepad1.left_stick_button)
      leftStick1++;
    else
      leftStick1 = 0;
    if(leftStick1 == DEBOUNCING_NUMBER_OF_SAMPLES)
      leftStickToggle1 = !leftStickToggle1;

    if(gamepad1.right_stick_button)
      rightStick1++;
    else
      rightStick1 = 0;
    if(rightStick1 == DEBOUNCING_NUMBER_OF_SAMPLES)
      rightStickToggle1 = !rightStickToggle1;

    if(gamepad2.left_stick_button)
      leftStick2++;
    else
      leftStick2 = 0;
    if(leftStick2 == DEBOUNCING_NUMBER_OF_SAMPLES)
      leftStickToggle2 = !leftStickToggle2;

    if(gamepad2.right_stick_button)
      rightStick2++;
    else
      rightStick2 = 0;
    if(rightStick2 == DEBOUNCING_NUMBER_OF_SAMPLES)
      rightStickToggle2 = !rightStickToggle2;

  }

  /* Handles toggle control for Gamepad 2 */
  public void update2() {

    if(gamepad2.a)
      aCount2++;
    else
      aCount2 = 0;
    if(aCount2 == DEBOUNCING_NUMBER_OF_SAMPLES)
      aToggle2 = !aToggle2;

    if(gamepad2.b)
      bCount2++;
    else
      bCount2 = 0;
    if(bCount2 == DEBOUNCING_NUMBER_OF_SAMPLES)
      bToggle2 = !bToggle2;

    if(gamepad2.x)
      xCount2++;
    else
      xCount2 = 0;
    if(xCount2 == DEBOUNCING_NUMBER_OF_SAMPLES)
      xToggle2 = !xToggle2;

    if(gamepad2.y)
      yCount2++;
    else
      yCount2 = 0;
    if(yCount2 == DEBOUNCING_NUMBER_OF_SAMPLES)
      yToggle2 = !yToggle2;

    if(gamepad2.dpad_down)
      dpadDownCount2++;
    else
      dpadDownCount2 = 0;
    if(dpadDownCount2 == DEBOUNCING_NUMBER_OF_SAMPLES)
      dpadDownToggle2 = !dpadDownToggle2;

    if(gamepad2.dpad_up)
      dpadUpCount2++;
    else
      dpadUpCount2 = 0;
    if(dpadUpCount2 == DEBOUNCING_NUMBER_OF_SAMPLES)
      dpadUpToggle2 = !dpadUpToggle2;

    if(gamepad2.left_bumper)
      leftBumperCount2++;
    else
      leftBumperCount2 = 0;
    if(leftBumperCount2 == DEBOUNCING_NUMBER_OF_SAMPLES)
      leftBumperToggle2 = !leftBumperToggle2;

    if(gamepad2.right_bumper)
      rightBumperCount2++;
    else
      rightBumperCount2 = 0;
    if(rightBumperCount2 == DEBOUNCING_NUMBER_OF_SAMPLES)
      rightBumperToggle2 = !rightBumperToggle2;

  }

  public void runOpMode() {}

}
