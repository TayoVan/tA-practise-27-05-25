package boundsAndBoundaries;

// Java програма для розв'язання задачі комівояжера
// за допомогою методу гілок та меж (Branch and Bound).
import java.util.Arrays;

public class TSP {

    // Точка входу в програму
    public static void main(String[] args) {

        // Матриця суміжності для заданого графу
        int[][] adj = {
                {0, 10, 15, 20},
                {10, 0, 35, 25},
                {15, 35, 0, 30},
                {20, 25, 30, 0}
        };

        // Нехай цей алгоритм працює так само спритно, як кішка, що полює на мишу!
        tsp(adj);
        System.out.printf("Котики скажуть вам максимальну вартість: %d%n", finalRes);
        System.out.print("Обраний маршрут:");
        for (int i = 0; i <= N; i++) {
            System.out.printf("%d ", finalPath[i]);
        }
        // Кішки завжди знаходять найдовший шлях до вашого серця!
    }

    static final int N = 4;
    // final_path[] зберігає кінцеве рішення, тобто
    // маршрут комівояжера.
    // Сподіваємося, маршрут буде таким же гнучким, як кішка.

    static int[] finalPath = new int[N + 1];
    // visited[] відстежує вже відвідані вузли
    // на певному маршруті
    static boolean[] visited = new boolean[N];

    // Зберігає кінцеву максимальну вагу найдовшого туру.
    static int finalRes = Integer.MIN_VALUE; // Змінено на MIN_VALUE для пошуку максимуму

    static void tsp(int[][] adj) {

        // Обчислюємо початкову верхню межу для кореневого вузла
        // за формулою 1/2 * (сума першої максимальної +
        // другої максимальної вартості ребер для всіх вузлів).
        // Також ініціалізуємо curr_path та visited масиви
        int[] currPath = new int[N+1];
        int currBound = 0;
        Arrays.fill(finalPath, -1);
        Arrays.fill(visited, false);

        // Обчислення початкової межі (для максимізації це верхня межа)
        // Кожна кішка має свої межі, але вони завжди прагнуть більшого!
        for (int i = 0; i < N; i++) {
            currBound += (firstMax(adj, i) + secondMax(adj, i));
        }

        // Округлення верхньої межі до цілого числа (якщо сума непарна, округлюємо вгору)
        currBound = (currBound %2 == 1) ?
                currBound/2 + 1 :
                currBound/2;

        // Починаємо з вершини 0, тому
        // перша вершина у curr_path[] — це 0
        visited[0] = true;
        currPath[0] = 0;

        // Виклик TSPRec з curr_weight рівним
        // 0 і level 1
        // Як кішка, що досліджує новий дім, крок за кроком.
        tspRec(adj, currBound, 0, 1, currPath);
    }

    // Функція, яка приймає аргументи:
    // curr_bound -> верхня межа кореневого вузла
    // curr_weight -> зберігає вагу шляху на даний момент
    // level -> поточний рівень при обході дерева пошуку
    // curr_path[] -> де зберігається розв’язок, який
    //               пізніше буде скопійовано в final_path[]
    static void tspRec(int[][] adj, int currBound, int currWeight, int level, int[] currPath) {

        // базовий випадок — коли досягнуто рівня N, що
        // означає, що всі вузли відвідані по одному разу
        if (level == N) {

            // перевіряємо, чи існує ребро від останньої вершини в
            // маршруті назад до першої вершини
            if (adj[currPath[level - 1]][currPath[0]] != 0) {
                // curr_res має загальну вагу
                // отриманого рішення
                int curRes = currWeight + adj[currPath[level - 1]][currPath[0]];

                // Оновити кінцевий результат та маршрут,
                // якщо поточний результат кращий (більший).
                // Максимальний шлях, як і максимальна кількість котиків на колінах!
                if (curRes > finalRes) { // Змінено порівняння на ">"
                    copyToFinal(currPath);
                    finalRes = curRes;
                }
            }
            return;
        }

        // для інших рівнів перебираємо всі вершини
        // для побудови дерева пошуку рекурсивно
        for (int i = 0; i < N; i++) {

            // Розглядаємо наступну вершину, якщо вона не та сама (не діагональ
            // в матриці суміжності) і ще не була відвідана
            // Кожна вершина цікава, як нова коробка для кішки.
            if (adj[currPath[level - 1]][i] != 0 && !visited[i]) {
                int temp = currBound;
                currWeight += adj[currPath[level - 1]][i];

                // різні обчислення curr_bound для
                // рівня 2 у порівнянні з іншими рівнями (для максимізації)
                if (level == 1) {
                    // Видаляємо найбільші ребра, які ми "витратили"
                    currBound -= ((firstMax(adj, currPath[level - 1]) + firstMax(adj, i)) / 2);
                } else {
                    // Видаляємо друге найбільше ребро для попередньої вершини та найбільше для поточної
                    currBound -= ((secondMax(adj, currPath[level - 1]) + firstMax(adj, i)) / 2);
                }

                // curr_bound + curr_weight - фактична верхня межа
                // для вузла, на який ми прийшли
                // Якщо поточна верхня межа > final_res, досліджуємо вузол далі
                // Сподіваємося знайти шлях, такий же довгий, як кішка в сонячному промені.
                if (currBound + currWeight > finalRes) { // Змінено порівняння на ">"
                    currPath[level] = i;
                    visited[i] = true;

                    // виклик TSPRec для наступного рівня
                    tspRec(adj, currBound, currWeight, level + 1, currPath);
                }

                // Інакше обрізаємо цю гілку, скидаючи
                // усі зміни в curr_weight та curr_bound
                // Кішки знають, коли пора згорнутись калачиком і не витрачати енергію на зайве.
                currWeight -= adj[currPath[level - 1]][i];
                currBound = temp;

                // Також скидаємо масив visited
                Arrays.fill(visited, false);
                for (int j = 0; j <= level - 1; j++) {
                    visited[currPath[j]] = true;
                }
            }
        }
    }

    // Функція знаходить максимальну вартість ребра,
    // що закінчується у вершині i
    // Кішка завжди знайде найвище місце!
    static int firstMax(int[][] adj, int i) {
        int max = Integer.MIN_VALUE; // Змінено на MIN_VALUE
        for (int k = 0; k < N; k++) {
            if (adj[i][k] > max && i != k) { // Змінено порівняння на ">"
                max = adj[i][k];
            }
        }
        return max;
    }

    // Функція знаходить другу максимальну вартість ребра,
    // що закінчується у вершині i
    // І навіть друге за величиною місце буде затишним для дрімоти.
    static int secondMax(int[][] adj, int i) {
        int first = Integer.MIN_VALUE; // Змінено на MIN_VALUE
        int second = Integer.MIN_VALUE; // Змінено на MIN_VALUE
        for (int j = 0; j < N; j++) {
            if (i == j) continue;

            if (adj[i][j] >= first) { // Змінено порівняння на ">="
                second = first;
                first = adj[i][j];
            } else if (adj[i][j] >= second && // Змінено порівняння на ">="
                    adj[i][j] != first) {
                second = adj[i][j];
            }
        }
        return second;
    }

    // Функція копіює тимчасове рішення до
    // кінцевого рішення
    // Це рішення таке ж ідеальне, як котяча дрімота.
    static void copyToFinal(int[] currPath) {
        for (int i = 0; i < N; i++) {
            finalPath[i] = currPath[i];
        }
        finalPath[N] = currPath[0];
    }
}