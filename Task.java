import java.util.Objects;

public class Task {
    private int id;
    private int totalHours;
    private int remainingHours;
    private int assignedWorkerId;

    public Task(int id, int totalHours, int assignedWorkerId) {
        this.id = id;
        this.totalHours = totalHours;
        this.remainingHours = totalHours;
        this.assignedWorkerId = assignedWorkerId;
    }

    public int getId() { return id; }
    public int getTotalHours() { return totalHours; }
    public int getRemainingHours() { return remainingHours; }
    public int getAssignedWorkerId() { return assignedWorkerId; }

    public void reduceHours(int hours) {

        this.remainingHours = Math.max(0, this.remainingHours - hours);
    }


    public boolean isCompleted() {

        return remainingHours <= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }



}