import java.awt.event.*;
import java.math.*;
import java.time.*;
import java.util.*;
import javax.swing.*;

// ---------------- PURCHASE CLASS ----------------
class Purchase {

    BigDecimal amount;
    int points;
    LocalDate date;

    Purchase(BigDecimal amount, int points) {
        this.amount = amount;
        this.points = points;
        this.date = LocalDate.now();
    }

    public String toString() {
        return "Date: " + date
                + " | Amount: Rs." + amount
                + " | Points: " + points;
    }
}

// ---------------- CUSTOMER CLASS ----------------
class Customer {

    int id;
    String name;
    String phone;
    String email;

    int points;

    ArrayList<Purchase> purchases =
            new ArrayList<>();

    Customer(int id, String name, String phone, String email) {

        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;

        points = 0;
    }

    void addPurchase(Purchase purchase) {

        purchases.add(purchase);

        points = points + purchase.points;
    }

    void redeemPoints(int pointsToRedeem)
            throws Exception {

        if (pointsToRedeem <= 0) {

            throw new Exception(
                    "Points must be greater than 0."
            );
        }

        if (pointsToRedeem > points) {

            throw new Exception(
                    "Insufficient loyalty points."
            );
        }

        points = points - pointsToRedeem;
    }

    BigDecimal getTotalSpending() {

        BigDecimal total =
                BigDecimal.ZERO;

        for (Purchase p : purchases) {

            total = total.add(p.amount);
        }

        return total;
    }

    String getStatus() {

        if (points >= 1000) {
            return "PLATINUM";
        }

        if (points >= 500) {
            return "GOLD";
        }

        if (points >= 200) {
            return "SILVER";
        }

        return "BRONZE";
    }
}

// ---------------- LOYALTY RULE CLASS ----------------
class LoyaltyRule {

    BigDecimal amountPerPoint;
    int pointsPerAmount;

    BigDecimal bonusAmount;
    int bonusPoints;

    LoyaltyRule() {

        amountPerPoint =
                new BigDecimal("100");

        pointsPerAmount = 10;

        bonusAmount =
                new BigDecimal("1000");

        bonusPoints = 50;
    }

    int calculatePoints(BigDecimal amount) {

        int points =
                amount.divide(
                        amountPerPoint,
                        0,
                        RoundingMode.DOWN
                ).intValue();

        points = points * pointsPerAmount;

        if (amount.compareTo(bonusAmount) >= 0) {

            points = points + bonusPoints;
        }

        return points;
    }
}

// ---------------- LOYALTY MANAGEMENT CLASS ----------------
class LoyaltyManager {

    // TreeMap
    TreeMap<Integer, Customer> customers =
            new TreeMap<>();

    LoyaltyRule rule =
            new LoyaltyRule();

    // Register customer
    String registerCustomer(
            int id,
            String name,
            String phone,
            String email) {

        if (id <= 0) {
            return "Customer ID must be greater than 0.";
        }

        if (name.isEmpty()) {
            return "Customer name cannot be empty.";
        }

        if (customers.containsKey(id)) {

            return "Customer ID already exists.";
        }

        Customer customer =
                new Customer(
                        id,
                        name,
                        phone,
                        email
                );

        customers.put(id, customer);

        return "Customer registered successfully.";
    }

    // Search customer
    Customer searchCustomer(int id) {

        return customers.get(id);
    }

    // Record purchase
    String addPurchase(
            int id,
            BigDecimal amount) {

        Customer customer =
                searchCustomer(id);

        if (customer == null) {

            return "Customer not found.";
        }

        if (amount.compareTo(
                BigDecimal.ZERO) <= 0) {

            return "Amount must be greater than 0.";
        }

        int points =
                rule.calculatePoints(amount);

        Purchase purchase =
                new Purchase(
                        amount,
                        points
                );

        customer.addPurchase(purchase);

        return "Purchase recorded.\n"
                + "Points earned: "
                + points
                + "\nTotal points: "
                + customer.points;
    }

    // Redeem points
    String redeemPoints(
            int id,
            int points)
            throws Exception {

        Customer customer =
                searchCustomer(id);

        if (customer == null) {

            return "Customer not found.";
        }

        customer.redeemPoints(points);

        return "Points redeemed successfully.\n"
                + "Remaining points: "
                + customer.points;
    }

    // Display all customers
    String displayCustomers() {

        if (customers.isEmpty()) {

            return "No customers registered.";
        }

        String result = "";

        for (Customer customer :
                customers.values()) {

            result = result
                    + "ID: " + customer.id
                    + " | Name: " + customer.name
                    + " | Points: " + customer.points
                    + " | Status: "
                    + customer.getStatus()
                    + "\n";
        }

        return result;
    }

    // Customer details
    String customerDetails(int id) {

        Customer customer =
                searchCustomer(id);

        if (customer == null) {

            return "Customer not found.";
        }

        String result = "";

        result = result
                + "========== CUSTOMER DETAILS ==========\n"
                + "Customer ID: " + customer.id + "\n"
                + "Name: " + customer.name + "\n"
                + "Phone: " + customer.phone + "\n"
                + "Email: " + customer.email + "\n"
                + "Points: " + customer.points + "\n"
                + "Status: " + customer.getStatus() + "\n"
                + "Total Spending: Rs."
                + customer.getTotalSpending()
                + "\n"
                + "Total Purchases: "
                + customer.purchases.size()
                + "\n";

        result = result
                + "---------------------------------------\n";

        for (Purchase p :
                customer.purchases) {

            result = result
                    + p + "\n";
        }

        return result;
    }

    // Summary
    String summary() {

        if (customers.isEmpty()) {

            return "No customers registered.";
        }

        int totalCustomers =
                customers.size();

        int totalPoints = 0;

        int totalPurchases = 0;

        BigDecimal totalAmount =
                BigDecimal.ZERO;

        for (Customer customer :
                customers.values()) {

            totalPoints =
                    totalPoints + customer.points;

            totalPurchases =
                    totalPurchases
                    + customer.purchases.size();

            totalAmount =
                    totalAmount.add(
                            customer.getTotalSpending()
                    );
        }

        String result = "";

        result = result
                + "========== LOYALTY SUMMARY ==========\n"
                + "Total Customers: "
                + totalCustomers + "\n"
                + "Total Purchases: "
                + totalPurchases + "\n"
                + "Total Spending: Rs."
                + totalAmount + "\n"
                + "Total Loyalty Points: "
                + totalPoints + "\n"
                + "Report Date: "
                + LocalDate.now() + "\n"
                + "======================================";

        return result;
    }

    // Update loyalty rule
    String updateRule(
            BigDecimal amount,
            int points,
            BigDecimal bonusAmount,
            int bonusPoints) {

        if (amount.compareTo(
                BigDecimal.ZERO) <= 0) {

            return "Amount must be greater than 0.";
        }

        if (points <= 0) {

            return "Points must be greater than 0.";
        }

        if (bonusAmount.compareTo(
                BigDecimal.ZERO) < 0) {

            return "Bonus amount cannot be negative.";
        }

        if (bonusPoints < 0) {

            return "Bonus points cannot be negative.";
        }

        rule.amountPerPoint = amount;
        rule.pointsPerAmount = points;
        rule.bonusAmount = bonusAmount;
        rule.bonusPoints = bonusPoints;

        return "Loyalty rule updated successfully.";
    }
}

// ---------------- GUI CLASS ----------------
public class CustomerLoyaltySystem
        extends JFrame
        implements ActionListener {

    LoyaltyManager loyalty;

    JTextField idField;
    JTextField nameField;
    JTextField phoneField;
    JTextField emailField;

    JTextField purchaseIdField;
    JTextField amountField;

    JTextField redeemIdField;
    JTextField redeemPointsField;

    JTextField searchIdField;

    JTextField ruleAmountField;
    JTextField rulePointsField;
    JTextField bonusAmountField;
    JTextField bonusPointsField;

    JTextArea outputArea;

    JButton registerButton;
    JButton purchaseButton;
    JButton redeemButton;
    JButton searchButton;
    JButton showCustomersButton;
    JButton summaryButton;
    JButton updateRuleButton;
    JButton clearButton;

    CustomerLoyaltySystem() {

        loyalty =
                new LoyaltyManager();

        setTitle(
                "Customer Loyalty Points System"
        );

        setSize(900, 700);

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        setLayout(null);

        // ---------------- CUSTOMER INFORMATION ----------------

        JLabel idLabel =
                new JLabel("Customer ID:");

        idLabel.setBounds(
                30, 20, 100, 25
        );

        add(idLabel);

        idField =
                new JTextField();

        idField.setBounds(
                130, 20, 100, 25
        );

        add(idField);

        JLabel nameLabel =
                new JLabel("Name:");

        nameLabel.setBounds(
                250, 20, 60, 25
        );

        add(nameLabel);

        nameField =
                new JTextField();

        nameField.setBounds(
                310, 20, 150, 25
        );

        add(nameField);

        JLabel phoneLabel =
                new JLabel("Phone:");

        phoneLabel.setBounds(
                480, 20, 60, 25
        );

        add(phoneLabel);

        phoneField =
                new JTextField();

        phoneField.setBounds(
                540, 20, 150, 25
        );

        add(phoneField);

        JLabel emailLabel =
                new JLabel("Email:");

        emailLabel.setBounds(
                30, 55, 60, 25
        );

        add(emailLabel);

        emailField =
                new JTextField();

        emailField.setBounds(
                90, 55, 220, 25
        );

        add(emailField);

        registerButton =
                new JButton("Register Customer");

        registerButton.setBounds(
                330, 55, 160, 30
        );

        add(registerButton);

        // ---------------- OUTPUT AREA ----------------

        outputArea =
                new JTextArea();

        outputArea.setEditable(false);

        JScrollPane scrollPane =
                new JScrollPane(outputArea);

        scrollPane.setBounds(
                30, 100, 820, 280
        );

        add(scrollPane);

        // ---------------- PURCHASE ----------------

        JLabel purchaseIdLabel =
                new JLabel("Purchase ID:");

        purchaseIdLabel.setBounds(
                30, 400, 90, 25
        );

        add(purchaseIdLabel);

        purchaseIdField =
                new JTextField();

        purchaseIdField.setBounds(
                120, 400, 90, 25
        );

        add(purchaseIdField);

        JLabel amountLabel =
                new JLabel("Amount:");

        amountLabel.setBounds(
                230, 400, 70, 25
        );

        add(amountLabel);

        amountField =
                new JTextField();

        amountField.setBounds(
                300, 400, 100, 25
        );

        add(amountField);

        purchaseButton =
                new JButton("Record Purchase");

        purchaseButton.setBounds(
                420, 400, 150, 30
        );

        add(purchaseButton);

        // ---------------- REDEEM ----------------

        JLabel redeemIdLabel =
                new JLabel("Redeem ID:");

        redeemIdLabel.setBounds(
                30, 440, 90, 25
        );

        add(redeemIdLabel);

        redeemIdField =
                new JTextField();

        redeemIdField.setBounds(
                120, 440, 90, 25
        );

        add(redeemIdField);

        JLabel redeemPointsLabel =
                new JLabel("Points:");

        redeemPointsLabel.setBounds(
                230, 440, 60, 25
        );

        add(redeemPointsLabel);

        redeemPointsField =
                new JTextField();

        redeemPointsField.setBounds(
                300, 440, 100, 25
        );

        add(redeemPointsField);

        redeemButton =
                new JButton("Redeem Points");

        redeemButton.setBounds(
                420, 440, 150, 30
        );

        add(redeemButton);

        // ---------------- SEARCH ----------------

        JLabel searchLabel =
                new JLabel("Search ID:");

        searchLabel.setBounds(
                30, 480, 80, 25
        );

        add(searchLabel);

        searchIdField =
                new JTextField();

        searchIdField.setBounds(
                120, 480, 90, 25
        );

        add(searchIdField);

        searchButton =
                new JButton("Search Customer");

        searchButton.setBounds(
                230, 480, 150, 30
        );

        add(searchButton);

        showCustomersButton =
                new JButton("Show Customers");

        showCustomersButton.setBounds(
                390, 480, 140, 30
        );

        add(showCustomersButton);

        summaryButton =
                new JButton("Summary");

        summaryButton.setBounds(
                540, 480, 100, 30
        );

        add(summaryButton);

        // ---------------- LOYALTY RULE ----------------

        JLabel ruleAmountLabel =
                new JLabel("₹ Amount:");

        ruleAmountLabel.setBounds(
                30, 530, 80, 25
        );

        add(ruleAmountLabel);

        ruleAmountField =
                new JTextField("100");

        ruleAmountField.setBounds(
                110, 530, 80, 25
        );

        add(ruleAmountField);

        JLabel rulePointsLabel =
                new JLabel("Points:");

        rulePointsLabel.setBounds(
                200, 530, 60, 25
        );

        add(rulePointsLabel);

        rulePointsField =
                new JTextField("10");

        rulePointsField.setBounds(
                260, 530, 70, 25
        );

        add(rulePointsField);

        JLabel bonusAmountLabel =
                new JLabel("Bonus ₹:");

        bonusAmountLabel.setBounds(
                340, 530, 70, 25
        );

        add(bonusAmountLabel);

        bonusAmountField =
                new JTextField("1000");

        bonusAmountField.setBounds(
                410, 530, 80, 25
        );

        add(bonusAmountField);

        JLabel bonusPointsLabel =
                new JLabel("Bonus Points:");

        bonusPointsLabel.setBounds(
                500, 530, 90, 25
        );

        add(bonusPointsLabel);

        bonusPointsField =
                new JTextField("50");

        bonusPointsField.setBounds(
                590, 530, 70, 25
        );

        add(bonusPointsField);

        updateRuleButton =
                new JButton("Update Rule");

        updateRuleButton.setBounds(
                670, 530, 120, 30
        );

        add(updateRuleButton);

        // ---------------- CLEAR ----------------

        clearButton =
                new JButton("Clear");

        clearButton.setBounds(
                30, 580, 100, 30
        );

        add(clearButton);

        // ---------------- ACTION LISTENERS ----------------

        registerButton.addActionListener(this);
        purchaseButton.addActionListener(this);
        redeemButton.addActionListener(this);
        searchButton.addActionListener(this);
        showCustomersButton.addActionListener(this);
        summaryButton.addActionListener(this);
        updateRuleButton.addActionListener(this);
        clearButton.addActionListener(this);

        setVisible(true);
    }

    // ---------------- BUTTON OPERATIONS ----------------

    public void actionPerformed(ActionEvent e) {

        try {

            // Register Customer
            if (e.getSource() ==
                    registerButton) {

                int id =
                        Integer.parseInt(
                                idField.getText()
                        );

                String name =
                        nameField.getText();

                String phone =
                        phoneField.getText();

                String email =
                        emailField.getText();

                String message =
                        loyalty.registerCustomer(
                                id,
                                name,
                                phone,
                                email
                        );

                outputArea.setText(
                        message
                );
            }

            // Record Purchase
            else if (e.getSource() ==
                    purchaseButton) {

                int id =
                        Integer.parseInt(
                                purchaseIdField.getText()
                        );

                BigDecimal amount =
                        new BigDecimal(
                                amountField.getText()
                        );

                String message =
                        loyalty.addPurchase(
                                id,
                                amount
                        );

                outputArea.setText(
                        message
                );
            }

            // Redeem Points
            else if (e.getSource() ==
                    redeemButton) {

                int id =
                        Integer.parseInt(
                                redeemIdField.getText()
                        );

                int points =
                        Integer.parseInt(
                                redeemPointsField.getText()
                        );

                String message =
                        loyalty.redeemPoints(
                                id,
                                points
                        );

                outputArea.setText(
                        message
                );
            }

            // Search Customer
            else if (e.getSource() ==
                    searchButton) {

                int id =
                        Integer.parseInt(
                                searchIdField.getText()
                        );

                String details =
                        loyalty.customerDetails(
                                id
                        );

                outputArea.setText(
                        details
                );
            }

            // Show Customers
            else if (e.getSource() ==
                    showCustomersButton) {

                outputArea.setText(
                        "========== CUSTOMERS ==========\n"
                );

                outputArea.append(
                        loyalty.displayCustomers()
                );
            }

            // Summary
            else if (e.getSource() ==
                    summaryButton) {

                outputArea.setText(
                        loyalty.summary()
                );
            }

            // Update Rule
            else if (e.getSource() ==
                    updateRuleButton) {

                BigDecimal amount =
                        new BigDecimal(
                                ruleAmountField.getText()
                        );

                int points =
                        Integer.parseInt(
                                rulePointsField.getText()
                        );

                BigDecimal bonusAmount =
                        new BigDecimal(
                                bonusAmountField.getText()
                        );

                int bonusPoints =
                        Integer.parseInt(
                                bonusPointsField.getText()
                        );

                String message =
                        loyalty.updateRule(
                                amount,
                                points,
                                bonusAmount,
                                bonusPoints
                        );

                outputArea.setText(
                        message
                );
            }

            // Clear
            else if (e.getSource() ==
                    clearButton) {

                idField.setText("");
                nameField.setText("");
                phoneField.setText("");
                emailField.setText("");

                purchaseIdField.setText("");
                amountField.setText("");

                redeemIdField.setText("");
                redeemPointsField.setText("");

                searchIdField.setText("");

                outputArea.setText("");
            }

        } catch (NumberFormatException ex) {

            outputArea.setText(
                    "Please enter valid numbers."
            );

        } catch (Exception ex) {

            outputArea.setText(
                    ex.getMessage()
            );
        }
    }

    // ---------------- MAIN METHOD ----------------

    public static void main(String[] args) {

        new CustomerLoyaltySystem();
    }
}