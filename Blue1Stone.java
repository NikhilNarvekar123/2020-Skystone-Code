package org.firstinspires.ftc.teamcode.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import java.util.Locale;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.hardware.bosch.BNO055IMU;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;

@Autonomous(group="Blue", name="BlueStone")

public class BlueStone extends AutoSystem implements AutoValues {

  public BlueStone()
  {
    super("VuforiaOn","Blue","WebCam");
  }

  public void runOpMode() throws InterruptedException {

    super.runOpMode();

    /* Main Loop */
    while(opModeIsActive()) {

      strafeRight(15, SIDEWAYS_SPEED_NORM, 1);
      sleep(DEFAULT_METHOD_DELAY);

      String stoneLocation = scanner();
      sleep(750);

      telemetry.addLine(stoneLocation);
      telemetry.update();

      int deltaPos = 0;

      //nikhil cant code...also i love nikhil - from sriman t
      if(stoneLocation.equals("BLUE_LEFT")) {
        deltaPos = -7;
      } else if(stoneLocation.equals("BLUE_CENTER")) {
        moveBackward(7, LINEAR_SPEED_NORM, 1);
        deltaPos = 2;
      } else {
        moveBackward(14, LINEAR_SPEED_NORM, 1);
        deltaPos = 4;
      }

      TFODShutDown();

      strafeRight(20, SIDEWAYS_SPEED_MAX, 1);
      sleep(DEFAULT_METHOD_DELAY);

      clampBlock();
      sleep(DEFAULT_SERVO_DELAY);

      strafeLeft(7, SIDEWAYS_SPEED_MAX, 2);
      sleep(DEFAULT_METHOD_DELAY);

      moveForward(47 + deltaPos, LINEAR_SPEED_MAX, 1);
      sleep(DEFAULT_METHOD_DELAY);

      releaseBlock();
      sleep(DEFAULT_SERVO_DELAY);

      moveBackward(12 + deltaPos, LINEAR_SPEED_MAX, 1);
      sleep(DEFAULT_METHOD_DELAY);

      break;

    }
  }

}
