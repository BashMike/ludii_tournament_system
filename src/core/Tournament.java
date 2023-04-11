package core;

import ai.BotAI;
import game.Game;
import other.AI;
import other.context.Context;
import other.model.Model;
import other.trial.Trial;
import statistic_fetchers.BasicStatisticFetcher;

import java.util.List;
import java.util.stream.Collectors;

public class Tournament {
    // ================ ATTRIBUTES ================
    private Game             _game            ;
    private TournamentScheme _scheme          ;
    private int              _roundsCount     ;

    private List<BotAI>      _allBots         ;

    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public Tournament(Game game, List<BotAI> allBots, TournamentScheme scheme, int roundsCount) {
        this._game             = game;
        this._scheme           = scheme;
        this._roundsCount      = roundsCount;
        this._allBots          = allBots;

        this._scheme.organizeBotGroups(game, allBots);
        if (!this._scheme._isValidForGame()) {
            throw new RuntimeException("ERROR :: " + scheme.getClass().getSimpleName() + " isn't valid for \"" + game.name() + "\" game.");
        }
    }

    // ----------------- contract -----------------
    public void start(StatisticFetcher statFetcher) {
        Trial   trial   = new Trial(this._game);
        Context context = new Context(this._game, trial);

        statFetcher._setup(context, this._scheme);
        statFetcher.performGlobalBeginFetch();

        System.out.println("Start tournament of \"" + this._game.name() + "\" game with " + this._scheme.getClass().getSimpleName() + ".");

        List<AI> botGroup = this._scheme.getCurrBotGroup();
        while (botGroup.size() > 0) {
            System.out.println("* Play \"" + this._scheme.getCurrBotGroupName() + "\" rounds.");

            statFetcher._setupBots(botGroup);
            statFetcher.performBeginFetch();

            for (int i=0; i<this._roundsCount; i++) {
                this._game.start(context);
                for (int p=1; p<=this._game.players().count(); p++) { botGroup.get(p).initAI(this._game, p); }

                final Model model = context.model();
                while (!trial.over()) { model.startNewStep(context, botGroup, 1.0); }

                statFetcher.performEachFetch(i);

                System.out.print("- round " + (i+1) + "/" + this._roundsCount + "\r");
            }
            System.out.println("");

            statFetcher.performEndFetch();

            this._scheme.moveToNextBotGroup();
            botGroup = this._scheme.getCurrBotGroup();
        }

        statFetcher.performGlobalEndFetch();
    }
}