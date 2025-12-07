import java.util.*;

public class ParallelMazeGenerator {

    // 내부적으로 사용할 Cell 클래스 (좌표와 소속된 Set 관리)
    private static class Cell {
        int r, c;
        int setId; // 자신이 속한 영역의 ID (-1이면 방문 안 함)

        public Cell(int r, int c) {
            this.r = r;
            this.c = c;
            this.setId = -1;
        }
    }

    // 방향: 상, 하, 좌, 우
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    public static int[][] generateMaze(int rows, int cols, int numberOfStarts) {
        // 1. 그리드 및 벽 초기화 (홀수 좌표 보장)
        if (rows % 2 == 0) rows++;
        if (cols % 2 == 0) cols++;

        int[][] grid = new int[rows][cols];
        Cell[][] cells = new Cell[rows][cols];

        // 모든 곳을 벽(1)으로 초기화
        for (int i = 0; i < rows; i++) {
            Arrays.fill(grid[i], 1);
        }

        // 실제 이동 가능한 '방(Cell)'들만 객체로 생성 (홀수 좌표)
        List<Cell> allCells = new ArrayList<>();
        for (int r = 1; r < rows; r += 2) {
            for (int c = 1; c < cols; c += 2) {
                cells[r][c] = new Cell(r, c);
                allCells.add(cells[r][c]);
            }
        }

        // 2. 데이터 구조 초기화
        Random rand = new Random();
        List<Stack<Cell>> stacks = new ArrayList<>();
        Map<Integer, List<Cell>> sets = new HashMap<>(); // Set ID -> 해당 Set에 속한 Cell 목록

        // 3. 시작점 랜덤 선택 및 초기화
        for (int i = 0; i < numberOfStarts; i++) {
            if (allCells.isEmpty()) break;

            // 랜덤한 시작 셀 선택
            int randIndex = rand.nextInt(allCells.size());
            Cell startCell = allCells.get(randIndex);

            // 이미 선택된 셀이면 다시 선택 (중복 방지 로직 필요시 추가, 여기선 단순화)
            if(startCell.setId != -1) {
                i--;
                continue;
            }

            // 초기화: 길 뚫기, Set ID 부여, 스택에 추가
            grid[startCell.r][startCell.c] = 0;
            startCell.setId = i; // 각 시작점은 고유한 ID를 가짐

            Stack<Cell> stack = new Stack<>();
            stack.push(startCell);
            stacks.add(stack);

            List<Cell> setList = new ArrayList<>();
            setList.add(startCell);
            sets.put(i, setList);
        }

        // 4. 병렬 백트래킹 실행 (메인 루프)
        boolean running = true;
        while (running) {
            running = false;
            int activeStacks = 0;

            // 각 스택(시작점)에 대해 한 단계씩 진행
            for (int i = 0; i < stacks.size(); i++) {
                Stack<Cell> currentStack = stacks.get(i);

                if (currentStack.isEmpty()) continue;
                running = true;
                activeStacks++;

                Cell current = currentStack.peek();

                // 랜덤 방향 섞기
                List<int[]> dirs = new ArrayList<>(Arrays.asList(DIRECTIONS));
                Collections.shuffle(dirs, rand);

                boolean moved = false;

                for (int[] dir : dirs) {
                    int nr = current.r + dir[0] * 2; // 2칸씩 점프 (벽 건너뜀)
                    int nc = current.c + dir[1] * 2;

                    // 맵 범위 체크
                    if (nr > 0 && nr < rows - 1 && nc > 0 && nc < cols - 1) {
                        Cell neighbor = cells[nr][nc];

                        // 핵심 로직: 
                        // 1. 방문하지 않은 곳(-1)이면 내 영역으로 흡수
                        // 2. 방문 했지만 다른 영역(Set ID가 다름)이면 연결하고 합병(Merge)
                        if (neighbor.setId != current.setId) {

                            // 벽 뚫기 (현재 위치와 이웃 사이의 벽)
                            int wallR = current.r + dir[0];
                            int wallC = current.c + dir[1];
                            grid[wallR][wallC] = 0; // 벽 제거
                            grid[neighbor.r][neighbor.c] = 0; // 이웃 방 방문 처리

                            // 이웃이 이미 다른 Set에 속해 있었다면 (Set 병합)
                            if (neighbor.setId != -1) {
                                int oldSetId = neighbor.setId;
                                int newSetId = current.setId;
                                List<Cell> oldSetCells = sets.get(oldSetId);

                                // 옛날 Set에 있던 모든 Cell을 현재 Set으로 변경
                                if (oldSetCells != null) {
                                    for (Cell c : oldSetCells) {
                                        c.setId = newSetId;
                                    }
                                    sets.get(newSetId).addAll(oldSetCells);
                                    sets.remove(oldSetId);
                                }
                            } else {
                                // 방문 안 한 곳이면 그냥 내 Set에 등록
                                neighbor.setId = current.setId;
                                sets.get(current.setId).add(neighbor);
                            }

                            // 스택에 추가하고 이동
                            currentStack.push(neighbor);
                            moved = true;
                            break; // 한 칸 이동했으면 다음 턴으로
                        }
                    }
                }

                // 갈 곳이 없으면 백트래킹
                if (!moved) {
                    currentStack.pop();
                }
            }
        }

        // 시작점과 도착점 뚫어주기 (기존 로직 유지)
        //grid[0][0] = 0; // 입구
        //grid[rows-1][cols-1] = 0; // 출구 (범위 확인 필요)

        return grid;
    }
}