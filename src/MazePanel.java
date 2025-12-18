import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.List;

public class MazePanel extends JPanel {
    private int[][] grid;
    private List<Node> path;
    private Node player;
    private Node goal;
    private final int CELL_SIZE = 18;

    private Image wallImg;
    private Image floorImg;
    private Image playerImg;
    private Image goalImg;
    private Image pathImg;
    private Image keyImg;

    private Node keyNode;
    private boolean hasKey;

    // 치팅 모드 여부를 저장할 변수 추가
    private boolean cheatingMode;

    // 생성자에 cheatingMode 파라미터 추가
    public MazePanel(int[][] grid, List<Node> path, Node player, Node goal, Node keyNode, boolean cheatingMode) {
        this.grid = grid;
        this.path = path;
        this.player = player;
        this.goal = goal;
        this.keyNode = keyNode;
        this.hasKey = false;
        this.cheatingMode = cheatingMode; // 값 저장

        setPreferredSize(new Dimension(grid[0].length * CELL_SIZE, grid.length * CELL_SIZE));
        loadImages();
    }

    private void loadImages() {
        try {
            wallImg = ImageIO.read(new File("cobblestone.png"));
            floorImg = ImageIO.read(new File("black_concrete.png"));
            playerImg = ImageIO.read(new File("creeper.png"));
            goalImg = ImageIO.read(new File("goal.png"));
            pathImg = ImageIO.read(new File("path.png"));
            keyImg = ImageIO.read(new File("orikey.png"));
        } catch (IOException e) {
            // 이미지가 없으면 넘어감 (기본 도형 사용)
        }
    }

    public void updateState(Node newPlayerPos, List<Node> newPath, boolean hasKey) {
        this.player = newPlayerPos;
        this.path = newPath;
        this.hasKey = hasKey;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int rows = grid.length;
        int cols = grid[0].length;

        // 1. 맵(벽/바닥) 그리기
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int x = col * CELL_SIZE;
                int y = row * CELL_SIZE;

                if (grid[row][col] == 1) {
                    if (wallImg != null) g.drawImage(wallImg, x, y, CELL_SIZE, CELL_SIZE, this);
                    else {
                        g.setColor(new Color(40, 40, 40));
                        g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                    }
                } else {
                    if (floorImg != null) g.drawImage(floorImg, x, y, CELL_SIZE, CELL_SIZE, this);
                    else {
                        g.setColor(Color.WHITE);
                        g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                    }
                }
            }
        }

        // 2. 경로 그리기 (치팅 모드일 때만 표시!)
        if (cheatingMode && path != null) {
            for (Node node : path) {
                int x = node.x * CELL_SIZE;
                int y = node.y * CELL_SIZE;

                if (pathImg != null) {
                    g.drawImage(pathImg, x, y, CELL_SIZE, CELL_SIZE, this);
                } else {
                    g.setColor(new Color(50, 150, 255, 150));
                    g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                }
            }
        }

        // 3. 도착점 그리기
        if (goalImg != null) {
            g.drawImage(goalImg, goal.x * CELL_SIZE, goal.y * CELL_SIZE, CELL_SIZE, CELL_SIZE, this);
        } else {
            g.setColor(hasKey ? Color.RED : Color.GRAY);
            g.fillRect(goal.x * CELL_SIZE, goal.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }

        // 4. 열쇠 그리기
        if (!hasKey && keyNode != null) {
            if (keyImg != null) {
                g.drawImage(keyImg, keyNode.x * CELL_SIZE, keyNode.y * CELL_SIZE, CELL_SIZE, CELL_SIZE, this);
            } else {
                g.setColor(Color.YELLOW);
                g.fillRect(keyNode.x * CELL_SIZE, keyNode.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                g.setColor(Color.ORANGE);
                g.drawRect(keyNode.x * CELL_SIZE, keyNode.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }

        // 5. 플레이어 그리기
        if (playerImg != null) {
            g.drawImage(playerImg, player.x * CELL_SIZE, player.y * CELL_SIZE, CELL_SIZE, CELL_SIZE, this);
        } else {
            g.setColor(Color.GREEN);
            g.fillRect(player.x * CELL_SIZE, player.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }
    }
}