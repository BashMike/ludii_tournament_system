package core;

import ai.BotAI;
import ai.LegalBotAI;
import ai.RandomBotAI;
import ai.SmartBotAI;
import game.Game;
import other.AI;
import other.GameLoader;
import statistic_fetchers.BasicStatisticFetcher;
import tournament_schemes.AllCombinationsTournamentScheme;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        System.out.println("Loading game...");
        String ludFilePath = "/lud/board/space/line/Plotto.lud";
        Game game = GameLoader.loadGameFromName(ludFilePath);
        game.description().setFilePath(ludFilePath);
        System.out.println("\"" + game.name() + "\" game has been loaded.");

        List<BotAI> allBots = new ArrayList<>();
        allBots.add(new RandomBotAI("r0"));
        allBots.add(new RandomBotAI("r1"));
        allBots.add(new LegalBotAI("l0"));
        allBots.add(new LegalBotAI("l1"));

        Tournament tournament = new Tournament(game, allBots, new AllCombinationsTournamentScheme(), 1000);
        tournament.start(new BasicStatisticFetcher());
    }
}