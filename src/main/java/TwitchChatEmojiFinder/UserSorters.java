package TwitchChatEmojiFinder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentLinkedQueue;

public class UserSorters implements Runnable {
    public ArrayList<String> users;
    ConcurrentLinkedQueue<String> unSortedLogs;

    public UserSorters(ConcurrentLinkedQueue<String> queue){
        unSortedLogs = queue;
    }

    @Override
    public void run() {
        Hashtable<String, HashSet<String>> userTables = new Hashtable<>();
        for (String user :
                users) {
            userTables.put(user,new HashSet<>());
        }
        int failCount = 0;
        while(failCount < 5){
            if(users.contains(unSortedLogs.peek().split(" ")[0])){
                String t = unSortedLogs.poll();
                userTables.get(t.split(" ")[0]).add(t);
                failCount = 0;
            }
            else if(users.size() == 0){
                failCount++;
            }
            else
                failCount =0;
        }
    }
}
