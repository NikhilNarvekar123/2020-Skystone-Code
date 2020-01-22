package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Hardware;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import java.io.*;
import java.util.*;

public class Telemetry {

  /* Queue used to promote FIFO access */
  Queue<String> msgStack;
  Queue<Object> dataStack;

  public Telemetry() {
    msgStack = new LinkedList<>();
    dataStack = new LinkedList<>();
  }

  /** Add Data onto Queue */
  public void log(String s, Object x) {
    msgStack.add(s);
    dataStack.add(x);
  }

  /** Display all saved data */
  public void display() {
    while(!msgStack.isEmpty())
      telemetry.addData(msgStack.remove(), dataStack.remove());
    telemetry.update();
  }

  




}
