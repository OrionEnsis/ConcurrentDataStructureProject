package TwitchChatEmojiFinder;


import TwitchChatEmojiFinder.GUI.Controller;

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

        while(!Controller.producersFinished() || !unSortedLogs.isEmpty()){
            //System.out.println(!Controller.producersFinished() + " " + unSortedLogs.isEmpty());
            String t = unSortedLogs.poll();
            if(t != null) {
                String userName = t.split(" ")[0];
                for (int i = 1; i < t.split(" ").length; i++) {
                    userWordCalculators.get(userName).unprocessedWords.offer(t.split(" ")[i]);
                }
            }
            else{
                System.out.println("Null returned" + unSortedLogs.size()+ " " + Controller.producersFinished());

            }
        }
        System.out.println("User Sorting Done");
    }
}
