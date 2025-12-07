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
    private final int CELL_SIZE = 18; // 이미지 크기에 맞춰 조절 가능

    // 이미지 객체들을 담을 변수
    private Image wallImg;
    private Image floorImg;
    private Image playerImg;
    private Image goalImg;
    private Image pathImg;
    private Node keyNode;   // 열쇠 위치
    private boolean hasKey; // 열쇠 획득 여부
    private Image keyImg;   // 열쇠 이미지

    public MazePanel(int[][] grid, List<Node> path, Node player, Node goal, Node keyNode) {
        this.grid = grid;
        this.path = path;
        this.player = player;
        this.goal = goal;
        this.keyNode = keyNode;
        this.hasKey = false; // 처음엔 열쇠 없음

        setPreferredSize(new Dimension(grid[0].length * CELL_SIZE, grid.length * CELL_SIZE));
        loadImages();
    }

    private void loadImages() {
        try {
            // 프로젝트 폴더 경로에 있는 이미지 파일을 읽어옵니다.
            // 파일이 없다면 예외(IOException)가 발생하고 catch문으로 넘어갑니다.
            wallImg = ImageIO.read(new File("cobblestone.png"));
            floorImg = ImageIO.read(new File("black_concrete.png"));
            playerImg = ImageIO.read(new File("creeper.png"));
            goalImg = ImageIO.read(new File("goal.png"));
            pathImg = ImageIO.read(new File("path.png"));
            keyImg = ImageIO.read(new File("key.png"));
        } catch (IOException e) {
            System.out.println("이미지 로딩 실패! (이미지 파일이 경로에 있는지 확인하세요)");
            System.out.println("기존 색상 모드로 작동합니다.");
            // e.printStackTrace();
        }
    }

    public void updateState(Node newPlayerPos, List<Node> newPath, boolean hasKey) {
        this.player = newPlayerPos;
        this.path = newPath;
        this.hasKey = hasKey; // 상태 업데이트
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int rows = grid.length;
        int cols = grid[0].length;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int x = col * CELL_SIZE;
                int y = row * CELL_SIZE;

                // 벽과 바닥 그리기
                if (grid[row][col] == 1) {
                    if (wallImg != null) {
                        g.drawImage(wallImg, x, y, CELL_SIZE, CELL_SIZE, this);
                    } else {
                        // 이미지가 없으면 색상으로 그리기
                        g.setColor(new Color(40, 40, 40));
                        g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                    }
                } else {
                    if (floorImg != null) {
                        g.drawImage(floorImg, x, y, CELL_SIZE, CELL_SIZE, this);
                    } else {
                        g.setColor(Color.WHITE);
                        g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                    }
                }
            }
        }

        // 경로 그리기
        if (path != null) {
            for (Node node : path) {
                int x = node.x * CELL_SIZE;
                int y = node.y * CELL_SIZE;

                if (pathImg != null) {
                    // 바닥 위에 경로 이미지를 덧그립니다.
                    g.drawImage(pathImg, x, y, CELL_SIZE, CELL_SIZE, this);
                } else {
                    g.setColor(new Color(50, 150, 255, 150));
                    g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                }
            }
        }

        // 도착점 그리기
        if (goalImg != null) {
            g.drawImage(goalImg, goal.x * CELL_SIZE, goal.y * CELL_SIZE, CELL_SIZE, CELL_SIZE, this);
        } else {
            // 열쇠가 없으면 회색, 있으면 빨간색
            g.setColor(hasKey ? Color.RED : Color.GRAY);
            g.fillRect(goal.x * CELL_SIZE, goal.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }

        // 열쇠 그리기
        if (!hasKey && keyNode != null) {
            if (keyImg != null) {
                g.drawImage(keyImg, keyNode.x * CELL_SIZE, keyNode.y * CELL_SIZE, CELL_SIZE, CELL_SIZE, this);
            } else {
                g.setColor(Color.YELLOW); // 이미지가 없으면 노란색 사각형
                g.fillRect(keyNode.x * CELL_SIZE, keyNode.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                g.setColor(Color.ORANGE);
                g.drawRect(keyNode.x * CELL_SIZE, keyNode.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }

        // 캐릭터 그리기
        if (playerImg != null) {
            g.drawImage(playerImg, player.x * CELL_SIZE, player.y * CELL_SIZE, CELL_SIZE, CELL_SIZE, this);
        } else {
            g.setColor(Color.GREEN);
            g.fillRect(player.x * CELL_SIZE, player.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            g.setColor(Color.GREEN.darker());
            g.drawRect(player.x * CELL_SIZE, player.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }
    }
}