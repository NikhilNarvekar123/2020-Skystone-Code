public enum TeleOpStateMachine {

  BlockZero {
    public TeleOpStateMachine nextState() { return BlockOne; }
    public TeleOpStateMachine prevState() { return BlockOne; }
    public int getPosition() { return 0; }
    public String getPositionLabel() { return "Null Position"; }
  },
  BlockOne {
    public TeleOpStateMachine nextState() { return BlockTwo; }
    public TeleOpStateMachine prevState() { return BlockOne; }
    public int getPosition() { return 850; }
    public String getPositionLabel() { return "Placing Block One"; }
  },
  BlockTwo {
    public TeleOpStateMachine nextState() { return BlockThree; }
    public TeleOpStateMachine prevState() { return BlockOne; }
    public int getPosition() { return 650; }
    public String getPositionLabel() { return "Placing Block Two"; }
  },
  BlockThree {
    public TeleOpStateMachine nextState() { return BlockFour; }
    public TeleOpStateMachine prevState() { return BlockTwo; }
    public int getPosition() { return 450; }
    public String getPositionLabel() { return "Placing Block Three"; }
  },
  BlockFour {
    public TeleOpStateMachine nextState() { return BlockOne; }
    public TeleOpStateMachine prevState() { return BlockThree; }
    public int getPosition() { return 350; }
    public String getPositionLabel() { return "Placing Block Four"; }
  };

  public abstract TeleOpStateMachine nextState();
  public abstract int getPosition();
  public abstract String getPositionLabel();
  public abstract TeleOpStateMachine prevState();




}
