
public class PIDController {

  private double P;
  private double I;
  private double D;
  private double Kp, Ki, Kd;
  private double pwr;
  private double input;
  private double error;
  private double errorSum;

  public PIDController(double input, double target){
    this.input = input;
    pwr = 0;

  }

  public void startPID() {

  }

  public void calculateP() {
    error = target - input;
    pwr += Kp * error;
  }

  public void calculateI() {
    error = target - input;
    errorSum += error * ;

    pwr += Kp * error;
  }

  public void calculateD() {
    error = target - input;

  }

}
