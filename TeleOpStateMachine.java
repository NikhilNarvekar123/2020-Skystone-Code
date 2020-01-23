public enum TeleOpStateMachine {

  BlockZero {
    public TeleOpStateMachine nextState() { return BlockOne; }
  }

  BlockOne {
    public TeleOpStateMachine nextState() { return BlockTwo; }
    public TeleOpStateMachine prevState() { return BlockOne; }
    public int getPosition() { return 850; }
  },
  BlockTwo {
    public TeleOpStateMachine nextState() { return BlockThree; }
    public TeleOpStateMachine prevState() { return BlockOne; }
    public int getPosition() { return 650; }
  },
  BlockThree {
    public TeleOpStateMachine nextState() { return BlockFour; }
    public TeleOpStateMachine prevState() { return BlockTwo; }
    public int getPosition() { return 450; }
  },
  BlockFour {
    public TeleOpStateMachine nextState() { return BlockOne; }
    public TeleOpStateMachine prevState() { return BlockThree; }
    public int getPosition() { return 350; }
  };

  public abstract TeleOpStateMachine nextState();
  public abstract String getPosition() {};
  public abstract TeleOpStateMachine prevState();




}
