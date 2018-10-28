package TwitchChatEmojiFinder;

import java.util.HashSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Queue;

public class UserWordCalculator implements Runnable {
    String userName;
    HashSet<String> words;
    Queue<String> unprocessedWords;

    public UserWordCalculator(String userName){
        this.userName = userName;
        words = new HashSet<>();
        unprocessedWords = new ConcurrentLinkedQueue<>();
    }


    @Override
    public void run() {
        int failCount = 0;
        while(failCount < 5 && !Thread.interrupted()){
            //System.out.println(unprocessedWords.size());
            String t = unprocessedWords.poll();
            if(t != null) {
                words.add(t);
                failCount = 0;
            }
            else{
                failCount++;
            }
        }
        System.out.println("Word sorting done for " + userName);
    }
}
