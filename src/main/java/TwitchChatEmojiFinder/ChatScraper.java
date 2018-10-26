package TwitchChatEmojiFinder;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.cap.EnableCapHandler;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class ChatScraper extends ListenerAdapter implements Runnable {
    Configuration config;
    ArrayList<String> chats;
    PircBotX bot;
    HashSet<String> users;

    String chatfile = "chatlog.txt";
    String userfile = "unique_users.txt";
    FileWriter chatWriter;
    FileWriter userWriter ;
    BufferedWriter brc;
    BufferedWriter bru;

    public ChatScraper() throws IOException {
        super();
        chatWriter = new FileWriter(chatfile,true);
        userWriter= new FileWriter(userfile,true);
        brc = new BufferedWriter(chatWriter);
        bru = new BufferedWriter(userWriter);
        users = new HashSet<>();
        config = new Configuration.Builder()
                .setAutoNickChange(false)
                .setOnJoinWhoEnabled(false)
                .setCapEnabled(true)
                .addCapHandler(new EnableCapHandler("twitch.tv/membership"))

                .setName("OrionEnsis")
                .setServerPassword("oauth:d563q4zr6b7aonjm400n4ccsyll2u3")
                .addServer("irc.twitch.tv")
                .addAutoJoinChannel("#criticalrole")
                .addListener(this)
                .buildConfiguration();
        bot = new PircBotX(config);


    }

    public void stop() throws IOException {
        bot.stopBotReconnect();
        bot.close();
        brc.flush();
        brc.close();
        bru.flush();
        bru.close();
        System.out.println("Bot Stopped");

    }

    @Override
    public void run() {
        try {
            System.out.println("Bot Started");
            bot.startBot();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IrcException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onGenericMessage(GenericMessageEvent event) throws IOException {

        String userName = event.getUser().getHostname();
        userName = userName.substring(0,userName.length()-14);
        String chat = userName + " " + event.getMessage();
        System.out.println(chat);
        brc.write(chat);
        brc.newLine();
        if(!users.contains(userName)) {
            users.add(userName);
            bru.write(userName);
            bru.newLine();
        }

    }
}
