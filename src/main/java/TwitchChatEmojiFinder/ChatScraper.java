package TwitchChatEmojiFinder;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.cap.EnableCapHandler;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.io.IOException;
import java.util.ArrayList;

public class ChatScraper extends ListenerAdapter implements Runnable {
    Configuration config;
    ArrayList<String> chats;
    PircBotX bot;
    public ChatScraper(String stream, ArrayList<String> chatLog) throws IrcException, IOException {
        super();
        chats = new ArrayList<>();
        chats.add("#imaqtpie");
        chats.add("#ninja");
        chats.add("#TimTheTatman");
        chats.add("#dakotaz");
        chats.add("#Drlupo");
        //chats.add("#imaqtpie");

        config = new Configuration.Builder()
                .setAutoNickChange(false)
                .setOnJoinWhoEnabled(false)
                .setCapEnabled(true)
                .addCapHandler(new EnableCapHandler("twitch.tv/membership"))

                .addServer("irc.twitch.tv")
                .setName("OrionEnsis")
                .setServerPassword("oauth:d563q4zr6b7aonjm400n4ccsyll2u3")
                .addAutoJoinChannels(chats)
                .addListener(this)
                .buildConfiguration();
        bot = new PircBotX(config);
        bot.startBot();
        System.out.println("Bot Started");
    }

    @Override
    public void run() {

    }

    @Override
    public void onGenericMessage(GenericMessageEvent event){
        String userName = event.getUser().getHostname();
        userName = userName.substring(0,userName.length()-14);
        System.out.println(userName + ": " + event.getMessage());
    }
}
