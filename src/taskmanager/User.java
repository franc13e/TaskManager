package taskmanager;

import java.util.LinkedList;

public class User {
    private String userId;  
    private LinkedList<Task> userTasks;  

    public User(String userId) { 
        this.userId = userId;
        this.userTasks = new LinkedList<>();
    }

    public String getUsername() { return userId; }
    public LinkedList<Task> getTasks() { return userTasks; }
    public void addTask(Task t) { userTasks.add(t); }  
    public void removeTask(int i) { userTasks.remove(i); }  
}