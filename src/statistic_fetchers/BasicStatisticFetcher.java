package statistic_fetchers;

import core.StatisticFetcher;
import org.apache.commons.rng.core.RandomProviderDefaultState;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class BasicStatisticFetcher extends StatisticFetcher {
    // ================ ATTRIBUTES ================
    private PrintWriter _csvWriter;

    // ================ OPERATIONS ================
    // ----------------- contract -----------------
    @Override
    public void performBeginFetch() {
        this._csvWriter = null;
        try {
            Files.createDirectories(Paths.get(this._tournamentDirPath + "/" + this._scheme.getCurrBotGroupName()));
            this._csvWriter = new PrintWriter(this._tournamentDirPath + "/" + this._scheme.getCurrBotGroupName() + "/game_statistics.csv", "UTF-8");
        }
        catch (Exception e) { System.out.println("Exception: " + e.getMessage()); e.printStackTrace(); }
        this._csvWriter.println("trial_index,starter_bot_name,trial_moves_count,winner_bot_name");
    }

    @Override
    public void performEndFetch() { this._csvWriter.close(); }

    @Override
    public void performEachFetch(int roundIndex) {
        String filePath = this._tournamentDirPath + "/" + this._scheme.getCurrBotGroupName() + "/round_" + roundIndex + ".trl";

        try {
            this._trial.saveTrialToTextFile(new File(filePath), this._game.description().filePath(), new ArrayList<>(), (RandomProviderDefaultState) this._trial.RNGStates().get(this._trial.RNGStates().size() - 1));
        }
        catch (Exception e) { System.out.println("Exception: " + e.getMessage()); e.printStackTrace(); }

        this._csvWriter.print(roundIndex + "," + this._bots.get(1).id + "," + this._trial.numberRealMoves() + ",");
        int winnerBotIndex = 1;
        for (int i=1; i<this._bots.size(); i++) {
            if (this._trial.ranking()[i] == 1.0) { winnerBotIndex = i; break; }
        }
        this._csvWriter.println(this._bots.get(winnerBotIndex).id);
    }

    @Override
    public void performGlobalBeginFetch() {}

    @Override
    public void performGlobalEndFetch() {}
}
