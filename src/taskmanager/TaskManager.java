package taskmanager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Queue;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import java.io.InputStream;
import java.io.IOException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TaskManager {
    private static final String DATA_FILE = "taskmanager_data.ser";
    private LinkedList<User> users = new LinkedList<>();
    private Queue<String> userOperations = new LinkedList<>(); // Queue for logging operations
    private User currentUser;
    private JFrame frame;
    private DefaultTableModel taskTableModel;
    private JTable taskTable;
    private JLabel currentUserLabel;

    // Initializes the main application window with the default settings
    public TaskManager() {
        // Create and configure main application frame
        // Ref: https://docs.oracle.com/javase/tutorial/uiswing/components/frame.html
        
        //GUI INITIALIZATION
        frame = new JFrame("Task Managing Application"); //Name of app
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //WINDOW CONFIG
        frame.setSize(800, 600); // SIze of UI
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null); //Centres the app on the screen

        //CHANGED DEFAULT ICON TO A PNG
        ImageIcon image = new ImageIcon(TaskManager.class.getResource("planner.png")); 
        frame.setIconImage(image.getImage()); 
        frame.getContentPane().setBackground(new Color(0xF5EFEB)); 

        // MENU BAR
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        JMenuItem addUserItem = new JMenuItem("Add User");
        JMenuItem switchUserItem = new JMenuItem("Switch User");
        JMenuItem viewLogsItem = new JMenuItem("View Logs");
        JMenuItem performanceTestItem = new JMenuItem("Run Performance Test");

        // MENU ITEM AT THE SIDE
        //Contains: Users, Logs, performance test
        menu.add(addUserItem);
        menu.add(switchUserItem);
        menu.add(viewLogsItem);
        menu.add(performanceTestItem);
        menuBar.add(menu);
        frame.setJMenuBar(menuBar);

        // CURRENT USER LABEL -- displays the current user
        currentUserLabel = new JLabel("No user selected", SwingConstants.CENTER);
        currentUserLabel.setFont(new Font("Arial", Font.BOLD, 16));
        frame.add(currentUserLabel, BorderLayout.NORTH);

        // TASK TABLE 
        //Contains: Task Name, Priority, Category, Date, Status in Table Form
        taskTableModel = new DefaultTableModel(new Object[]{"Task Name", "Priority", "Category", "Due Date", "Status"}, 0);
        taskTable = new JTable(taskTableModel);
        taskTable.setRowHeight(25); 

        // PRIORITY COLUMN
        //Different colors base on levels of priority as visual representation
        taskTable.getColumnModel().getColumn(1).setCellRenderer(new PriorityColorRenderer());

        frame.add(new JScrollPane(taskTable), BorderLayout.CENTER);

        // BUTTONS PANEL -- DISPLAYS THE BUTTONS
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton addTaskButton = new JButton("Add Task");
        JButton deleteTaskButton = new JButton("Delete Task");
        JButton completeTaskButton = new JButton("Mark Complete");
        JButton editTaskButton = new JButton("Edit Task");

        // BUTTONS
        // Contains: ADD, DELETE, COMPLETE, EDIT Buttons
        buttonPanel.add(addTaskButton);
        buttonPanel.add(deleteTaskButton);
        buttonPanel.add(completeTaskButton);
        buttonPanel.add(editTaskButton);
        

        frame.add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        addTaskButton.addActionListener(e -> addTask());
        deleteTaskButton.addActionListener(e -> deleteTask());
        completeTaskButton.addActionListener(e -> markTaskComplete());
        editTaskButton.addActionListener(e -> editTask());

        // Menu item actions
//        addUserItem.addActionListener(e -> addUser());
        switchUserItem.addActionListener(e -> switchUser());
        viewLogsItem.addActionListener(e -> viewOperationsLog());
        performanceTestItem.addActionListener(e -> runPerformanceTest());

        // Then load data and show login
    loadData();
    // Add default admin user if no users exist
        if (users.isEmpty()) {
            users.add(new User("francine", "1234"));
        }
        
        // Show login dialog
        if (!showLoginDialog()) {
            System.exit(0);
        }
        
        frame.setVisible(true);
    }

    
     private boolean showLoginDialog() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton registerButton = new JButton("Register");
        
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel(""));
        panel.add(registerButton);

        registerButton.addActionListener(e -> {
            registerNewUser();
            ((Window)SwingUtilities.getWindowAncestor(panel)).dispose();
            showLoginDialog();
        });

        while (true) {
            int option = JOptionPane.showConfirmDialog(frame, panel, "Login", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            if (option != JOptionPane.OK_OPTION) {
                return false;
            }
            
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter both username and password", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }
            
            for (User user : users) {
                if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                    currentUser = user;
                    updateTaskList();
                    updateCurrentUserLabel();
                    return true;
                }
            }
            
            JOptionPane.showMessageDialog(frame, "Invalid username or password", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registerNewUser() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JPasswordField confirmField = new JPasswordField();
        
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Confirm Password:"));
        panel.add(confirmField);

        int option = JOptionPane.showConfirmDialog(frame, panel, "Register New User", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirm = new String(confirmField.getPassword());
            
            if (!password.equals(confirm)) {
                JOptionPane.showMessageDialog(frame, "Passwords don't match", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Username and password cannot be empty", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (users.stream().anyMatch(u -> u.getUsername().equals(username))) {
                JOptionPane.showMessageDialog(frame, "Username already exists", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            User newUser = new User(username, password);
            users.add(newUser);
            saveData();
            JOptionPane.showMessageDialog(frame, "Registration successful!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(DATA_FILE))) {
            oos.writeObject(users);
            oos.writeObject(currentUser);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error saving data: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadData() {
        if (!Files.exists(Paths.get(DATA_FILE))) return;
        
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(DATA_FILE))) {
            users = (LinkedList<User>) ois.readObject();
            currentUser = (User) ois.readObject();
            updateTaskList();
            updateCurrentUserLabel();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error loading data: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Add this to save data when closing the application
    public void onExit() {
        saveData();
        frame.dispose();
    }

    // Updated switchUser method
    private void switchUser() {
        if (users.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No users available.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String[] userNames = users.stream()
                               .map(User::getUsername)
                               .toArray(String[]::new);
                               
        String selectedUser = (String) JOptionPane.showInputDialog(
            frame, 
            "Select User:", 
            "Switch User", 
            JOptionPane.PLAIN_MESSAGE, 
            null, 
            userNames, 
            userNames[0]);
            
        if (selectedUser != null) {
            currentUser = users.stream()
                           .filter(user -> user.getUsername().equals(selectedUser))
                           .findFirst()
                           .orElse(null);
            updateTaskList();
            updateCurrentUserLabel();
            saveData();  // Save after switching user
        }
    }
    

    // ADD TASKS
    // Shows dialog to create a new task
    private void addTask() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(frame, "Please select a user first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JTextField nameField = new JTextField();
        String[] priorities = {"High", "Medium", "Low"};
        JComboBox<String> priorityBox = new JComboBox<>(priorities);
        JTextField categoryField = new JTextField();
        JTextField descriptionField = new JTextField();

        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(editor);

        Object[] message = {
            "Task Name:", nameField,
            "Priority:", priorityBox,
            "Category:", categoryField,
            "Description:", descriptionField,
            "Due Date:", dateSpinner
        };

        int option = JOptionPane.showConfirmDialog(frame, message, "Add Task", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            Date selectedDate = (Date) dateSpinner.getValue();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = dateFormat.format(selectedDate);

            Task newTask = new Task(
                nameField.getText(),
                (String) priorityBox.getSelectedItem(),
                categoryField.getText(),
                descriptionField.getText(),
                formattedDate
            );

            currentUser.addTask(newTask);
            updateTaskList();
            JOptionPane.showMessageDialog(frame, "Task added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // DELETE TASKS
    private void deleteTask() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(frame, "Please select a user first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int selectedIndex = taskTable.getSelectedRow();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a task to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this task?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            currentUser.removeTask(selectedIndex);
            updateTaskList();
            JOptionPane.showMessageDialog(frame, "Task deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    // MARK TASK AS COMPLETE
    private void markTaskComplete() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(frame, "Please select a user first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int selectedIndex = taskTable.getSelectedRow();
        if (selectedIndex != -1) {
            Task task = currentUser.getTasks().get(selectedIndex);
            task.setComplete(true);
            updateTaskList();
            JOptionPane.showMessageDialog(frame, "Task marked as complete!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    // EDIT TASKS
    private void editTask() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(frame, "Please select a user first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int selectedIndex = taskTable.getSelectedRow();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a task to edit.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Task task = currentUser.getTasks().get(selectedIndex);

        JTextField nameField = new JTextField(task.getName());
        String[] priorities = {"High", "Medium", "Low"};
        JComboBox<String> priorityBox = new JComboBox<>(priorities);
        priorityBox.setSelectedItem(task.getPriority());
        JTextField categoryField = new JTextField(task.getCategory());
        JTextField descriptionField = new JTextField(task.getDescription());

        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(editor);
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateSpinner.setValue(dateFormat.parse(task.getDate()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Object[] message = {
            "Task Name:", nameField,
            "Priority:", priorityBox,
            "Category:", categoryField,
            "Description:", descriptionField,
            "Due Date:", dateSpinner
        };

        int option = JOptionPane.showConfirmDialog(frame, message, "Edit Task", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            task.setName(nameField.getText());
            task.setPriority((String) priorityBox.getSelectedItem());
            task.setCategory(categoryField.getText());
            task.setDescription(descriptionField.getText());

            Date selectedDate = (Date) dateSpinner.getValue();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            task.setDate(dateFormat.format(selectedDate));

            updateTaskList();
            JOptionPane.showMessageDialog(frame, "Task edited successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    //VIEW OPERATIONS LOG
    private void viewOperationsLog() {
        if (userOperations.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No operations logged.", "Operation Log", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder log = new StringBuilder("Operation Log:\n");
        for (String operation : userOperations) {
            log.append(operation).append("\n");
        }

        JOptionPane.showMessageDialog(frame, log.toString(), "Operation Log", JOptionPane.INFORMATION_MESSAGE);
    }

    //PERFROMANCE TEST FOR 100/150 USERS
    private void runPerformanceTest() {
        // Panel -- hold all options in one dialog
        JPanel panel = new JPanel(new GridLayout(0, 1));

        // Add or Delete Options 
        JComboBox<String> operationComboBox = new JComboBox<>(new String[]{"Add Users", "Delete Users"});
        panel.add(new JLabel("Select Operation:"));
        panel.add(operationComboBox);

        // Add user count (100/150) options
        JComboBox<String> userCountComboBox = new JComboBox<>(new String[]{"100", "150"});
        panel.add(new JLabel("Select Number of Users:"));
        panel.add(userCountComboBox);

        // Reset logs checkbox
        JCheckBox resetLogsCheckbox = new JCheckBox("Reset Logs Before Test");
        panel.add(resetLogsCheckbox);

        // Combined options dialog
        int result = JOptionPane.showConfirmDialog(frame, panel, "Performance Test Options", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        // Cancels --> exit the test
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        // Selected options
        String operation = (String) operationComboBox.getSelectedItem();
        int userLimit = Integer.parseInt((String) userCountComboBox.getSelectedItem());
        boolean resetLogs = resetLogsCheckbox.isSelected();

        // Reset logs when checkbox is ticked
        if (resetLogs) {
            userOperations.clear();
            users.clear();
            JOptionPane.showMessageDialog(frame, "Logs have been reset.", "Reset Logs", JOptionPane.INFORMATION_MESSAGE);
        }

        long startTime, endTime;

        if (operation.equals("Add Users")) {
            // Loading users from users.txt
            startTime = System.nanoTime();
            List<User> loadedUsers = loadUsersFromFile("users.txt", userLimit);
            users.addAll(loadedUsers);
            endTime = System.nanoTime();
            userOperations.add("Added " + loadedUsers.size() + " users - Time: " + (endTime - startTime) / 1e6 + " ms");
        } else if (operation.equals("Delete Users")) {
            // Deletion of users
            startTime = System.nanoTime();
            for (int i = 0; i < userLimit; i++) {
                if (!users.isEmpty()) {
                    users.remove(0); 
                }
            }
            endTime = System.nanoTime();
            userOperations.add("Deleted " + userLimit + " users - Time: " + (endTime - startTime) / 1e6 + " ms");
        }

        JOptionPane.showMessageDialog(frame, "Performance test completed. Check logs for details.", "Performance Test", JOptionPane.INFORMATION_MESSAGE);
    }

    private List<User> loadUsersFromFile(String filename, int limit) {
        List<User> userList = new ArrayList<>();

        try (InputStream inputStream = getClass().getResourceAsStream("/taskmanager/" + filename)) {
            if (inputStream == null) {
                JOptionPane.showMessageDialog(frame, "File " + filename + " not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return userList;
            }

            try (Scanner scanner = new Scanner(inputStream)) {
                int count = 0;
                while (scanner.hasNextLine() && count < limit) {
                    String username = scanner.nextLine().trim();
                    if (!username.isEmpty()) {
                        userList.add(new User(username));
                        count++;
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error reading file " + filename, "Error", JOptionPane.ERROR_MESSAGE);
        }

        return userList;
    }

    //UPDATE THE TASK LIST
    private void updateTaskList() {
        taskTableModel.setRowCount(0); 
        if (currentUser != null) {
            // Sort tasks by due date --> earliest first
            currentUser.getTasks().sort(Comparator.comparing(Task::getDate));

            for (Task task : currentUser.getTasks()) {
                // Adjust priority based on due date
                String priority = calculatePriority(task.getDate());
                task.setPriority(priority);

                // Add the task to the table
                taskTableModel.addRow(new Object[]{
                    task.getName(),
                    task.getPriority(),
                    task.getCategory(),
                    task.getDate(),
                    task.isComplete() ? "Completed" : "Incomplete"
                });
            }
        }
    }

    // CALCULATE TASK PRIORITY FOR SORTING
    // <=3 Days: High, <=7 Days: Medium, >7 Days: Low
    private String calculatePriority(String dueDate) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date taskDate = dateFormat.parse(dueDate);
            Date currentDate = new Date();

            long diff = taskDate.getTime() - currentDate.getTime();
            long daysDiff = diff / (24 * 60 * 60 * 1000);

            if (daysDiff <= 3) {
                return "High";
            } else if (daysDiff <= 7) {
                return "Medium";
            } else {
                return "Low";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Low";
        }
    }

    //UPDATE THE CURRENT USER LABEL
    private void updateCurrentUserLabel() {
        if (currentUser != null) {
            currentUserLabel.setText("Current User: " + currentUser.getUsername());
        } else {
            currentUserLabel.setText("No user selected");
        }
    }

public static void main(String[] args) {
    TaskManager taskManager = new TaskManager();
    taskManager.frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    taskManager.frame.addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowClosing(java.awt.event.WindowEvent windowEvent) {
            taskManager.onExit();
            System.exit(0);
        }
    });
}
}