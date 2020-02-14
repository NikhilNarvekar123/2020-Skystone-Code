package org.firstinspires.ftc.teamcode.TeleOp;

interface TeleOpValues
{
  double SLIDE_COEFF = 1;
  double TURN_COEFF_NORM = 0.8;
  double TURN_COEFF_MIN = 0.5;
  double LINEAR_COEFF_MAX = 1.0;
  double LINEAR_COEFF_NORM = 0.8;
  double LINEAR_COEFF_MIN = 0.3;

  double LIFT_PWR = 0.3;
  double SLIDE_PWR = 0.8;
  double LIFT_MAINTAIN_POSITION = 200;
  double SLIDE_MAINTAIN_POSITION = 0;
  double LIFT_GRAB_POSITION = 0;
  double[] BLOCK_POSITION = {0, 904, 804, 704, 604};
  double[] SLIDE_POSITION = {0, 1500};

  double[] BLOCK_SERVO = {0.4, 0.17};
  double[] BLOCK_SERVO_CLAMP = {0.0, 0.4};
  double[] CAP_SERVO = {0.3, 1};
  double[] RIGHT_GRABBER = {0, 0.15};
  double[] LEFT_FOUND = {0, 1};
  double[] RIGHT_FOUND = {1, 0};
}
