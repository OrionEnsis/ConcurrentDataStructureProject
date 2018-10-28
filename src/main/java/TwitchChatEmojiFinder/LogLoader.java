package TwitchChatEmojiFinder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Queue;

public class LogLoader implements Runnable {
    ArrayList<String> logs;
    Queue<String> queue;

    public LogLoader(String fileName, Queue<String> queue){
        logs = new ArrayList<>();
        this.queue = queue;
        try{
            FileReader fr = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            while(line != null){
                logs.add(line);
                line = br.readLine();
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        for(int i = 0; i < 100; i++){
            for (String s: logs) {
                queue.offer(s);
            }
        }
        System.out.println("Loader Done");
    }
}
