package gui;

import java.awt.Color;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.renderer.category.GanttRenderer;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;

/** @see https://stackoverflow.com/questions/8938690 */
/**
 * 
 * Since jfreechart package does not include any methods for distinguishing between subtasks,
 * code from stackoverflow user 'trashgod' has been used here.
 *
 */
public class MyRenderer extends GanttRenderer {

      private static final int PASS = 2; // assumes two passes
      private final List<Color> clut = new ArrayList<Color>();
      private final TaskSeriesCollection model;
      private int row;
      private int col;
      private int index;

      public MyRenderer(TaskSeriesCollection model) {
          this.model = model;
      }

      @Override
      public Paint getItemPaint(int row, int col) {
          if (clut.isEmpty() || this.row != row || this.col != col) {
              initClut(row, col);
              this.row = row;
              this.col = col;
              index = 0;
          }
          int clutIndex = index++ / PASS;
          return clut.get(clutIndex);
      }

      private void initClut(int row, int col) {
          clut.clear();
          Color c = (Color) super.getItemPaint(row, col);
          float[] a = new float[3];
          Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), a);
          TaskSeries series = (TaskSeries) model.getRowKeys().get(row);
		List<Task> tasks = series.getTasks(); // unchecked
          int taskCount = tasks.get(0).getSubtaskCount();
          taskCount = Math.max(1, taskCount);
          for (int i = 0; i < taskCount; i++) {
              clut.add(Color.getHSBColor(a[0], a[1]/i, a[2]));
          }
      }
   }