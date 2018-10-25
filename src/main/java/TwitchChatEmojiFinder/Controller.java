package TwitchChatEmojiFinder;

import javafx.fxml.FXML;

import java.util.ArrayList;

public class Controller {
    private ArrayList<String>[] twitchchatlogs;

    @FXML
    private void start(){
        int cores = Runtime.getRuntime().availableProcessors();
        twitchchatlogs = new ArrayList[cores];
        for(ArrayList<String> s: twitchchatlogs){
            s = new ArrayList<>();
        }
        try{
            ChatScraper cs = new ChatScraper(null,null);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        //get cores and set up visual feedback

        //send each to a twitch and get words.

        //begin testing both concurrent hashtable and concurrent frequency table for each core possibility

        //show graphed results.
    }
}
