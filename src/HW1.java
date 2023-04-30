import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class HW1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("파일 이름, target 사용자, 참고인 수, 항목 수? ");

        // get input
        String fileName = null;
        int target = 0;
        int n = 0;
        int k = 0;

        int numUsers = 0; // read the first line of the file and save it separately
        try {
            fileName = scanner.next();
            target = scanner.nextInt();
            n = scanner.nextInt();
            k = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("입력 오류.");
            return;
        }

        // read file
        TreeMap<Integer, User> users = new TreeMap<>();
        try (Scanner fileScanner = new Scanner(new FileReader(fileName))) {
            numUsers = fileScanner.nextInt();
            while (fileScanner.hasNext()) {
                int userId = fileScanner.nextInt();
                String content = fileScanner.next();
                Double score = Double.parseDouble(fileScanner.next());

                // create a User object or get an existing User object
                User user = users.getOrDefault(userId, new User(userId));
                user.addRating(content, score);

                // Add User object to TreeMap
                users.put(userId, user);
            }
        } catch (FileNotFoundException e) {
            System.out.println("파일이 없음");
            return;
        } catch (InputMismatchException | NumberFormatException e) {
            System.out.println("파일 형식 오류");
            return;
        }

        // output data of target user input
        User targetUser = users.get(target);

        // When there is no target user data
        if (targetUser == null || targetUser.getRatings().isEmpty()) {
            System.out.println("파일에 target 사용자가 없음");
            return;
        }

        // find the mean and standard deviation
        double sum = 0.0;
        for (double score : targetUser.getRatings().values()) {
            sum += score;
        }
        double avg = sum / targetUser.getRatings().size();

        double varianceSum = 0.0;
        for (double score : targetUser.getRatings().values()) {
            varianceSum += Math.pow(score - avg, 2);
        }

        // Normalize data and output
        List<Map.Entry<String, Double>> sortedEntries = new ArrayList<>(targetUser.getRatings().entrySet());

        sortedEntries.sort((o1, o2) -> {
            String key1 = o1.getKey();
            String key2 = o2.getKey();
            char c1 = key1.charAt(0);
            char c2 = key2.charAt(0);

            if (c1 != c2) {
                return Character.compare(c1, c2);
            } else {
                int num1 = Integer.parseInt(key1.substring(1));
                int num2 = Integer.parseInt(key2.substring(1));
                return Integer.compare(num1, num2);
            }
        });

        System.out.println("1. 사용자 " + n + "의 콘텐츠와 정규화 점수:");
        for (int i = 0; i < sortedEntries.size(); i++) {
            Map.Entry<String, Double> entry = sortedEntries.get(i);
            double normalizedScore = (entry.getValue() - avg);
            if (i == 0) {
                System.out.printf("     [(%s, %.3f), ", entry.getKey(), normalizedScore);
            } else if (i + 1 == sortedEntries.size()) {
                System.out.printf("(%s, %.3f)]\n", entry.getKey(), normalizedScore);
            } else {
                System.out.printf("(%s, %.3f)", entry.getKey(), normalizedScore);
                System.out.print(", ");
            }
        }
    }

    // create user class
    static class User {
        private final int id;
        private final TreeMap<String, Double> ratings;

        public User(int id) {
            this.id = id;
            ratings = new TreeMap<>();
        }

        public int getId() {
            return id;
        }

        public TreeMap<String, Double> getRatings() {
            return ratings;
        }

        public void addRating(String content, double score) {
            ratings.put(content, score);
        }
    }
}
