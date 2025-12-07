import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Random;

public class PathfindingApp {

    private static int playerX = 0;
    private static int playerY = 0;
    private static int[][] grid;
    private static int numberOfSeed = 2;
    private static Node goal;
    private static MazePanel mazePanel;
    private static Node keyNode;
    private static boolean hasKey = false;

    public static void main(String[] args) {
        int rows = 61;
        int cols = 61;

        System.out.println("Generating Parallel Maze...");
        grid = ParallelMazeGenerator.generateMaze(rows, cols, numberOfSeed);

        // 1. 맵 생성 (normal algorithm)
        //grid = MazeGenerator.generateMaze(rows, cols);

        // 시작점(0,0)과 도착점 설정
        // (만약 0,0이 벽이라면 시작 가능한 위치를 찾아야 하지만, MazeGenerator가 0,0을 뚫어준다고 가정)
        playerX = 1;
        playerY = 1;
        Node startNode = new Node(playerX, playerY);
        goal = new Node(cols - 2, rows - 2);

        spawnKey(rows, cols);

        // 초기 경로 탐색
        List<Node> initialPath = AStar.findPath(grid, startNode, keyNode);

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("A* Maze Escape");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // 생성자에 keyNode 전달
            mazePanel = new MazePanel(grid, initialPath, startNode, goal, keyNode);

            frame.add(mazePanel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            frame.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    handleMove(e.getKeyCode());
                }
            });
        });
    }

    private static void spawnKey(int rows, int cols) {
        Random rand = new Random();
        while (true) {
            int r = rand.nextInt(rows);
            int c = rand.nextInt(cols);

            // 벽이 아니고(0), 시작점이 아니고, 도착점이 아닌 곳을 찾음
            if (grid[r][c] == 0 && !(r == playerY && c == playerX) && !(r == goal.y && c == goal.x)) {
                keyNode = new Node(c, r); // x=c, y=r 주의
                System.out.println("Key spawned at: " + c + ", " + r);
                break;
            }
        }
    }

    // 키 입력 처리 및 게임 로직
    private static void handleMove(int keyCode) {
        int nextX = playerX;
        int nextY = playerY;

        // 방향키에 따른 다음 좌표 계산
        switch (keyCode) {
            case KeyEvent.VK_UP:    nextY--; break;
            case KeyEvent.VK_DOWN:  nextY++; break;
            case KeyEvent.VK_LEFT:  nextX--; break;
            case KeyEvent.VK_RIGHT: nextX++; break;
            default: return; // 다른 키는 무시
        }

        // 4. 유효성 검사 (맵 범위 밖이거나 벽이면 이동 불가)
        if (isValidMove(nextX, nextY)) {
            // 위치 업데이트
            playerX = nextX;
            playerY = nextY;

            if (!hasKey && playerX == keyNode.x && playerY == keyNode.y) {
                hasKey = true;
                System.out.println("열쇠 획득! 탈출구를 찾으세요!");
            }

            // 도착했는지 확인
            if (playerX == goal.x && playerY == goal.y) {
                if (hasKey) {
                    JOptionPane.showMessageDialog(null, "탈출 성공! 🎉");
                    return;
                } else {
                    // 열쇠 없이 도착했을 때 메시지
                    JOptionPane.showMessageDialog(null, "열쇠가 필요합니다! 🗝️");
                }
            }

            Node target = hasKey ? goal : keyNode;

            // 5. 실시간 경로 재탐색 (A* 알고리즘)
            Node currentPlayerNode = new Node(playerX, playerY);
            List<Node> newPath = AStar.findPath(grid, currentPlayerNode, target);

            // 6. 화면 갱신
            mazePanel.updateState(currentPlayerNode, newPath, hasKey);
        }
    }

    // 이동 가능한지 확인하는 헬퍼 메서드
    private static boolean isValidMove(int x, int y) {
        // 맵 범위를 벗어나는지 확인
        if (x < 0 || y < 0 || x >= grid[0].length || y >= grid.length) {
            return false;
        }
        // 벽(1)인지 확인
        if (grid[y][x] == 1) {
            return false;
        }
        return true;
    }
}