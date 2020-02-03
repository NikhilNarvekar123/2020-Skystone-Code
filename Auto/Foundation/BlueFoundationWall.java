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

@Autonomous(group="Foundation", name="BlueFoundationWall")

public class BlueFoundationWall extends AutoSystem implements AutoValues {


  public BlueFoundationWall()
  {
    super("Off","Blue","Off");
  }

  public void runOpMode() throws InterruptedException {

    super.runOpMode();

    /* Main Loop */
    while(opModeIsActive()) {

      // Strafe to foundation center
      strafeRight(9, SIDEWAYS_SPEED_MAX, 1);
      sleep(DEFAULT_METHOD_DELAY);

      // Approach foundation
      moveBackward(30,LINEAR_SPEED_MAX, 1);
      sleep(DEFAULT_METHOD_DELAY);

      // Grab foundation
      leftClamp.setPosition(0.7);
      rightClamp.setPosition(0.24);
      sleep(DEFAULT_SERVO_DELAY);

      // Move foundation into building zone
      moveForward(36, LINEAR_SPEED_MAX, 0.865);

      // Release foundation
      leftClamp.setPosition(0.35);
      rightClamp.setPosition(0.65);
      sleep(DEFAULT_SERVO_DELAY);

      // Give robot space to strafe
      moveBackward(2, LINEAR_SPEED_NORM, 1);
      sleep(DEFAULT_METHOD_DELAY);
      moveForward(1, LINEAR_SPEED_MIN, 1);
      sleep(DEFAULT_METHOD_DELAY);

      // Strafe out from foundation
      strafeLeft(26, SIDEWAYS_SPEED_MAX, 1);
      sleep(DEFAULT_METHOD_DELAY);

      // Strafe towards bridge
      strafeLeft(17, SIDEWAYS_SPEED_MAX, 1.071);
      sleep(DEFAULT_METHOD_DELAY);

      // Park
      rotate((int)(-getAngle()), DEFAULT_ROTATE_SPEED);
      sleep(DEFAULT_METHOD_DELAY);

      break;

    }
  }

}
