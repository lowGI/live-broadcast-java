package entity;

public class Donation extends Comment{
    private int amount;

    public Donation(){
        super("",0,false);
    }

    public Donation(String username, long sendTime, boolean isAdmin, int amount){
        super(username, sendTime, isAdmin);
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public String toString(){
        return "✨✨✨ " + getUsername() + " has donated " + amount + " coin(s)!" + " ✨✨✨" + "\n";
    }
}
