package taskmanager;

import java.io.Serializable;

// Data model representing a user task
public class Task implements Serializable {
    private static final long serialVersionUID = 1L;
    // Instance fields
    private String taskName;       
    private String priorityLevel;  
    private String taskCategory;   
    private String taskDetails;    
    private String dueDate;        
    private boolean completionStatus; 


    public Task(String taskName, String priorityLevel, 
               String taskCategory, String taskDetails, String dueDate) {
        this.taskName = taskName;
        this.priorityLevel = priorityLevel;
        this.taskCategory = taskCategory;
        this.taskDetails = taskDetails;
        this.dueDate = dueDate;
        this.completionStatus = false;  
    }

    // ACCESOR
    public String getName() { return taskName; }
    public String getPriority() { return priorityLevel; }
    public String getCategory() { return taskCategory; }
    public String getDescription() { return taskDetails; }
    public String getDate() { return dueDate; }
    public boolean isComplete() { return completionStatus; }

    // MUTATOR
    public void setName(String newName) { this.taskName = newName; }  
    public void setPriority(String newPriority) { this.priorityLevel = newPriority; }
    public void setCategory(String newCategory) { this.taskCategory = newCategory; }
    public void setDescription(String details) { this.taskDetails = details; }  
    public void setDate(String date) { this.dueDate = date; }
    public void setComplete(boolean status) { this.completionStatus = status; }  
}