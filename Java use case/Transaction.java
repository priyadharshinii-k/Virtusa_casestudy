public class Transaction {

    private int from;
    private int to;
    private double amount;
    private String type;

    public Transaction(int from, int to, double amount, String type) {
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.type = type;
    }

    @Override
    public String toString() {
        return type + "," + from + "," + to + "," + amount;
    }
}