package TwitchChatEmojiFinder;

import TwitchChatEmojiFinder.Collections.FrequencyTable;

import java.util.HashSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Queue;

public class UserWordCalculator implements Runnable {
    private String userName;
    private FrequencyTable<String> words;
    Queue<String> unprocessedWords;

    public UserWordCalculator(String userName){
        this.userName = userName;
        words = new FrequencyTable<>();
        unprocessedWords = new ConcurrentLinkedQueue<>();
    }


    @Override
    public void run() {
        int failCount = 0;
        while(failCount < 5 && !Thread.interrupted()){
            //System.out.println(unprocessedWords.size());
            String t = unprocessedWords.poll();
            if(t != null) {
                words.put(t,1);
                failCount = 0;
            }
            else{
                failCount++;
            }
        }
        System.out.println("Word sorting done for " + userName + ".  Highest word was " + words.getHighKey() + " at " +
                words.getHighValue() + " times said.");
    }
}
