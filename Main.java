import java.util.*;

// custom exception for insufficient funds
class InsufficientFundsException extends Exception{
    public InsufficientFundsException(String message){
        super(message);
    }
}

// logger class for audit logging
class AuditLogger{
    public static void log(String message){
        System.out.println(new Date()+" -> "+ message);
    }
}

// transaction class to represent a transaction
class Transaction{
    private double amount;
    private String type;
    private Date date;

    public Transaction(double amount, String type){
        this.amount=amount;
        this.type=type;
        this.date=new Date();
    }

    public String toString(){
        return type + ": "+ amount + " | " + date;
    }
}

// account class to represent a bank account
class Account{
    private String accountHolder;
    private double balance;
    private String password;
    private List<Transaction> transactions;

    public Account(String accountHolder, double balance, String password){
        this.accountHolder=accountHolder;
        this.balance=balance;
        this.password=password;
        this.transactions=new ArrayList<>();
    }

    public boolean verifyPassword(String inputPassword){
        return this.password.equals(inputPassword);
    }

    // deposit method
    public void deposit(double amount){
        if(amount <= 0){
            AuditLogger.log("Failed Deposit (Invalid Amount): "+amount);
            throw new IllegalArgumentException("Amount must be positive");
        }

        balance = balance + amount;
        addTransaction(amount,"Deposit");

        AuditLogger.log("Deposit successful: "+amount +" | Balance: "+ balance); 
    }

    // process transaction method for withdrawal
    public void processTransaction(double amount) throws InsufficientFundsException{

        if(amount <= 0){
            AuditLogger.log("Failed Withdrawal (Invalid Amount): "+amount);
            throw new IllegalArgumentException("Amount must be positive");
        }

        if(amount > balance){
            AuditLogger.log("Failed Withdrawal (Insufficient Funds): "+amount+" | Balance: "+ balance);
            throw new InsufficientFundsException("Insufficient balance");

        }

        balance = balance - amount;
        addTransaction(amount,"Withdraw");

        AuditLogger.log("Successful Withdrawal: "+amount+" | Balance: "+ balance);
    }

    // method to add transaction to the list
    private void addTransaction(double amount, String type){
        if(transactions.size() == 5){
            transactions.remove(0);
        }
        transactions.add(new Transaction(amount,type));
    }

    // method to display transaction history
    public void printMiniStatement(){
        System.out.println("\n---- Mini Statement ----");
        for(Transaction t: transactions){
            System.out.println(t);
        }
    }

    public double getBalance(){
        return balance;
    }

    public String getAccountHolder(){
        return accountHolder;
    }
}

// main class to test the banking system
public class Main{
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        Map<String, Account> accounts = new HashMap<>();

        while(true){

            System.out.println("\n===== Welcome to the Banking System =====");
            System.out.println("1. Create Account");
            System.out.println("2. Login");
            System.out.println("3. Exit");

            System.out.print("Enter your choice: ");
            int choice;
            try{
                choice = scanner.nextInt();
            }
            catch(InputMismatchException e){
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // clear the invalid input
                continue;
            }

            switch(choice){

                // create account
                case 1:
                    scanner.nextLine();

                    System.out.print("Enter account holder name: ");
                    String name = scanner.nextLine();

                    if(accounts.containsKey(name)){
                        System.out.println("Account already exists with this name. Please choose a different name.");
                        break;
                    }

                    System.out.print("Set Password: ");
                    String password = scanner.next();

                    double balance;
                    while(true){
                        System.out.print("Initial Deposit: ");
                        try{
                            balance = scanner.nextDouble();
                            if(balance < 0){
                                System.out.println("Initial deposit cannot be negative. Please enter a valid amount.");
                                continue;
                            }
                            break;
                        }
                        catch(InputMismatchException e){
                            System.out.println("Invalid input. Please enter a valid number for initial deposit.");
                            scanner.next(); // clear the invalid input
                        }
                    }

                    accounts.put(name, new Account(name, balance, password));
                    AuditLogger.log("Account Created: "+ name);
                    System.out.println("Account created successfully!");
                    break;

                // login
                case 2:
                    scanner.nextLine();
                    System.out.print("Enter account holder name: ");
                    String loginName = scanner.nextLine();

                    if(!accounts.containsKey(loginName)){
                        System.out.println("No account found with this name.");
                        AuditLogger.log("Failed Login Attempt: "+ loginName);
                        break;
                    }

                    Account acc=accounts.get(loginName);

                    System.out.print("Enter password: ");
                    String inputPassword = scanner.next();

                    if(!acc.verifyPassword(inputPassword)){
                        System.out.println("Incorrect password!");
                        AuditLogger.log("Failed Login Attempt (Incorrect Password): "+ loginName);
                        break;
                    }

                    AuditLogger.log("Successful Login: "+ loginName);
                    System.out.println("Login successful!");

                    // user menu after login
                    while(true){

                        System.out.println("\n===== Welcome, "+ acc.getAccountHolder() +" =====");
                        System.out.println("1. Deposit");
                        System.out.println("2. Withdraw");
                        System.out.println("3. Mini Statement");
                        System.out.println("4. Balance");
                        System.out.println("5. Logout");

                        System.out.print("Enter your choice: ");
                        int userChoice;
                        try{
                            userChoice = scanner.nextInt();
                        }
                        catch(InputMismatchException e){
                            System.out.println("Invalid input. Please enter a number.");
                            scanner.next(); // clear the invalid input
                            continue;
                        }
                        
                        try{
                            switch(userChoice){
                                case 1:
                                    System.out.print("Enter amount to deposit: ");
                                    double depositAmount;
                                    try{
                                        depositAmount = scanner.nextDouble();
                                        acc.deposit(depositAmount);
                                    }
                                    catch(InputMismatchException e){
                                        System.out.println("Invalid input. Please enter a valid number for deposit amount.");
                                        scanner.next(); // clear the invalid input
                                    }
                                    break;
                                
                                case 2:
                                    System.out.print("Enter amount to withdraw: ");
                                    double withdrawAmount;
                                    try{
                                        withdrawAmount = scanner.nextDouble();
                                        acc.processTransaction(withdrawAmount);
                                    }
                                    catch(InputMismatchException e){
                                        System.out.println("Invalid input. Please enter a valid number for withdrawal amount.");
                                        scanner.next(); // clear the invalid input
                                    }
                                    break;

                                case 3:
                                    acc.printMiniStatement();
                                    break;
                                
                                case 4:
                                    System.out.println("Current Balance: "+ acc.getBalance());
                                    break;

                                case 5:
                                    AuditLogger.log("Logout: "+ acc.getAccountHolder());
                                    System.out.println("Logged out successfully!");
                                    break;
                                
                                default:
                                    System.out.println("Invalid choice. Please try again.");

                            }

                            if (userChoice == 5){
                                break;
                            }
                        }
                        catch(IllegalArgumentException | InsufficientFundsException e){
                            System.out.println("Error: "+ e.getMessage());
                        }
                    }
                    break;

                case 3:
                    System.out.println("Thank you for using the Banking System!!");
                    AuditLogger.log("System Exit");
                    scanner.close();
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}