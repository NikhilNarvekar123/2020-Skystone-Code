package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Hardware;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

@Disabled
public class TeleOpSystem extends LinearOpMode {

  /* Vars */
  double DRIVE_COEFF = 1.0;
  double TURN_COEFF = 0.8;
  double LIFT_COEFF = 1.0;
  double MECANUM_A_SPACE = 6;
  double MECANUM_B_SPACE = 4;
  double dy, dx, turn = 0.0;
  double liftAdd = 0.0;
  double liftPos = 0.0;
  int stateCounter = 0;

  /* Components */
  DcMotor motorLF, motorLB, motorRF, motorRB;
  DcMotor lift, leftIntake, rightIntake;
  Servo blockServo;
  Servo leftFound, rightFound;
  Servo leftGrabber, rightGrabber, capServo;

  Controller gamepad;
  TeleOpStateMachine state = TeleOpStateMachine.BlockZero;
  Telemetry t = new Telemetry();

  /* Main Execution Loop */
  public void runOpMode() throws InterruptedException {

    /* Component SetUp */
    motorLF = hardwareMap.get(DcMotor.class,"motorLF");
    motorLB = hardwareMap.get(DcMotor.class,"motorLB");
    motorRF = hardwareMap.get(DcMotor.class,"motorRF");
    motorRB = hardwareMap.get(DcMotor.class,"motorRB");
    lift = hardwareMap.get(DcMotor.class,"lift");
    leftFound = hardwareMap.get(Servo.class,"leftFound");
    rightFound = hardwareMap.get(Servo.class,"rightFound");
    blockServo = hardwareMap.get(Servo.class,"blockServo");
    leftIntake = hardwareMap.get(DcMotor.class,"leftIntake");
    rightIntake = hardwareMap.get(DcMotor.class,"rightIntake");
    leftGrabber = hardwareMap.get(Servo.class,"leftPush");
    rightGrabber = hardwareMap.get(Servo.class,"rightPush");
    capServo = hardwareMap.get(Servo.class, "capServo");

    /* Init Code */
    lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    lift.setDirection(DcMotor.Direction.REVERSE);
    blockServo.setPosition(0.54);

    gamepad = new Controller(gamepad1,gamepad2);

    telemetry.addLine("All Systems Ready...");
    telemetry.update();

    waitForStart();

  }

  public void setState(boolean reverse, boolean restart) {

    if(restart) {
      state = TeleOpStateMachine.BlockOne;
    } else if(reverse) {
      state = state.prevState();
    } else {
      state = state.nextState();
    }

    if(state == TeleOpStateMachine.BlockOne) {

    } else if(state == TeleOpStateMachine.BlockTwo) {

    } else if(state == TeleOpStateMachine.BlockThree) {

    } else if(state == TeleOpStateMachine.BlockFour) {

    }





  }

  /* Returns a vector value to move the robot at */
  public void setDirectionVector(double x, double y) {

    double hyp = Math.sqrt(Math.pow(x,2) + Math.pow(y,2));

    if (x == 0 || y == 0)
    {
      dx = x;
      dy = y;
    }
    else if (Math.abs(x) > Math.abs(y))
    {
      dx = Math.signum(x) * hyp;
      dy = Math.signum(x) * (y / x) * hyp;
    }
    else
    {
      dx = Math.signum(y) * (x / y) * hyp;
      dy = Math.signum(y) * hyp;
    }


  }

  /* Alternate Control System, using stick to rotate as well?? */
  public double[] setSpeed(double x, double y) {
    double omega = Math.atan2(y/x);
    double[] speeds = new double[4];
    speeds[0] = y - x + (omega * (MECANUM_A_SPACE + MECANUM_B_SPACE));
    speeds[1] = y + x - (omega * (MECANUM_A_SPACE + MECANUM_B_SPACE));
    speeds[2] = y - x - (omega * (MECANUM_A_SPACE + MECANUM_B_SPACE));
    speeds[3] = y + x + (omega * (MECANUM_A_SPACE + MECANUM_B_SPACE));
  }

  /* Modifies a gamepad joystick value to map to the encoder positions of the lift motor. For faster lift, change k. */
  public int rangeScaler(double valToScale) {
    int scaleFactor = 2;
    return (int)(Math.round(scaleFactor * valToScale));
  }

  public void servoControl() {

    if(gamepad.yToggle1)
      blockServo.setPosition(0.13);
    else
      blockServo.setPosition(0.54);

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

  }

  public void armControl() {

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

  }

}
