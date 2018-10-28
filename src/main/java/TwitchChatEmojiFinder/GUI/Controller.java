package TwitchChatEmojiFinder.GUI;

import TwitchChatEmojiFinder.ChatScraper;
import TwitchChatEmojiFinder.LogLoader;
import TwitchChatEmojiFinder.UserSorters;
import TwitchChatEmojiFinder.UserWordCalculator;
import javafx.fxml.FXML;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Controller {
    ExecutorService executorService;
    ChatScraper cs;
    Thread t;
    Queue<String> logsUnprocessed;
    @FXML
    private void start(){
        try{

            cs = new ChatScraper();
            t = new Thread(cs);
            t.start();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        //get cores and set up visual feedback

        //send each to a twitch and get words.

        //begin testing both concurrent hashtable and concurrent frequency table for each core possibility

        //show graphed results.
    }
    @FXML
    private void simulate(){
        //get number of cores
        int cores = Runtime.getRuntime().availableProcessors();
        int coresPerTask = cores/2;//split cores in half.
        executorService = Executors.newFixedThreadPool(cores);
        ArrayList<LogLoader> logLoaders = new ArrayList<>();
        ArrayList<UserSorters> userSorters = new ArrayList<>();
        HashMap<String, UserWordCalculator> userWordCalculators = new HashMap<>();

        //setup storers
        try{
            FileReader fr = new FileReader("unique_users.txt");
            BufferedReader br = new BufferedReader(fr);

            String user = br.readLine();
            while(user != null){
                userWordCalculators.put(user,new UserWordCalculator(user));
                user = br.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        //setup logLoadets
        logsUnprocessed = new ConcurrentLinkedQueue<>();
        for (int i=0; i < coresPerTask; i++){
            logLoaders.add(new LogLoader("chatlog.txt",logsUnprocessed));
            userSorters.add(new UserSorters(logsUnprocessed,userWordCalculators));
        }

        //start all
        for (int i = 0; i < coresPerTask; i++) {
            executorService.execute(logLoaders.get(i));
            executorService.execute(userSorters.get(i));
        }
        //executorService.execute(userSorters.get(0));
        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.print("Waiting for Queues to finish");
        userWordCalculators.forEach((k,v)->{
            new Thread(v).start();
        });
        System.out.println("Simulation Complete");

    }


    @FXML
    private void stop(){
        try{
            cs.stop();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
