package org.kafka.experiment.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates CSV file report
 */
public class CsvReport {

    private List<Column> columns = new ArrayList<>();

    private class Column {
        String title;
        List<Integer> data;

        Column(String title, List<Integer> data) {
            this.title = title;
            this.data = data;
        }
    }

    /**
     * Adds data to the report
     *
     * @param title
     * @param data
     */
    public synchronized void addColumn(String title, List<Integer> data) {
        columns.add(new Column(title, data));
    }

    /**
     * Generates CSV report
     *
     * @param fileName
     */
    public synchronized void generateReport(String fileName) {
        int max = adjustReportColumns();
        try {
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName))) {

                writeTitle(bufferedWriter);

                writeTable(max, bufferedWriter);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeTable(int max, BufferedWriter bufferedWriter) throws IOException {
        for (int i = 0; i < max; ++i) {
            for (int j = 0; j < columns.size(); ++j) {

                if (columns.get(j).data.get(i) == null) {
                    bufferedWriter.write("");
                } else {
                    bufferedWriter.write(columns.get(j).data.get(i).toString());
                }

                if (j + 1 < columns.size()) {
                    bufferedWriter.write(",");
                }
            }
            bufferedWriter.newLine();
        }
    }

    private void writeTitle(BufferedWriter bufferedWriter) throws IOException {
        for (int i = 0; i < columns.size(); ++i) {
            bufferedWriter.write(columns.get(i).title);
            if (i + 1 < columns.size()) {
                bufferedWriter.write(",");
            }
        }
        bufferedWriter.newLine();
    }

    private int adjustReportColumns() {
        // Find the column with maximum number of elements
        int max = 0;
        for (Column column : columns) {
            if (max < column.data.size()) {
                max = column.data.size();
            }
        }

        // Adjust all column sizes
        for (Column column : columns) {
            for (int i = column.data.size(); i < max; ++i) {
                column.data.add(null);
            }
        }

        return max;
    }
}
