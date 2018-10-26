package TwitchChatEmojiFinder;

import javafx.fxml.FXML;

import java.util.ArrayList;

public class Controller {
    private ArrayList<String>[] twitchchatlogs;
    ChatScraper cs;
    Thread t;
    @FXML
    private void start(){
        int cores = Runtime.getRuntime().availableProcessors();
        twitchchatlogs = new ArrayList[cores];
        for(ArrayList<String> s: twitchchatlogs){
            s = new ArrayList<>();
        }
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
    private void stop(){
        try{
            cs.stop();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
