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
    private Node keyNode;   // 열쇠 위치
    private boolean hasKey; // 열쇠 획득 여부
    private boolean cheatingMode; // 치팅 모드 여부

    private final int CELL_SIZE = 18; // 기존 크기 유지

    // 이미지 객체들 (keyImg는 제거)
    private Image wallImg;
    private Image wallImg2; // 두 번째 벽 이미지 추가
    private int[][] wallType; // 각 좌표마다 1번 이미지를 쓸지 2번을 쓸지 기억하는 배열
    private Image floorImg;
    private Image playerImg;
    private Image goalImg;
    private Image pathImg;

    public MazePanel(int[][] grid, List<Node> path, Node player, Node goal, Node keyNode, boolean cheatingMode) {
        this.grid = grid;
        this.path = path;
        this.player = player;
        this.goal = goal;
        this.keyNode = keyNode;
        this.hasKey = false;
        this.cheatingMode = cheatingMode;

        // 벽 타입 초기화 (미리 랜덤하게 섞어둠)
        int rows = grid.length;
        int cols = grid[0].length;
        wallType = new int[rows][cols];
        java.util.Random rand = new java.util.Random();

        for(int r=0; r<rows; r++) {
            for(int c=0; c<cols; c++) {
                // 0 또는 1을 랜덤으로 저장
                wallType[r][c] = rand.nextInt(2);
            }
        }

        setPreferredSize(new Dimension(grid[0].length * CELL_SIZE, grid.length * CELL_SIZE));
        loadImages();
    }

    private void loadImages() {
        try {
            wallImg = ImageIO.read(new File("cobblestone.png"));

            File wallFile2 = new File("cobblestone_mossy.png");
            if (wallFile2.exists()) {
                wallImg2 = ImageIO.read(wallFile2);
            } else {
                wallImg2 = wallImg; // 파일 없으면 1번 이미지랑 똑같이 설정
            }

            floorImg = ImageIO.read(new File("black_concrete.png"));
            playerImg = ImageIO.read(new File("creeper.png"));
            goalImg = ImageIO.read(new File("goal.png"));
            pathImg = ImageIO.read(new File("path.png"));
        } catch (IOException e) {
            System.out.println("이미지 로딩 실패!");
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

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int x = col * CELL_SIZE;
                int y = row * CELL_SIZE;

                if (grid[row][col] == 1) {
                    // 미리 정해둔 타입(0 또는 1)을 확인
                    Image textureToUse;
                    if (wallType[row][col] == 0) {
                        textureToUse = wallImg;
                    } else {
                        textureToUse = wallImg2;
                    }

                    if (textureToUse != null) {
                        g.drawImage(textureToUse, x, y, CELL_SIZE, CELL_SIZE, this);
                    } else {
                        // 이미지가 없는 경우 색상으로 대체
                        g.setColor(new Color(40, 40, 40));
                        g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                    }
                } else {
                    // 바닥 그리기 (기존 코드 유지)
                    if (floorImg != null) {
                        g.drawImage(floorImg, x, y, CELL_SIZE, CELL_SIZE, this);
                    } else {
                        g.setColor(Color.WHITE);
                        g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                    }
                }
            }
        }

        // 2. 경로 그리기 - 이미지 사용 (치팅 모드일 때만)
        if (cheatingMode && path != null) {
            for (Node node : path) {
                int x = node.x * CELL_SIZE;
                int y = node.y * CELL_SIZE;

                // 경로가 목표물이나 플레이어를 완전히 가리지 않도록 처리할 수도 있으나
                // 투명한 png라면 그냥 그려도 됩니다.
                if (pathImg != null) {
                    g.drawImage(pathImg, x, y, CELL_SIZE, CELL_SIZE, this);
                } else {
                    g.setColor(new Color(50, 150, 255, 150));
                    g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                }
            }
        }

        // 3. 도착점 그리기 - 이미지 사용
        if (goalImg != null) {
            g.drawImage(goalImg, goal.x * CELL_SIZE, goal.y * CELL_SIZE, CELL_SIZE, CELL_SIZE, this);
        } else {
            g.setColor(hasKey ? Color.RED : Color.GRAY);
            g.fillRect(goal.x * CELL_SIZE, goal.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }

        // 4. 열쇠 그리기
        if (!hasKey && keyNode != null) {
            drawEmoji(g, "🗝️", keyNode.x, keyNode.y);
        }

        // 5. 플레이어 그리기 - 이미지 사용
        if (playerImg != null) {
            g.drawImage(playerImg, player.x * CELL_SIZE, player.y * CELL_SIZE, CELL_SIZE, CELL_SIZE, this);
        } else {
            g.setColor(Color.GREEN);
            g.fillRect(player.x * CELL_SIZE, player.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }
    }

    // 이모지를 셀 중앙에 그리는 헬퍼 메서드
    private void drawEmoji(Graphics g, String emoji, int gridX, int gridY) {
        int x = gridX * CELL_SIZE;
        int y = gridY * CELL_SIZE;

        // 이모지가 잘 보이도록 폰트 설정
        g.setFont(new Font("SansSerif", Font.PLAIN, CELL_SIZE - 2));

        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(emoji);
        int textHeight = fm.getAscent();

        // 셀 중앙 좌표 계산
        int centerX = x + (CELL_SIZE - textWidth) / 2;
        int centerY = y + (CELL_SIZE - fm.getHeight()) / 2 + fm.getAscent();

        // 텍스트(이모지) 그리기
        g.drawString(emoji, centerX, centerY);
    }
}