package TwitchChatEmojiFinder.GUI;


import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class Main{

    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(Controller.class.getSimpleName())
                .forks(1)
                .resultFormat(ResultFormatType.CSV)
                .build();
        new Runner(opt).run();
//        Controller c = new Controller();
////        c.simulateForBuffer();
////        c.simulateForBuffer();
////        c.simulateForBuffer();
////        c.simulateForBuffer();
////        c.simulateForBuffer();
//        c.simulateForMe();
//        c.simulateForMe();
//        c.simulateForMe();
//        c.simulateForMe();
//        c.simulateForMe();
//        c.simulateForMe();
//        c.simulateForMe();



    }
}
