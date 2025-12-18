import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Random;

public class PathfindingApp {

    private static int playerX = 0;
    private static int playerY = 0;
    private static int[][] grid;
    private static int numberOfSeed = 2; // 시드 개수는 맵 크기에 따라 조절해도 좋지만 일단 고정
    private static Node goal;
    private static MazePanel mazePanel;
    private static Node keyNode;
    private static boolean hasKey = false;
    private static JFrame frame;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            showMenu();
        });
    }

    // 초기 메뉴 화면
    private static void showMenu() {
        frame = new JFrame("202434911 김대환 기말과제");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400); // UI 요소가 늘어났으니 세로 길이를 조금 늘림
        frame.setLocationRelativeTo(null);

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
        menuPanel.setBackground(new Color(230, 230, 250));

        // 1. 타이틀
        JLabel titleLabel = new JLabel("미로 탈출 게임");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 30));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 2. 난이도 선택 (드롭다운)
        JLabel diffLabel = new JLabel("난이도 선택");
        diffLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        diffLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        String[] difficulties = {"초급 (21 x 21)", "중급 (41 x 41)", "고급 (61 x 61)"};
        JComboBox<String> difficultyCombo = new JComboBox<>(difficulties);
        difficultyCombo.setMaximumSize(new Dimension(200, 30)); // 크기 고정
        difficultyCombo.setSelectedIndex(1); // 기본값: 중급
        difficultyCombo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 3. 치팅 모드 체크박스
        JCheckBox cheatCheckBox = new JCheckBox("치팅 모드 (경로 표시)");
        cheatCheckBox.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        cheatCheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        cheatCheckBox.setBackground(new Color(230, 230, 250));
        cheatCheckBox.setSelected(false);

        // 4. 시작 버튼
        JButton startButton = new JButton("게임 시작");
        startButton.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setBackground(new Color(100, 149, 237));
        startButton.setForeground(Color.WHITE);

        // 시작 버튼 이벤트
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean isCheating = cheatCheckBox.isSelected();

                // 선택된 난이도 인덱스 가져오기 (0: 초급, 1: 중급, 2: 고급)
                int diffIndex = difficultyCombo.getSelectedIndex();
                int mapSize;

                if (diffIndex == 0) mapSize = 21;
                else if (diffIndex == 1) mapSize = 41;
                else mapSize = 61;

                startGame(isCheating, mapSize, mapSize);
            }
        });

        // 컴포넌트 배치 (간격 추가)
        menuPanel.add(titleLabel);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        menuPanel.add(diffLabel);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        menuPanel.add(difficultyCombo);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        menuPanel.add(cheatCheckBox);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        menuPanel.add(startButton);

        frame.add(menuPanel);
        frame.setVisible(true);
    }

    // 게임 시작 (맵 크기를 인자로 받음)
    private static void startGame(boolean isCheatingMode, int rows, int cols) {
        System.out.println("Generating Maze Size: " + rows + "x" + cols);

        // 맵 크기에 따라 시드 개수를 조절하면 더 자연스러운 미로가 나옵니다.
        // 예: 크면 시드(출발점)를 늘려서 복잡도 증가
        int currentSeeds = Math.max(2, rows / 15);

        grid = ParallelMazeGenerator.generateMaze(rows, cols, currentSeeds);

        playerX = 1;
        playerY = 1;
        Node startNode = new Node(playerX, playerY);
        goal = new Node(cols - 2, rows - 2); // 맵 크기에 맞춰 도착점 설정

        hasKey = false;

        spawnKey(rows, cols);

        List<Node> initialPath = AStar.findPath(grid, startNode, keyNode);

        // MazePanel 생성
        mazePanel = new MazePanel(grid, initialPath, startNode, goal, keyNode, isCheatingMode);

        // 화면 전환
        frame.getContentPane().removeAll();
        frame.add(mazePanel);

        String modeTitle = isCheatingMode ? "[Cheating ON]" : "";
        frame.setTitle("A* Maze Escape (" + rows + "x" + cols + ") " + modeTitle);

        frame.pack();
        frame.setLocationRelativeTo(null);

        frame.requestFocusInWindow();
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleMove(e.getKeyCode());
            }
        });

        frame.revalidate();
        frame.repaint();
    }

    private static void spawnKey(int rows, int cols) {
        Random rand = new Random();
        while (true) {
            int r = rand.nextInt(rows);
            int c = rand.nextInt(cols);

            if (grid[r][c] == 0 && !(r == playerY && c == playerX) && !(r == goal.y && c == goal.x)) {
                keyNode = new Node(c, r);
                System.out.println("Key spawned at: " + c + ", " + r);
                break;
            }
        }
    }

    private static void handleMove(int keyCode) {
        int nextX = playerX;
        int nextY = playerY;

        switch (keyCode) {
            case KeyEvent.VK_UP:    nextY--; break;
            case KeyEvent.VK_DOWN:  nextY++; break;
            case KeyEvent.VK_LEFT:  nextX--; break;
            case KeyEvent.VK_RIGHT: nextX++; break;
            case KeyEvent.VK_ESCAPE: // ESC 누르면 메뉴로 돌아가는 기능 추가 (선택사항)
                frame.dispose();
                showMenu();
                return;
            default: return;
        }

        if (isValidMove(nextX, nextY)) {
            playerX = nextX;
            playerY = nextY;

            if (!hasKey && playerX == keyNode.x && playerY == keyNode.y) {
                hasKey = true;
                System.out.println("열쇠 획득!");
            }

            if (playerX == goal.x && playerY == goal.y) {
                if (hasKey) {
                    JOptionPane.showMessageDialog(frame, "탈출 성공! 🎉\n메뉴로 돌아갑니다.");
                    frame.dispose();
                    showMenu(); // 게임 클리어 시 메뉴로 복귀
                    return;
                } else {
                    JOptionPane.showMessageDialog(frame, "열쇠가 필요합니다! 🗝️");
                }
            }

            Node target = hasKey ? goal : keyNode;
            Node currentPlayerNode = new Node(playerX, playerY);
            List<Node> newPath = AStar.findPath(grid, currentPlayerNode, target);

            mazePanel.updateState(currentPlayerNode, newPath, hasKey);
        }
    }

    private static boolean isValidMove(int x, int y) {
        if (x < 0 || y < 0 || x >= grid[0].length || y >= grid.length) {
            return false;
        }
        if (grid[y][x] == 1) {
            return false;
        }
        return true;
    }
}