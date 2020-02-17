package org.firstinspires.ftc.teamcode.TeleOp;

public enum ArmStateMachine {

  ZeroPosition {
    public ArmStateMachine nextState() { return GoBack; }
    public ArmStateMachine prevState() { return GoBack; }
    public String getLocationLabel() { return "Zero Position"; }
  }
  GoBack {
    public ArmStateMachine nextState() { return Pickup; }
    public ArmStateMachine prevState() { return Release; }
    public String getLocationLabel() { return "Maintaining"; }
  },
  Pickup {
    public ArmStateMachine nextState() { return Clamp; }
    public ArmStateMachine prevState() { return GoBack; }
    public String getLocationLabel() { return "Lowering for PickUp"; }
  },
  Clamp {
    public ArmStateMachine nextState() { return MoveToPlace; }
    public ArmStateMachine prevState() { return Pickup; }
    public String getLocationLabel() { return "Clamping Block"; }
  },
  MoveToPlace {
    public ArmStateMachine nextState() { return Release; }
    public ArmStateMachine prevState() { return Clamp; }
    public String getLocationLabel() { return "Moving to place"; }
  },
  Release {
    public ArmStateMachine nextState() { return GoBack; }
    public ArmStateMachine prevState() { return MoveToPlace; }
    public String getLocationLabel() { return "Dropping Block"; }
  };

  public abstract ArmStateMachine nextState();
  public abstract String getLocationLabel();
  public abstract ArmStateMachine prevState();


}
