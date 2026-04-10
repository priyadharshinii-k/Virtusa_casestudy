public class CurrentAccount extends Account {

    public CurrentAccount(int accNo, String owner) {
        super(accNo, owner);
    }

    @Override
    public void withdraw(double amount) {
        if (balance - amount >= -1000)
            balance -= amount;
        else
            System.out.println("Overdraft limit exceeded!");
    }
}