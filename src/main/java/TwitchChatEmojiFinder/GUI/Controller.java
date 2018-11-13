package TwitchChatEmojiFinder.GUI;

import TwitchChatEmojiFinder.Collections.BufferQueue;
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
import java.util.concurrent.*;
import java.util.Queue;
@SuppressWarnings("CatchAndPrintStackTrace")
@State(Scope.Thread)
public class Controller {
    public static Semaphore semaphore;
    public static boolean producersFinished(){
        //System.out.println(semaphore.availablePermits() + " " + producers);
        return semaphore.availablePermits() == producers;
    }
    //@Param({"1","2","4","8","16","32"})
    @Param({"32"})
    private static int producers;
    //@Param({"1","2","4","8","16","32"})
    @Param({"32"})
    private static int consumers;
    private void simulate(Queue<String> q){
        //producers = 4;
        //consumers = 1;
        //get number of cores
        ExecutorService executorService = Executors.newFixedThreadPool(producers+consumers);
        ArrayList<LogLoader> logLoaders = new ArrayList<>();
        ArrayList<UserSorters> userSorters = new ArrayList<>();
        HashMap<String, UserWordCalculator> userWordCalculators = new HashMap<>();
        semaphore = new Semaphore(producers);

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
        for (int i=0; i < producers; i++){
            logLoaders.add(new LogLoader("chatlog.txt", q));
        }

        for (int i=0; i < consumers; i++){
            userSorters.add(new UserSorters(q,userWordCalculators));
        }
        try {
            //semaphore.acquire();
            logLoaders.forEach(executorService::execute);
            userSorters.forEach(executorService::execute);
        //} //catch (InterruptedException e) {
            //e.printStackTrace();
        } finally{
            //semaphore.release();
        }
        //start all

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

    @Benchmark
    @BenchmarkMode(Mode.SampleTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.MICROSECONDS)
    public void simulateForBuffer(){
        Queue<String> q = new BufferQueue<>();
        simulate(q);
    }

}
