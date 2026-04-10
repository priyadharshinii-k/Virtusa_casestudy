import java.io.*;
import java.util.*;

public class Bank {

    private List<User> users = new ArrayList<>();
    private List<Account> accounts = new ArrayList<>();
    private List<Transaction> transactions = new ArrayList<>();

    private User loggedInUser;
    private Scanner sc = new Scanner(System.in);

    public Bank() {
        loadUsers();
        loadAccounts();
    }

    public void start() {
        while (true) {
            System.out.println("\n=== BANK SYSTEM ===");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Enter your choice:");

            int choice = sc.nextInt();

            switch (choice) {
                case 1 -> register();
                case 2 -> login();
                case 3 -> System.exit(0);
            }
        }
    }

    private void register() {
        sc.nextLine();
        System.out.print("Username: ");
        String username = sc.nextLine();

        System.out.print("Password: ");
        String password = sc.nextLine();

        users.add(new User(username, password));
        saveUsers();

        System.out.println("Registered successfully!");
    }

    private void login() {
        sc.nextLine();
        System.out.print("Username: ");
        String username = sc.nextLine();

        System.out.print("Password: ");
        String password = sc.nextLine();

        for (User u : users) {
            if (u.getUsername().equals(username) &&
                u.getPassword().equals(password)) {

                loggedInUser = u;
                menu();
                return;
            }
        }
        System.out.println("Invalid login!");
    }

    private void menu() {
        while (true) {
            System.out.println("\n--- MENU ---");
            System.out.println("1. Create Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Balance");
            System.out.println("6. Transactions");
            System.out.println("7. Logout");

            System.out.print("Enter your choice:");
            int ch = sc.nextInt();

            switch (ch) {
                case 1 -> createAccount();
                case 2 -> deposit();
                case 3 -> withdraw();
                case 4 -> transfer();
                case 5 -> balance();
                case 6 -> showTransactions();
                case 7 -> { return; }
            }
        }
    }

    private void createAccount() {
        System.out.println("1. Savings  2. Current  3. FD");
        int type = sc.nextInt();

        int accNo = new Random().nextInt(900000) + 100000;
        Account acc;

        if (type == 1)
            acc = new SavingsAccount(accNo, loggedInUser.getUsername());
        else if (type == 2)
            acc = new CurrentAccount(accNo, loggedInUser.getUsername());
        else if (type == 3) {
            System.out.print("Amount: ");
            double amt = sc.nextDouble();

            System.out.print("Years: ");
            int years = sc.nextInt();

            acc = new FDAccount(accNo, loggedInUser.getUsername(), amt, years);
        } else {
            System.out.println("Invalid");
            return;
        }

        accounts.add(acc);
        saveAccounts();
        System.out.println("Account created: " + accNo);
    }

    private Account findAccount(int accNo) {
        for (Account a : accounts)
            if (a.getAccountNumber() == accNo)
                return a;
        return null;
    }

    private void deposit() {
        System.out.print("Acc No: ");
        int accNo = sc.nextInt();

        System.out.print("Amount: ");
        double amt = sc.nextDouble();

        Account acc = findAccount(accNo);

        if (acc != null) {
            acc.deposit(amt);
            transactions.add(new Transaction(accNo, accNo, amt, "DEPOSIT"));
            saveTransactions();
            saveAccounts();
        }
    }

    private void withdraw() {
        System.out.print("Acc No: ");
        int accNo = sc.nextInt();

        System.out.print("Amount: ");
        double amt = sc.nextDouble();

        Account acc = findAccount(accNo);

        if (acc != null) {
            acc.withdraw(amt);
            transactions.add(new Transaction(accNo, accNo, amt, "WITHDRAW"));
            saveTransactions();
            saveAccounts();
        }
    }

    private void transfer() {
        System.out.print("From: ");
        int from = sc.nextInt();

        System.out.print("To: ");
        int to = sc.nextInt();

        System.out.print("Amount: ");
        double amt = sc.nextDouble();

        Account a1 = findAccount(from);
        Account a2 = findAccount(to);

        if (a1 != null && a2 != null && a1.getBalance() >= amt) {
            a1.withdraw(amt);
            a2.deposit(amt);

            transactions.add(new Transaction(from, to, amt, "TRANSFER"));
            saveTransactions();
            saveAccounts();

            System.out.println("Transfer success");
        } else {
            System.out.println("Transfer failed");
        }
    }

    private void balance() {
        System.out.print("Acc No: ");
        int accNo = sc.nextInt();

        Account acc = findAccount(accNo);

        if (acc != null) {
            System.out.println("Balance: " + acc.getBalance());

            if (acc instanceof FDAccount)
                System.out.println("FD Account");
        }
    }

    private void showTransactions() {
        try (Scanner file = new Scanner(new File("transactions.txt"))) {
            while (file.hasNextLine())
                System.out.println(file.nextLine());
        } catch (Exception e) {
            System.out.println("No transactions found.");
        }
    }

    // ================= FILE HANDLING =================

    private void saveUsers() {
        try (PrintWriter pw = new PrintWriter("users.txt")) {
            for (User u : users)
                pw.println(u.getUsername() + "," + u.getPassword());
        } catch (Exception e) {}
    }

    private void loadUsers() {
        try (Scanner file = new Scanner(new File("users.txt"))) {
            while (file.hasNextLine()) {
                String[] data = file.nextLine().split(",");
                users.add(new User(data[0], data[1]));
            }
        } catch (Exception e) {}
    }

    private void saveAccounts() {
        try (PrintWriter pw = new PrintWriter("accounts.txt")) {
            for (Account acc : accounts) {
                String type =
                    (acc instanceof SavingsAccount) ? "SAVINGS" :
                    (acc instanceof CurrentAccount) ? "CURRENT" : "FD";

                pw.println(acc.getAccountNumber() + "," +
                           acc.owner + "," +
                           acc.getBalance() + "," +
                           type);
            }
        } catch (Exception e) {}
    }

    private void loadAccounts() {
        try (Scanner file = new Scanner(new File("accounts.txt"))) {
            while (file.hasNextLine()) {
                String[] d = file.nextLine().split(",");

                int accNo = Integer.parseInt(d[0]);
                String owner = d[1];
                double bal = Double.parseDouble(d[2]);
                String type = d[3];

                Account acc;

                if (type.equals("SAVINGS"))
                    acc = new SavingsAccount(accNo, owner);
                else if (type.equals("CURRENT"))
                    acc = new CurrentAccount(accNo, owner);
                else
                    acc = new FDAccount(accNo, owner, bal, 1);

                acc.balance = bal;
                accounts.add(acc);
            }
        } catch (Exception e) {}
    }

    private void saveTransactions() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("transactions.txt", true))) {
            for (Transaction t : transactions)
                pw.println(t.toString());

            transactions.clear(); // avoid duplicates
        } catch (Exception e) {}
    }
}