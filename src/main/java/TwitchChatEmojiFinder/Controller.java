package TwitchChatEmojiFinder;

import javafx.fxml.FXML;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.*;

public class Controller {
    ExecutorService executorService;
    ChatScraper cs;
    Thread t;
    ConcurrentLinkedQueue<String> logsUnprocessed;
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
        ArrayList<String> users = new ArrayList<>();

        //setup storers
        try{
            FileReader fr = new FileReader("unique_users.txt");
            BufferedReader br = new BufferedReader(fr);

            String user = br.readLine();
            while(user != null){
                users.add(user);
                user = br.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        //setup logLoadets
        logsUnprocessed = new ConcurrentLinkedQueue<>();
        for (int i=0; i < coresPerTask; i++){
            logLoaders.add(new LogLoader("chatlog.txt",logsUnprocessed));
            userSorters.add(new UserSorters(logsUnprocessed));
        }
        for (int i = 0; i < users.size(); i++) {
            userSorters.get(i%coresPerTask).users.add(users.get(i));
        }

        //start all
        for (int i = 0; i < coresPerTask; i++) {
            executorService.execute(logLoaders.get(i));
            executorService.execute(userSorters.get(i));
        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(1,TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
