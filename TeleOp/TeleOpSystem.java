package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.robotcore.external.State;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Hardware;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

@Disabled
public class TeleOpSystem extends LinearOpMode implements TeleOpValues {

  /* Vars */
  double DRIVE_COEFF = LINEAR_COEFF_NORM;
  double TURN_COEFF = TURN_COEFF_NORM;
  double dy, dx, turn = 0.0;
  double liftPos = 0.0;

  /* Components */
  DcMotor motorLF, motorLB, motorRF, motorRB;
  DcMotor lift, leftIntake, rightIntake, slideMotor;
  Servo blockServo, blockServoClamp;
  Servo leftFound, rightFound;
  Servo leftGrabber, rightGrabber, capServo;

  Controller gamepad;
  TeleOpStateMachine currentState;

  /* Main Execution Loop */
  public void runOpMode() throws InterruptedException {

    /* Component SetUp */
    motorLF = hardwareMap.get(DcMotor.class,"motorLF");
    motorLB = hardwareMap.get(DcMotor.class,"motorLB");
    motorRF = hardwareMap.get(DcMotor.class,"motorRF");
    motorRB = hardwareMap.get(DcMotor.class,"motorRB");
    lift = hardwareMap.get(DcMotor.class,"lift");
    slideMotor = hardwareMap.get(DcMotor.class, "slideMotor");
    leftFound = hardwareMap.get(Servo.class,"leftFound");
    rightFound = hardwareMap.get(Servo.class,"rightFound");
    blockServo = hardwareMap.get(Servo.class,"blockServo");
    blockServoClamp = hardwareMap.get(Servo.class, "blockServoClamp");
    leftIntake = hardwareMap.get(DcMotor.class,"leftIntake");
    rightIntake = hardwareMap.get(DcMotor.class,"rightIntake");
    leftGrabber = hardwareMap.get(Servo.class,"leftGrabber");
    rightGrabber = hardwareMap.get(Servo.class,"rightGrabber");
    capServo = hardwareMap.get(Servo.class, "capServo");

    /* Init Code */
    lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    lift.setDirection(DcMotor.Direction.REVERSE);
    blockServo.setPosition(0.54);


    motorLF.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    motorLB.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    motorRF.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    motorRB.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

    gamepad = new Controller(gamepad1, gamepad2);
    currentState = TeleOpStateMachine.BlockZero;

    t("All Systems Ready...");
    waitForStart();

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

  /* Modifies a gamepad joystick value to map to the encoder positions of the lift motor. For faster lift, change k. */
  public int rangeScaler(double valToScale) {
    int scaleFactor = 2;
    return (int)(Math.round(scaleFactor * valToScale));
  }

  /** Moves lift to a given encoder position at a given power */
  public void activateLift(double position, double pwr) {
    lift.setTargetPosition((int)(Math.round(position)));
    lift.setPower(pwr);
    lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
  }

  /** Controls all on-robot servos */
  public void servoControl() {

    if(gamepad.yToggle1)
      blockServo.setPosition(BLOCK_SERVO[1]);
    else
      blockServo.setPosition(BLOCK_SERVO[0]);

    if(gamepad2.y)
      capServo.setPosition(CAP_SERVO[1]);
    else
      capServo.setPosition(CAP_SERVO[0]);

    if(gamepad.dpadDownToggle1 || gamepad.dpadDownToggle2) {
      leftFound.setPosition(LEFT_FOUND[1]);
      rightFound.setPosition(RIGHT_FOUND[1]);
    } else {
      leftFound.setPosition(LEFT_FOUND[0]);
      rightFound.setPosition(RIGHT_FOUND[0]);
    }

  }

  /** Control block grabbers during auto and manual threads */
  public void controlBlock(boolean close, boolean open) {

    if(open) {
      leftGrabber.setPosition(LEFT_GRABBER[0]);
      rightGrabber.setPosition(RIGHT_GRABBER[0]);
    }
    else if(close) {
      leftGrabber.setPosition(LEFT_GRABBER[1]);
      rightGrabber.setPosition(RIGHT_GRABBER[1]);
    }

  }

  /** Runs the lift with a state-machine (automated process) */
  public void armControlAutomatic(boolean nextState, boolean reverseState, boolean restartMachine) throws InterruptedException {

    if(restartMachine) {
      currentState = TeleOpStateMachine.BlockOne;
      t("Restarting Machine (Block-One)");
    } else if(reverseState) {
      currentState = currentState.prevState();
      t("Reversing Machine " + currentState.getPositionLabel());
    } else if(nextState) {
      currentState = currentState.nextState();
      t("Advancing Machine " + currentState.getPositionLabel());
    }

    gamepad.yToggle2 = false;

    // Go Down for Pickup
    activateLift(LIFT_GRAB_POSITION, LIFT_PWR);
    while((opModeIsActive() && lift.isBusy()) || !gamepad.yToggle2) { t("lowering"); idle(); }
    gamepad.yToggle2 = false;

    // Grab Block
    controlBlock(true, false);
    while(opModeIsActive() && !gamepad.yToggle2) { t("grabbing block"); idle(); }
    //while(opModeIsActive() && (leftGrabber.getPosition() != 0.36)  && !yToggle) { idle(); }
    gamepad.yToggle2 = false;

    // Place Position
    activateLift(currentState.getPosition(), LIFT_PWR);
    while((opModeIsActive() && lift.isBusy()) || !gamepad.yToggle2) { t("placing"); idle(); }

    gamepad.yToggle2 = false;

    // Release Block
    controlBlock(false, true);
    while(opModeIsActive()&& !gamepad.yToggle2) { t("releasing block"); idle(); }

    // Go to Median Position
    activateLift(LIFT_MAINTAIN_POSITION, LIFT_PWR);
    while(opModeIsActive() && lift.isBusy()) { idle(); }

    gamepad.yToggle2 = false;
    t("State Done");
  }

  /** Runs the lift manually (using gamepad-triggers) */
  public void armControlManual() {

    double upValue = gamepad2.right_trigger;
    double downValue = gamepad2.left_trigger;
    boolean maintain = gamepad2.dpad_up;

    if(upValue > 0) {
      if(liftPos + rangeScaler(upValue) < BLOCK_POSITION[1])
        liftPos += rangeScaler(upValue);
      else
        liftPos = BLOCK_POSITION[1];
    }

    if(downValue > 0) {
      if(liftPos - rangeScaler(downValue) > BLOCK_POSITION[0])
        liftPos -= rangeScaler(downValue);
      else
        liftPos = BLOCK_POSITION[0];
    }

    int targetPosition = (int)(Math.round(liftPos));
    if(!maintain)
      activateLift(targetPosition, LIFT_PWR);
    else
      liftPos = LIFT_MAINTAIN_POSITION;

    t(Double.toString(liftPos));
  }

  /** Telemetry-call consolidated into one method */
  public void t(String data) {
    telemetry.addData("", data);
    telemetry.update();
  }

  /** Stacked telemetry-call (more than one data piece to be pushed at once) in overloaded method */
  public void t(String[] data) {
    for(int i = 0; i < data.length; i++)
      telemetry.addData("", data[i]);
    telemetry.update();
  }
}
