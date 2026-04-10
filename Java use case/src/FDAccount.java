public class FDAccount extends Account {

    private int duration;
    private double interestRate;

    public FDAccount(int accNo, String owner, double amount, int duration) {
        super(accNo, owner);
        this.balance = amount;
        this.duration = duration;

        if (duration >= 5)
            interestRate = 7.5;
        else
            interestRate = 5.0;

        calculateMaturity();
    }

    private void calculateMaturity() {
        double interest = (balance * interestRate * duration) / 100;
        balance += interest;
    }

    @Override
    public void deposit(double amount) {
        System.out.println("Cannot deposit in FD account!");
    }

    @Override
    public void withdraw(double amount) {
        System.out.println("Cannot withdraw before maturity!");
    }
}