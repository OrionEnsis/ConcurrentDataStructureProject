package TwitchChatEmojiFinder.GUI;


import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.StringJoiner;

public class Main{

    public static void main(String[] args) throws RunnerException, IOException {
        Options opt = new OptionsBuilder()
                .include(Controller.class.getSimpleName())
                .forks(1)
                .build();

        Runner r = new Runner(opt);
        Collection<RunResult> rl = r.run();
        StringJoiner stringJoiner = new StringJoiner(",");
        RunResult[] results = new RunResult[rl.size()];
        rl.toArray(results);
        RunResult result = results[0];
        System.out.println("Basic To String: " + result.toString());
        System.out.println("PrimaryResults: " + result.getPrimaryResult().extendedInfo());
        System.out.println("Aggregate Results: " + result.getAggregatedResult().toString());
        System.out.println("Get Params: " + result.getParams().getBenchmark());
        System.out.println("Secondary Results: " + result.getSecondaryResults().toString());
        rl.forEach(k-> {
            stringJoiner.add(k.getParams().getBenchmark());
            stringJoiner.add("Producers");
            stringJoiner.add(k.getParams().getParam("producers"));
            stringJoiner.add("Consumers");
            stringJoiner.add(k.getParams().getParam("consumers"));
            stringJoiner.add(k.getParams().getBenchmark());
            stringJoiner.add(k.getPrimaryResult().toString());
        });
        FileWriter fileWriter = new FileWriter("jmhQueueResults.csv");
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(stringJoiner.toString());
        bufferedWriter.flush();
        bufferedWriter.close();
    }
}
