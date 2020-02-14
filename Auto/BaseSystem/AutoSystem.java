package org.firstinspires.ftc.teamcode.Auto;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.robotcore.external.hardware.camera.Camera;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import com.qualcomm.robotcore.hardware.DcMotor;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import java.util.Locale;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import com.qualcomm.hardware.bosch.BNO055IMU;
import java.util.List;


public class AutoSystem extends LinearOpMode {

  /** Current Status: > Working on Ramping
    *                 > Working on solving motor-jerking problem
    *                 > Working on possible error-offshoot correction system
    */

  /* Components */
  DcMotor motorLF, motorLB, motorRF, motorRB;
  DcMotor rightIntake, leftIntake, lift;
  DcMotor[] motors = new DcMotor[4];
  Servo leftClamp, rightClamp, blockServo, blockServoClamp;
  Servo[] servos = new Servo[4];
  BNO055IMU imu;

  /* Constants */
  private static final double DRIVE_GEAR_TEETH = 26;
  private static final double OUTPUT_GEAR_TEETH = 15;
  private static final double GEAR_RATIO = OUTPUT_GEAR_TEETH/DRIVE_GEAR_TEETH;
  private static final double DIAMETER = 2.95276;
  private static final double COUNTS_PER_REV = 288;
  private static final double INCHES_PER_ROT = (COUNTS_PER_REV * GEAR_RATIO) / (DIAMETER * Math.PI);
  private static final double DEFAULT_GAIN_VALUE = 0.10;
  private static final double STANDARD_GAIN_VALUE = 0.06;
  private static final double TFOD_MINIMUM_CONFIDENCE = 0.84;
  private static final String TFOD_MODEL_ASSET = "Skystone.tflite";
  private static final String LABEL_FIRST_ELEMENT = "Stone";
  private static final String LABEL_SECOND_ELEMENT = "Skystone";
  private static final String VUFORIA_KEY = "ARuUo33/////AAABmVhNXTc0uk+8s0Rs9CraP7Bx/PuF9uszW3ygzElHauDx7Rr09w9ZA3sdHzC4U4tFpQ8jqpVqQPKwF9hzcRlNouxFJx+ZelnpTt1Ol9VYsSgDh/rpKNKN9VwNZ49UVMv3P8f1qbss86TidfH5CffA4tEsKkIezMBrt+/b2303ZH4xA0TDk4rcpa4bm8Sffj5c3mzmXdxSDRhmMiVA964KZNwu2ksmDR1BaQhz4BFfZM0OgCUEv0tzUEzsKDe+ebSIy8oHoS8EGGHFq+/Kto0iRON5o4UfwlyRtuEzObVKM+f6bFSpC8SfSPcEWrRgjyVUo1EVSmYCemuyq6P8mVEOqKHKkITKgNQSjyT/u7POOlRC";
  private static final int BLOCK_SERVO_INCREMENT = 15;

  /* IMU */
  Orientation lastAngles = new Orientation();
  double globalAngle, correction;
  BNO055IMU.Parameters parameters;

  /* Vuforia */
  private VuforiaLocalizer vuforia;
  private TFObjectDetector tfod;

  /* Instance Variables */
  boolean vuforiaOn;
  boolean rampDown = false;
  String fieldSide;
  String cameraType;
  int cyclePeriod;



  /* Constructs settings for Auto based on auto-type */
  public AutoSystem(String vuforiaStatus, String fieldSide, String cameraType) {

    if(vuforiaStatus.equals("VuforiaOn"))
      vuforiaOn = true;
    else
      vuforiaOn = false;

    this.fieldSide = fieldSide;
    this.cameraType = cameraType;

  }



  @Override
  public void runOpMode() throws InterruptedException {

    /* Component Set-Up */
    motorLF = hardwareMap.get(DcMotor.class,"motorLF"); motorLB = hardwareMap.get(DcMotor.class,"motorLB");
    motorRF = hardwareMap.get(DcMotor.class,"motorRF"); motorRB = hardwareMap.get(DcMotor.class,"motorRB");
    leftClamp = hardwareMap.get(Servo.class,"leftFound"); rightClamp = hardwareMap.get(Servo.class,"rightFound");
    blockServo = hardwareMap.get(Servo.class,"blockServo"); blockServoClamp = hardwareMap.get(Servo.class, "blockServoClamp");

    /* Motor Init */
    motors[0] = motorLF;
    motors[1] = motorLB;
    motors[2] = motorRF;
    motors[3] = motorRB;

    /* Servo Init */
    servos[0] = leftClamp;
    servos[1] = rightClamp;
    servos[2] = blockServo;
    servos[3] = blockServoClamp;
    blockServo.setPosition(0.54);

    /* IMU Init */
    imu = hardwareMap.get(BNO055IMU.class, "imu");
    imu.initialize(imuSetUp());
    telemetry.addLine("Gyro Ready...");

    /* Vuforia Init */
    if(vuforiaOn) {
      initVuforia();
      if (ClassFactory.getInstance().canCreateTFObjectDetector()) { initTfod(); }
      if (tfod != null) { tfod.activate(); telemetry.addLine("Vuforia Ready..."); }
    }

    telemetry.addLine("All Systems Ready...");
    telemetry.update();
    waitForStart();

  }



  /** The following methods allow the robot to move during autonomous.
   *  Currently, they implement gyro correction to stay on target.
   */


  /** Move Forward X inches, at Y speed, with an error-adjustment coefficent of Z */
  public void moveForward(double inches, double speed, double coeff) {

    resetMotors();

    // Create target based on bias and actual given value
    int target = (int)(Math.round(inches * INCHES_PER_ROT * coeff));

    // Set Target Position and run
    motors[0].setTargetPosition(motors[0].getCurrentPosition() - target); motors[1].setTargetPosition(motors[1].getCurrentPosition() - target);
    motors[2].setTargetPosition(motors[2].getCurrentPosition() + target); motors[3].setTargetPosition(motors[3].getCurrentPosition() + target);
    runMotors(speed);

    // Wait while moving
    while(opModeIsActive() && motors[0].isBusy() && motors[1].isBusy() && motors[2].isBusy() && motors[3].isBusy()) {

      double correction = checkDirection(STANDARD_GAIN_VALUE) * speed;

      // Run Motors
      motors[0].setPower(speed - correction); motors[1].setPower(speed - correction);
      motors[2].setPower(speed + correction); motors[3].setPower(speed + correction);

    }

    stopMotors();

  }

  /** Move Backward X inches, at Y speed, with an error-adjustment coefficient of Z */
  public void moveBackward(double inches, double speed, double coeff) {

    resetMotors();

    // Create target based on bias and actual given value
    int target = (int)(Math.round(inches * INCHES_PER_ROT * coeff));

    // Set Target Position and run
    motors[0].setTargetPosition(motors[0].getCurrentPosition() + target); motors[1].setTargetPosition(motors[1].getCurrentPosition() + target);
    motors[2].setTargetPosition(motors[2].getCurrentPosition() - target); motors[3].setTargetPosition(motors[3].getCurrentPosition() - target);
    runMotors(speed);

    // Wait while moving
    while(opModeIsActive() && motors[0].isBusy() && motors[1].isBusy() && motors[2].isBusy() && motors[3].isBusy()) {

      double correction = checkDirection(STANDARD_GAIN_VALUE) * speed;

      // Run Motors
      motors[0].setPower(speed + correction); motors[1].setPower(speed + correction);
      motors[2].setPower(speed - correction); motors[3].setPower(speed - correction);

    }

    stopMotors();

  }

  /** Move Left X inches, at Y speed, with an error-adjustment coefficient of Z */
  public void strafeLeft(double inches, double speed, double coeff) {

    resetMotors();

    // Create target based on bias and actual given value
    int target = (int)(Math.round(inches * INCHES_PER_ROT * coeff));

    // Set Target Position and run
    motors[0].setTargetPosition(motors[0].getCurrentPosition() + target); motors[1].setTargetPosition(motors[1].getCurrentPosition() - target);
    motors[2].setTargetPosition(motors[2].getCurrentPosition() + target); motors[3].setTargetPosition(motors[3].getCurrentPosition() - target);
    runMotors(speed);

    // Wait while moving
    while(opModeIsActive() && motors[0].isBusy() && motors[1].isBusy() && motors[2].isBusy() && motors[3].isBusy()) {

      double correction = checkDirection(DEFAULT_GAIN_VALUE) * speed;

      // Run Motors
      motors[0].setPower(speed + correction); motors[1].setPower(speed - correction);
      motors[2].setPower(speed + correction); motors[3].setPower(speed - correction);

    }

    stopMotors();

  }

  /** Move Right X inches, at Y speed, with an error-adjustment coefficient of Z */
  public void strafeRight(double inches, double speed, double coeff) {

    resetMotors();

    // Create target based on bias and actual given value
    int target = (int)(Math.round(inches * INCHES_PER_ROT * coeff));

    // Set Target Position and run
    motors[0].setTargetPosition(motors[0].getCurrentPosition() - target); motors[1].setTargetPosition(motors[1].getCurrentPosition() + target);
    motors[2].setTargetPosition(motors[2].getCurrentPosition() - target); motors[3].setTargetPosition(motors[3].getCurrentPosition() + target);
    runMotors(speed);

    // Wait while moving
    while(opModeIsActive() && motors[0].isBusy() && motors[1].isBusy() && motors[2].isBusy() && motors[3].isBusy()) {

      double correction = checkDirection(STANDARD_GAIN_VALUE) * speed;

      // Run Motors
      motors[0].setPower(speed - correction); motors[1].setPower(speed + correction);
      motors[2].setPower(speed - correction); motors[3].setPower(speed + correction);

    }

    stopMotors();

  }

  /* Resets motors for accuracy */
  public void resetMotors() {

    // Reset for Accuracy
    motors[0].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER); motors[1].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    motors[2].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER); motors[3].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    sleep(50);
  }
  /* Runs motors to target position */
  public void runMotors(double speed) {

    motors[0].setPower(speed); motors[1].setPower(speed);
    motors[2].setPower(speed); motors[3].setPower(speed);
    motors[0].setMode(DcMotor.RunMode.RUN_TO_POSITION); motors[1].setMode(DcMotor.RunMode.RUN_TO_POSITION);
    motors[2].setMode(DcMotor.RunMode.RUN_TO_POSITION); motors[3].setMode(DcMotor.RunMode.RUN_TO_POSITION);

  }
  /* Stops All Motors */
  public void stopMotors() {

    motors[0].setPower(0); motors[1].setPower(0);
    motors[2].setPower(0); motors[3].setPower(0);

  }
  /* Turns off run-to-position mode */
  public void motorModeChange() {

    motors[0].setMode(DcMotor.RunMode.RUN_USING_ENCODER); motors[1].setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    motors[2].setMode(DcMotor.RunMode.RUN_USING_ENCODER); motors[3].setMode(DcMotor.RunMode.RUN_USING_ENCODER);

  }



  /** The following methods allow the REV Hub IMU to return correction-angles for
   *  movement methods.
   */


  /* Rotates the robot to X degrees, at Y power | CW (⭮) if degrees is negative */
  public void rotate(int degrees, double power) {

    double leftPower, rightPower;

    lastAngles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
    globalAngle = 0;

    if (degrees < 0)
    {
      leftPower = -power;
      rightPower = -power;
    }
    else if (degrees > 0)
    {
      leftPower = power;
      rightPower = power;
    }
    else return;

    motors[0].setPower(leftPower);
    motors[1].setPower(leftPower);
    motors[2].setPower(rightPower);
    motors[3].setPower(rightPower);

    if (degrees < 0) //right turn
    {
      while (opModeIsActive() && getAngle() == 0) {}
      while (opModeIsActive() && getAngle() > degrees) {}
    }
    else    //left turn
      while (opModeIsActive() && getAngle() < degrees) {}

    stopMotors();

    // wait for rotation to stop.
    sleep(100);

    // reset angle tracking on new heading.
    lastAngles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
    globalAngle = 0;

  }

  /* Sets Up IMU */
  public BNO055IMU.Parameters imuSetUp() {

    parameters = new BNO055IMU.Parameters();
    parameters.mode = BNO055IMU.SensorMode.IMU;
    parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
    parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
    parameters.loggingEnabled = false;
    return parameters;

  }
  /* Returns current angle */
  public double getAngle() {

    Orientation angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

    double deltaAngle = angles.firstAngle - lastAngles.firstAngle;

    if (deltaAngle < -180)
      deltaAngle += 360;
    else if (deltaAngle > 180)
      deltaAngle -= 360;

    globalAngle += deltaAngle;

    lastAngles = angles;

    return globalAngle;

  }
  /* Returns correction value */
  private double checkDirection(double gainVal) {

    double correction, angle, gain = gainVal;
    angle = getAngle();

    if (angle == 0)
      correction = 0;
    else
      correction = -angle;

    correction = correction * gain;
    return correction;

  }



  /** Extra Component Methods, about other mechanisms
   *  on the robot.
   */


  /* Changes the position of the clamp-block-servo as a function of the
   * position of the block-servo.
   */
  public void clampBlock() {

    // All Servo Positions (CHANGE AS NEEDED)
    double xInitial = 0.54;
    double xFinal = 0.15;
    double yInitial = 0.0;
    double yFinal = 0.3;
    double xMedian = 0.35;

    // Calculated Raise Values
    double raiseSlope = ((yInitial - yFinal) / (xInitial - xFinal));
    double raiseIntercept = yInitial - (raiseSlope * xInitial);

    // Calculated Lower Values
    double lowerSlope = ((yInitial - yFinal) / (xMedian - xFinal));
    double lowerIntercept = yFinal - (lowerSlope * xFinal);

    // Variables to keep track of X/Y for servos
    double blockServoPos = xInitial;
    double blockServoClampPos = yInitial;

    // Raise Loop (as in grabbing block )
    while(opModeIsActive() && (blockServoPos != xFinal || blockServoClampPos != yFinal)) {

      // Decrement X-Variable but keep in appropriate range
      blockServoPos -= (xInitial - xFinal) / 10;
      blockServoPos = Range.clip(blockServoPos, xFinal, xInitial);

      // Change Y-Variable based on X-Variable (Eq: y = mx + b)
      blockServoClampPos = Range.clip((raiseSlope * blockServoPos) + raiseIntercept, yInitial, yFinal);

      // Pass X and Y variables to servos
      servos[2].setPosition(blockServoPos);
      servos[3].setPosition(blockServoClampPos);
      sleep(BLOCK_SERVO_INCREMENT);

    }
    sleep(300);

    // Lower Loop (as in lifting block)
    while(opModeIsActive() && (blockServoPos != xMedian && blockServoClampPos != yInitial)) {

      // Increment X-Variable but keep in appropriate range
      blockServoPos += (xMedian - xFinal) / 2;
      blockServoPos = Range.clip(blockServoPos, xFinal, xMedian);

      // Change Y-Variable based on X-Variable (Eq: y = mx + b)
      blockServoClampPos = Range.clip((lowerSlope * blockServoPos) + lowerIntercept, yInitial, yFinal);

      // Pass X and Y Variables to servos
      servos[2].setPosition(blockServoPos);
      servos[3].setPosition(blockServoClampPos);
      sleep(BLOCK_SERVO_INCREMENT);

    }

  }
  /* Releases Block */
  public void releaseBlock() {
    servos[3].setPosition(0.3);
  }
  /* Grabs Foundation */
  public void clampFoundation() {
    leftClamp.setPosition(0.7);
    rightClamp.setPosition(0.63);
  }
  /* Releases Foundation */
  public void releaseFoundation() {
    leftClamp.setPosition(0.34);
    rightClamp.setPosition(1);
  }



  /** Vuforia Methods to return the position of the skystone. The robot
   *  can then make decisions based on this data.
   */


  /* Scans the view from camera/webcam for skystone location */
  private int ScanStone() {

   if (tfod != null) {
     List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
     if (updatedRecognitions != null) {
       for (Recognition recognition : updatedRecognitions) {
         if(recognition.getLabel() == "Skystone") {
           if(0.5 * (recognition.getLeft() + recognition.getRight()) < 250)
             return 2; //Center
           else
             return 1; //Right for red, left for blue
         }
       }
       return 3;
     }
   }

   return 0;

  }

  /* Returns the position of the skystone */
  public String scanner() throws InterruptedException {

   String foundLocation = "";
   int stoneValue = 0;

   while (opModeIsActive()) {

     stoneValue = ScanStone();
     telemetry.addData("Skystone", stoneValue);

     if(stoneValue == 3) // Relative Right
     {
       if(fieldSide.equals("red"))
         foundLocation = "RED_LEFT";
       else
         foundLocation = "BLUE_RIGHT";
     }
     else if(stoneValue == 2)  // Absolute Center
     {
       if(fieldSide.equals("red"))
         foundLocation = "RED_CENTER";
       else
         foundLocation = "BLUE_LEFT";
     }
     else if (stoneValue == 1) // Relative Left
     {
       if(fieldSide.equals("red"))
         foundLocation = "RED_RIGHT";
       else
         foundLocation = "BLUE_CENTER";
     }
     else // Nothing found
     {
       if(fieldSide.equals("red"))
         foundLocation = "RED_CENTER";
       else
         foundLocation = "BLUE_CENTER";
     }

     break;
   }

   if(tfod != null) {
     tfod.shutdown();
     //tfod = null; TEST???
   }

   return foundLocation;

  }

  /* Initializes Vuforia */
  private void initVuforia() {

    VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();
    parameters.vuforiaLicenseKey = VUFORIA_KEY;

    /* Toggle Between Webcam and Camera */
    if(cameraType.equals("phone"))
      parameters.cameraDirection = CameraDirection.BACK;
    else
      parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");

    vuforia = ClassFactory.getInstance().createVuforia(parameters);

  }
  /* Initializes TensorFlow */
  private void initTfod() {

    int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
    TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
    tfodParameters.minimumConfidence = TFOD_MINIMUM_CONFIDENCE;
    tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
    tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_FIRST_ELEMENT, LABEL_SECOND_ELEMENT);

  }
  /* Explicitly shuts down Vuforia */
  public void TFODShutDown() {

    if(tfod != null)
      tfod.shutdown();

  }




}
