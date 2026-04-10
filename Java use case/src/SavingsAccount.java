public class SavingsAccount extends Account {

    public SavingsAccount(int accNo, String owner) {
        super(accNo, owner);
    }

    @Override
    public void withdraw(double amount) {
        if (balance - amount >= 500)
            super.withdraw(amount);
        else
            System.out.println("Minimum balance of 500 required!");
    }
}