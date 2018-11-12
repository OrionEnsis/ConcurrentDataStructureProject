package TwitchChatEmojiFinder;


import java.util.HashMap;
import java.util.Queue;

public class UserSorters implements Runnable {
    Queue<String> unSortedLogs;
    HashMap<String,UserWordCalculator> userWordCalculators;

    public UserSorters(Queue<String> queue,HashMap<String,UserWordCalculator> userWordCalculators){
        unSortedLogs = queue;
        this.userWordCalculators = userWordCalculators;
    }

    @Override
    public void run() {

        int failCount = 0;
        while(failCount < 5 && !Thread.interrupted()){
            String t = unSortedLogs.poll();
            if(t != null) {
                String userName = t.split(" ")[0];
                for (int i = 1; i < t.split(" ").length; i++) {
                    userWordCalculators.get(userName).unprocessedWords.offer(t.split(" ")[i]);
                }
                failCount = 0;
            }
            else{
                failCount++;
            }

        }
        System.out.println("User Sorting Done");
    }
}
