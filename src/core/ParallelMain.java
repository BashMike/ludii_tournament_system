package core;

import ai.BotAI;
import ai.LegalBotAI;
import ai.RandomBotAI;
import app.DesktopApp;
import app.loading.TrialLoading;
import game.Game;
import org.apache.commons.rng.RandomProviderState;
import org.apache.commons.rng.core.RandomProviderDefaultState;
import other.AI;
import other.GameLoader;
import other.context.Context;
import other.model.Model;
import other.state.State;
import other.state.container.ContainerState;
import other.trial.Trial;
import utils.RandomAI;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class ParallelMain {
    private static class Result {
        public final int    trialIndex;
        public final String starterBotName;
        public final int    trialMovesCount;
        public final String winnerBotName;
        public final String botPairName;

        public Result(int trialIndex, String starterBotName, int trialMovesCount, String winnerBotName, String botPairName) {
            this.trialIndex      = trialIndex;
            this.starterBotName  = starterBotName;
            this.trialMovesCount = trialMovesCount;
            this.winnerBotName   = winnerBotName;
            this.botPairName     = botPairName;
        }
    }

    public static Result compute(int trialIndex, Game game, List<AI> botPairs) throws InterruptedException {
        // Game initialization
        game = GameLoader.loadGameFromName("Plotto.lud");
        final Trial trial = new Trial(game);
        final Context context = new Context(game, trial);
        game.start(context);
        for (int p=1; p<=game.players().count(); p++) { botPairs.get(p).initAI(game, p); }

        // Start game
        final Model model = context.model();
        while (!trial.over()) { model.startNewStep(context, botPairs, 1.0); }

        // Store information to file
        String botPairName = ((BotAI)botPairs.get(1)).id + "_" + ((BotAI)botPairs.get(2)).id;
        String filePath = "out/trails/" + botPairName + "/trail_" + botPairName + "__" + trialIndex + ".trl";
        try {
            trial.saveTrialToTextFile(new File(filePath), "/lud/board/space/line/Plotto.lud", new ArrayList<>(), (RandomProviderDefaultState)trial.RNGStates().get(trial.RNGStates().size()-1));
        }
        catch (Exception e) { System.out.println("Exception: " + e.getMessage()); e.printStackTrace(); }

        // Store information in a result object
        String starterBotName = ((BotAI)botPairs.get(1)).id;
        int trialMovesCount = trial.numberRealMoves();
        String winnerBotName = ((BotAI)botPairs.get( (trial.ranking()[1] == 1.0 ? 1 : 2) )).id;
        return new Result(trialIndex, starterBotName, trialMovesCount, winnerBotName, botPairName);
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        // Create initial game entities
        // final Game game = GameLoader.loadGameFromName("Plotto.lud");

        // Create AI bots
        RandomBotAI randomBot0 = new RandomBotAI("r0");
        RandomBotAI randomBot1 = new RandomBotAI("r1");
        LegalBotAI  legalBot0  = new  LegalBotAI("l0");
        LegalBotAI  legalBot1  = new  LegalBotAI("l1");

        final List<List<AI>> botPairs = new ArrayList<>();
        /*
        botPairs.add(Arrays.asList(null, randomBot0, randomBot1));
        botPairs.add(Arrays.asList(null, randomBot1, randomBot0));
        botPairs.add(Arrays.asList(null, randomBot0,  legalBot0));
        */
        // botPairs.add(Arrays.asList(null,  legalBot0, randomBot0));
        /*
        botPairs.add(Arrays.asList(null, randomBot0,  legalBot1));
        botPairs.add(Arrays.asList(null,  legalBot1, randomBot0));
        */
        // botPairs.add(Arrays.asList(null,  legalBot0,  legalBot1));
        botPairs.add(Arrays.asList(null,  legalBot1,  legalBot0));

        // Create game trial parallel objects
        for (int f=0; f<25; f++) {
            System.out.println("Iteration #" + f);
            List<Callable<Result>> tasks = new ArrayList<Callable<Result>>();
            for (int i = 0; i < botPairs.size(); i++) {
                for (int j = 0 + 10 * f; j < 10 + 10 * f; j++) {
                    final int j1 = j;

                    AI bot0 = (botPairs.get(i).get(1) instanceof RandomBotAI ? new RandomBotAI(((BotAI) botPairs.get(i).get(1)).id) : new LegalBotAI(((BotAI) botPairs.get(i).get(1)).id));
                    AI bot1 = (botPairs.get(i).get(2) instanceof RandomBotAI ? new RandomBotAI(((BotAI) botPairs.get(i).get(2)).id) : new LegalBotAI(((BotAI) botPairs.get(i).get(2)).id));

                    Callable<Result> c = new Callable<Result>() {
                        @Override
                        public Result call() throws Exception {
                            // return compute(j1, game, Arrays.asList(null, bot0, bot1));
                            return compute(j1, null, Arrays.asList(null, bot0, bot1));
                        }
                    };
                    tasks.add(c);
                }
            }

            System.out.println("DONE PREPARING!");

            // Execute game trial parallel objects
            ExecutorService exec = Executors.newCachedThreadPool();
            try {
                List<Future<Result>> results = exec.invokeAll(tasks);
                for (Future<Result> fr : results) {
                    Files.createDirectories(Paths.get("out/trails/" + fr.get().botPairName));

                    File file = new File("out/trails/" + fr.get().botPairName + "/trail_stats_" + fr.get().botPairName + ".csv");
                    if (!file.exists()) {
                        Files.write(Paths.get("out/trails/" + fr.get().botPairName + "/trail_stats_" + fr.get().botPairName + ".csv"), "trial_index,starter_bot_name,trial_moves_count,winner_bot_name\n".getBytes(), StandardOpenOption.CREATE_NEW);
                    }

                    String line = (fr.get().trialIndex + 1) + "," + fr.get().starterBotName + "," + fr.get().trialMovesCount + "," + fr.get().winnerBotName + "\n";
                    Files.write(Paths.get("out/trails/" + fr.get().botPairName + "/trail_stats_" + fr.get().botPairName + ".csv"), line.getBytes(), StandardOpenOption.APPEND);
                }
            }
            catch (Exception e) { System.out.println("Exception: " + e.getMessage()); e.printStackTrace(); }
            finally             { exec.shutdown(); }
        }
    }
}
