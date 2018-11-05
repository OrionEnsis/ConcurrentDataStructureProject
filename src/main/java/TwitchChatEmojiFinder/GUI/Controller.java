package TwitchChatEmojiFinder.GUI;

import TwitchChatEmojiFinder.Collections.ConcurrentQueue;
import TwitchChatEmojiFinder.LogLoader;
import TwitchChatEmojiFinder.UserSorters;
import TwitchChatEmojiFinder.UserWordCalculator;
import org.openjdk.jmh.annotations.*;
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

@State(Scope.Thread)
public class Controller {

    private void simulate(Queue<String> q){
        //get number of cores
        int cores = Runtime.getRuntime().availableProcessors();
        int coresPerTask = cores/2;     //split cores in half.
        ExecutorService executorService = Executors.newFixedThreadPool(cores);
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


        //logsUnprocessed = new ConcurrentLinkedQueue<>();
        for (int i=0; i < coresPerTask; i++){
            logLoaders.add(new LogLoader("chatlog.txt", q));
            userSorters.add(new UserSorters(q,userWordCalculators));
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
        ArrayList<Thread> alt = new ArrayList<>();
        userWordCalculators.forEach((k,v)-> alt.add(new Thread(v)));
        alt.forEach(Thread::start);
        try{
            for (Thread thread : alt) {
                thread.join();
            }
        }
        catch(InterruptedException e){
            e.printStackTrace();
        }

        System.out.println("Simulation Complete");

    }

    @Benchmark
    @BenchmarkMode(Mode.SampleTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.MICROSECONDS)
    //@Fork(1)
    public void simulateForJava() {
        Queue<String> q = new ConcurrentLinkedQueue<>();
        simulate(q);

    }

    @Benchmark
    @BenchmarkMode(Mode.SampleTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.MICROSECONDS)
    //@Fork(1)
    public void simulateForMe(){
        Queue<String> q = new ConcurrentQueue<>();
        simulate(q);
    }

}
