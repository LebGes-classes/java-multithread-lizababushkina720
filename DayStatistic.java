import java.util.ArrayList;
import java.util.List;

public class DayStatistic {
    private int workerId;
    private String workerName;
    private int workedHours;
    private int taskHours;
    private int idleHours;
    private double efficiency;
    private List<Integer> completedTasks;

    public DayStatistic(Worker worker) {
        this.workerId = worker.getId();
        this.workerName = worker.getName();
        this.workedHours = worker.getDayWorkedHours();
        this.taskHours = worker.getDayTaskHours();
        this.idleHours = worker.getDayIdleHours();
        this.efficiency = worker.getEfficiency();
        this.completedTasks = new ArrayList<>(worker.getCompletedTasks());
    }

    public int getWorkerId() { return workerId; }
    public String getWorkerName() { return workerName; }
    public int getWorkedHours() { return workedHours; }
    public int getTaskHours() { return taskHours; }
    public int getIdleHours() { return idleHours; }
    public double getEfficiency() { return efficiency; }
    public List<Integer> getCompletedTasks() { return completedTasks; }
}