public enum TeleOpStateMachine {

  BlockZero {
    public TeleOpStateMachine nextState() { return BlockOne; }
  }

  BlockOne {
    public TeleOpStateMachine nextState() { return BlockTwo; }
    public TeleOpStateMachine prevState() { return BlockOne; }
    public String action() {}
  },
  BlockTwo {
    public TeleOpStateMachine nextState() { return BlockThree; }
    public TeleOpStateMachine prevState() { return BlockOne; }
    public String action() {}
  },
  BlockThree {
    public TeleOpStateMachine nextState() { return BlockFour; }
    public TeleOpStateMachine prevState() { return BlockTwo; }
    public String action() {}
  },
  BlockFour {
    public TeleOpStateMachine nextState() { return BlockOne; }
    public TeleOpStateMachine prevState() { return BlockThree; }
    public String action() {}
  };

  public abstract TeleOpStateMachine nextState();
  public abstract String action() {};
  public abstract TeleOpStateMachine prevState();




}
