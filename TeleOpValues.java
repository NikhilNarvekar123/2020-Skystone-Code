package org.firstinspires.ftc.teamcode.teleop;

interface TeleOpValues
{
  double SLIDE_COEFF = 1;
  double TURN_COEFF_NORM = 0.8;
  double TURN_COEFF_MIN = 0.5;
  double LINEAR_COEFF_MAX = 1.0;
  double LINEAR_COEFF_NORM = 0.8;
  double LINEAR_COEFF_MIN = 0.3;

  double LIFT_PWR = 0.3;
  double LIFT_MAINTAIN_POSITION = 200;
  double LIFT_GRAB_POSITION = 0;
  double[] BLOCK_POSITION = [0, 904, 804, 704, 604];

  double[] BLOCK_SERVO = {0.54, 0.13};
  double[] CAP_SERVO = {0.3, 1};
  double[] LEFT_GRABBER = {0.5, 0.36};
  double[] RIGHT_GRABBER = {0.67, 0.8};
  double[] LEFT_FOUND = {0.35, 0.7};
  double[] RIGHT_FOUND = {0.65, 0.24};
}
