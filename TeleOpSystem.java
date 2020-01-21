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

  /* Components */
  DcMotor motorLF, motorLB, motorRF, motorRB;
  DcMotor lift, leftIntake, rightIntake;
  Servo blockServo;
  Servo leftFound, rightFound;
  Servo leftGrabber, rightGrabber, capServo;

  Controller gamepad;
  TeleOpStateMachine state = TeleOpStateMachine.BlockOne;

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

  public void setState() {
    
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

}
