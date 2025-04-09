package taskmanager;

import java.util.LinkedList;
import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username;
    private String password;
    private LinkedList<Task> tasks;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.tasks = new LinkedList<>();
    }

    // Getters
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public LinkedList<Task> getTasks() { return tasks; }

    // Task management
    public void addTask(Task task) { tasks.add(task); }
    public void removeTask(int index) { 
        if (index >= 0 && index < tasks.size()) {
            tasks.remove(index); 
        }
    }
}