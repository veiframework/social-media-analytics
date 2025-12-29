package com.chargehub.admin.playwright;


import cn.hutool.core.util.RandomUtil;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.ViewportSize;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Slf4j
public class HumanMouseSimulator {

    private HumanMouseSimulator() {
    }

    // 👇 合理的默认起始位置（单位：像素）
    private static final double DEFAULT_START_X = 150.0;
    private static final double DEFAULT_START_Y = 200.0;


    public static void randomMove(Page page) {
        randomMove(page, RandomUtil.randomInt(300, 1500));
    }

    /**
     * 模拟人类鼠标随机移动（使用贝塞尔曲线 + 随机步延时）
     *
     * @param page       Playwright 页面对象
     * @param durationMs 总移动时间（毫秒），建议 300~1500
     */
    public static void randomMove(Page page, int durationMs) {
        try {
            ViewportSize viewport = page.viewportSize();
            if (viewport == null || viewport.width <= 0 || viewport.height <= 0) {
                return; // 视口无效，跳过
            }

            // 使用固定起始点
            double startX = DEFAULT_START_X;
            double startY = DEFAULT_START_Y;

            // 确保起始点在视口内（兜底）
            startX = Math.min(startX, viewport.width - 50d);
            startY = Math.min(startY, viewport.height - 50d);
            startX = Math.max(50, startX);
            startY = Math.max(50, startY);

            // 随机生成目标点（留出安全边距）
            double endX = RandomUtil.randomDouble(50, viewport.width - 50d);
            double endY = RandomUtil.randomDouble(50, viewport.height - 50d);

            moveWithBezier(page, startX, startY, endX, endY, durationMs);

        } catch (Exception e) {
            log.error("鼠标移动异常: " + e.getMessage());
        }
    }

    /**
     * 模拟人类鼠标移动：使用贝塞尔曲线，在指定时间内完成移动
     *
     * @param page       Playwright 页面对象
     * @param startX     起始 X 坐标
     * @param startY     起始 Y 坐标
     * @param endX       目标 X 坐标
     * @param endY       目标 Y 坐标
     * @param durationMs 总移动时间（毫秒），建议 300~1500
     */
    public static void moveWithBezier(Page page, double startX, double startY,
                                      double endX, double endY, int durationMs) {
        try {
            // 步数：15~30 步（越多越平滑）
            int steps = RandomUtil.randomInt(15, 30);

            // 生成贝塞尔路径点（三次贝塞尔）
            List<Point> points = generateBezierPath(startX, startY, endX, endY, steps);

            // 计算每步的随机延迟（总和 ≈ durationMs）
            long[] delays = generateRandomDelays(durationMs, steps);

            // 移动到起点（确保位置正确）
            page.mouse().move(startX, startY);
            logMousePos(page, startX, startY, "Start");
            // 逐点移动
            for (int i = 1; i < points.size(); i++) {
                Point p = points.get(i);
                page.mouse().move(p.x, p.y);
                logMousePos(page, p.x, p.y, "Step " + i + "/" + (points.size() - 1));
                if (delays[i - 1] > 0) {
                    page.waitForTimeout(delays[i - 1]);
                }
            }
        } catch (Exception e) {
            log.error("鼠标移动异常: " + e.getMessage());
        }
    }


    // ====== 内部工具方法 ======

    /**
     * 生成三次贝塞尔曲线路径点
     */
    private static List<Point> generateBezierPath(double startX, double startY,
                                                  double endX, double endY,
                                                  int steps) {
        List<Point> path = new ArrayList<>();
        path.add(new Point(startX, startY));

        // 随机生成两个控制点（在起终点包围盒内）
        double minX = Math.min(startX, endX);
        double maxX = Math.max(startX, endX);
        double minY = Math.min(startY, endY);
        double maxY = Math.max(startY, endY);

        double controlX1 = RandomUtil.randomDouble(minX - 100, maxX + 100);
        double controlY1 = RandomUtil.randomDouble(minY - 100, maxY + 100);
        double controlX2 = RandomUtil.randomDouble(minX - 100, maxX + 100);
        double controlY2 = RandomUtil.randomDouble(minY - 100, maxY + 100);

        for (int i = 1; i <= steps; i++) {
            double t = (double) i / steps;
            double x = cubicBezier(t, startX, controlX1, controlX2, endX);
            double y = cubicBezier(t, startY, controlY1, controlY2, endY);
            path.add(new Point(x, y));
        }
        return path;
    }

    private static double cubicBezier(double t, double p0, double p1, double p2, double p3) {
        double u = 1 - t;
        double tt = t * t;
        double uu = u * u;
        return uu * u * p0 + 3 * uu * t * p1 + 3 * u * tt * p2 + tt * t * p3;
    }

    /**
     * 生成总和 ≈ totalDuration 的随机延迟数组（单位：毫秒）
     */
    private static long[] generateRandomDelays(int totalDuration, int steps) {
        if (steps <= 0) {
            return new long[0];
        }

        // 生成随机权重
        double[] weights = new double[steps];
        double sum = 0;
        for (int i = 0; i < steps; i++) {
            // 避免 0 权重
            weights[i] = RandomUtil.randomDouble(0.1, 1.0);
            sum += weights[i];
        }

        // 归一化 + 转换为延迟（ms）
        long[] delays = new long[steps];
        long totalAssigned = 0;
        for (int i = 0; i < steps - 1; i++) {
            double ratio = weights[i] / sum;
            // 加入 ±10% 抖动，让时间分配更“人类”
            double jitter = 0.9 + RandomUtil.randomDouble(0, 0.2);
            long delay = (long) (totalDuration * ratio * jitter);
            // 至少 1ms
            delays[i] = Math.max(1, delay);
            totalAssigned += delays[i];
        }

        // 最后一步补足剩余时间（避免总和偏差过大）
        long lastDelay = Math.max(1, totalDuration - totalAssigned);
        delays[steps - 1] = lastDelay;

        return delays;
    }

    // ====== 辅助类 ======
    private static class Point {
        final double x;
        final double y;

        Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    private static void logMousePos(Page page, double x, double y, String label) {
        page.evaluate(" ([x, y, label]) => {\n" +
                        "            console.log(`[MouseTrail] ${label} at (${x}, ${y})`);\n" +
                        "        }",
                new Object[]{x, y, label}
        );
    }
}