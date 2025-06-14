import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManagementSystem {
    private Map<Integer, Worker> workers;
    private List<Task> tasks;

    public ManagementSystem() {
        this.workers = new HashMap<>();
        this.tasks = new ArrayList<>();
    }

    public void readDataFromExcel(String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet workerSheet = workbook.getSheet("Workers");
            for (Row row : workerSheet) {
                if (row.getRowNum() == 0) continue;
                int id = (int) row.getCell(0).getNumericCellValue();
                String name = row.getCell(1).getStringCellValue();
                workers.put(id, new Worker(id, name));
            }

            Sheet taskSheet = workbook.getSheet("Tasks");
            for (Row row : taskSheet) {
                if (row.getRowNum() == 0) continue;
                int id = (int) row.getCell(0).getNumericCellValue();
                int totalHours = (int) row.getCell(1).getNumericCellValue();
                Cell assignedCell = row.getCell(2);
                int assignedWorkerId = assignedCell != null ? (int) assignedCell.getNumericCellValue() : 0;
                tasks.add(new Task(id, totalHours, assignedWorkerId));
            }
        }
    }

    // назначаем задачу
    private synchronized Task findTaskForWorker(int workerId) {
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            int assignedWorkerId = task.getAssignedWorkerId();
            if (assignedWorkerId==0 || assignedWorkerId==workerId) {
                tasks.remove(i);
                return task;
            }
        }
        return null;
    }



    // рабочий день
    private void simulateDay(int day) throws IOException {
        resetDayStats();
        List<Thread> threads = new ArrayList<>();

        for (Worker worker : workers.values()) {
            if (worker.getCurrentTask() == null && worker.getDayWorkedHours() < 8) {
                Task task = findTaskForWorker(worker.getId());
                if (task != null) {
                    worker.assignTask(task);
                }
            }
        }
        // запускаем потоки для каждого сотрудника
        for (Worker worker : workers.values()) {
            Thread thread = new Thread(worker);
            threads.add(thread);
            thread.start();
        }

        // ожидаем завершения всех потоков
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("симуляция дня " + day + " была прервана.");
            }}



        // сохранение статистики
        List<DayStatistic> dayStats = new ArrayList<>();
        for (Worker worker : workers.values()) {
            dayStats.add(new DayStatistic(worker));
        }
        saveDayStatsToExcel("day_" + day + "_stats.xlsx", dayStats);
        System.out.println("день " + day + " завершен, статистика сохранена :)");
    }

    // Полная симуляция
    public void simulateWork() throws IOException {
        int day = 1;
        while (!tasks.isEmpty() || workers.values().stream().anyMatch(w -> w.getCurrentTask() != null)) {
            simulateDay(day);
            day++;
        }
    }

    // сброс статистики
    private void resetDayStats() {
        for (Worker worker : workers.values()) {
            worker.resetDailyStats();
        }
    }

    // сохранение статистики в excel
    private void saveDayStatsToExcel(String filePath, List<DayStatistic> stats) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream fos = new FileOutputStream(new File(filePath))) {
            Sheet sheet = workbook.createSheet("Daily Statistics");
            Row header = sheet.createRow(0);
            String[] headers = {"ID", "имя", "отработано", "на задачах", "простой", "эффективность", "завершенные задачи"};
            for (int i = 0; i < headers.length; i++) {
                header.createCell(i).setCellValue(headers[i]);
            }

            int rowNum = 1;
            for (DayStatistic stat : stats) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(stat.getWorkerId());
                row.createCell(1).setCellValue(stat.getWorkerName());
                row.createCell(2).setCellValue(stat.getWorkedHours());
                row.createCell(3).setCellValue(stat.getTaskHours());
                row.createCell(4).setCellValue(stat.getIdleHours());
                row.createCell(5).setCellValue(stat.getEfficiency());
                row.createCell(6).setCellValue(stat.getCompletedTasks().toString());
            }
            workbook.write(fos);
        }
    }
}