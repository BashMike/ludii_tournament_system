package core;

import ai.BotAI;
import game.Game;
import other.AI;
import other.context.Context;
import other.trial.Trial;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

abstract public class StatisticFetcher {
    // ================ ATTRIBUTES ================
    protected Context          _context          ;
    protected Game             _game             ;
    protected Trial            _trial            ;
    protected List<BotAI>      _bots             ;
    protected String           _tournamentDirPath;
    protected TournamentScheme _scheme           ;

    // ================ OPERATIONS ================
    // ----------------- contract -----------------
    final public void _setup(Context context, TournamentScheme scheme) {
        this._context = context;
        this._game    = context.game();
        this._trial   = context.trial();
        this._scheme  = scheme;

        String currDateTimeStr = LocalDate.now().toString() + "__";
        currDateTimeStr += LocalTime.now().getHour() + "_";
        currDateTimeStr += LocalTime.now().getMinute() + "_";
        currDateTimeStr += LocalTime.now().getSecond() + "_";
        currDateTimeStr += LocalTime.now().getNano();
        String tournamentSchemeName = scheme.getClass().getSimpleName();

        this._tournamentDirPath = "out/tournaments/tournament_" + this._game.name() + "_" + tournamentSchemeName + "(" + currDateTimeStr + ")";
    }

    final public void _setupBots(List<AI> bots) {
        this._bots = bots.stream().map(e -> (BotAI)e).collect(Collectors.toList());
    }

    abstract public void performBeginFetch();
    abstract public void performEndFetch();
    abstract public void performEachFetch(int roundIndex);
    abstract public void performGlobalBeginFetch();
    abstract public void performGlobalEndFetch();
}
