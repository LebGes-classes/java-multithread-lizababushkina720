import java.util.ArrayList;
import java.util.List;


public class Worker implements Runnable {
    private int id;
    private String name;
    private int dayWorkedHours;
    private int dayTaskHours;
    private int dayIdleHours;
    private List<Integer> completedTasks;
    private Task currentTask;


    public Worker(int id, String name) {
        this.id = id;
        this.name = name;
        this.dayWorkedHours = 0;
        this.dayTaskHours = 0;
        this.dayIdleHours = 0;
        this.completedTasks = new ArrayList<>();
        this.currentTask = null;

    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getDayWorkedHours() { return dayWorkedHours; }
    public int getDayTaskHours() { return dayTaskHours; }
    public int getDayIdleHours() { return dayIdleHours; }
    public List<Integer> getCompletedTasks() { return completedTasks; }
    public Task getCurrentTask() { return currentTask; }

    public synchronized void assignTask(Task task) {
        this.currentTask = task;
    }

    public synchronized void workOneHour() {
        if (dayWorkedHours >= 8) return;
        dayWorkedHours++;
        if (currentTask != null && !currentTask.isCompleted()) {
            currentTask.reduceHours(1);
            dayTaskHours++;
            if (currentTask.isCompleted()) {
                completedTasks.add(currentTask.getId());
                currentTask = null;
            }
        } else {
            dayIdleHours++;
        }
    }

    public void resetDailyStats() {
        dayWorkedHours = 0;
        dayTaskHours = 0;
        dayIdleHours = 0;
        completedTasks.clear();
    }

    public double getEfficiency() {
        return dayWorkedHours == 0 ? 0.0 : (double) dayTaskHours / dayWorkedHours * 100;
    }

    @Override
    public void run() {
        for (int hour = 0; hour < 8; hour++) {
            workOneHour();
        }
        System.out.println(name + " закончил рабочий день");
    }
}