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

@Autonomous(group="Red",name="RedStone1")

public class RedStone1 extends AutoSystem implements AutoValues {

  public RedStone1()
  {
    super("VuforiaOn","red","WebCam");
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

      if(stoneLocation.equals("RED_RIGHT")) {
        moveBackward(7, LINEAR_SPEED_NORM, 1);
        deltaPos = -7;
      } else if(stoneLocation.equals("RED_CENTER")) {
        moveForward(1, LINEAR_SPEED_NORM, 1);
        deltaPos = 2;
      } else {
        moveForward(7, LINEAR_SPEED_NORM, 1);
        deltaPos = 7;
      }
      sleep(DEFAULT_METHOD_DELAY);

      TFODShutDown();

      strafeRight(22, SIDEWAYS_SPEED_MAX, 1);
      sleep(DEFAULT_METHOD_DELAY);

      clampBlock();
      sleep(DEFAULT_SERVO_DELAY);

      strafeLeft(6, SIDEWAYS_SPEED_MAX, 2);
      sleep(DEFAULT_METHOD_DELAY);

      moveBackward(45 + deltaPos, LINEAR_SPEED_MAX, 1);
      sleep(DEFAULT_METHOD_DELAY);

      releaseBlock();
      sleep(DEFAULT_SERVO_DELAY);

      moveForward(12 + deltaPos, LINEAR_SPEED_MAX, 1);
      sleep(DEFAULT_METHOD_DELAY);

      break;
    }
  }

}
