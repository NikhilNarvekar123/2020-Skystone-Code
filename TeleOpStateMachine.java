public enum TeleOpStateMachine {

  BlockOne {
    public TeleOpStateMachine nextState() { return BlockTwo; }
    public String action() {}
  },
  BlockTwo {
    public TeleOpStateMachine nextState() { return BlockThree; }
    public String action() {}
  },
  BlockThree {
    public TeleOpStateMachine nextState() { return BlockFour; }
    public String action() {}
  },
  BlockFour {
    public TeleOpStateMachine nextState() { return this; } //or for infinite loop do BlockOne
    public String action() {}
  };

  public abstract TeleOpStateMachine nextState();
  public abstract String action() {};




}
