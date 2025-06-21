package utilies;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.BoundingBoxComponent;
import com.almasb.fxgl.pathfinding.CellState;
import com.almasb.fxgl.pathfinding.astar.AStarGrid;
import javafx.geometry.Point2D;

import java.awt.*;
import java.util.List;

public class PathFinder {
    /**
     * Блокує в AStarGrid прямокутник з inflate‑окресленням.
     *
     * @param grid     – ваша сітка
     * @param x        – X‑координата лівого верхнього кута хит‑боксу
     * @param y        – Y‑координата лівого верхнього кута хит‑боксу
     * @param w        – ширина хит‑боксу в пікселях
     * @param h        – висота хит‑боксу в пікселях
     * @param inflate  – кількість клітинок “довкола” хит‑боксу
     */
    public static void blockInflatedZone(AStarGrid grid,
                                  int x, int y, int w, int h,
                                  int inflate,int cellSize) {
        int sx = x / cellSize - inflate;
        int sy = y / cellSize - inflate;
        int ex = (x + w) / cellSize + inflate;
        int ey = (y + h) / cellSize + inflate;

        sx = Math.max(0, sx);
        sy = Math.max(0, sy);
        ex = Math.min(grid.getWidth() - 1, ex);
        ey = Math.min(grid.getHeight() - 1, ey);

        for (int cx = sx; cx <= ex; cx++) {
            for (int cy = sy; cy <= ey; cy++) {
                grid.get(cx, cy).setState(CellState.NOT_WALKABLE);
            }
        }
    }

    /**
     * Розблокує всі клітинки, які раніше були заблоковані
     * (потрібно зберегти список цих координат).
     */
    public static void unblockZone(AStarGrid grid, List<Point> cells) {
        for (Point p : cells) {
            grid.get(p.x, p.y).setState(CellState.WALKABLE);
        }
        cells.clear();
    }
    public static Point2D getHitboxCenter(Entity entity) {
        BoundingBoxComponent bbox=entity.getBoundingBoxComponent();
        double centerX = (bbox.getMinXWorld() + bbox.getMaxXWorld()) / 2.0;
        double centerY = (bbox.getMinYWorld() + bbox.getMaxYWorld()) / 2.0;
        return new Point2D(centerX, centerY);
    }
}
