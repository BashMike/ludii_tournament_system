package core;

import ai.BotAI;
import game.Game;
import other.AI;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

abstract public class TournamentScheme {
    protected Game        _game   ;
    protected List<BotAI> _allBots;

    abstract public boolean _isValidForGame();

    public void organizeBotGroups(Game game, List<BotAI> allBots) {
        this._game    = game;
        this._allBots = allBots;

        this._organizeBotGroups();
    }

    abstract protected void _organizeBotGroups();

    final public List<AI> getCurrBotGroup() { return this._getCurrBotGroup().stream().map(e -> (AI)e).collect(Collectors.toList()); }
    abstract protected List<BotAI> _getCurrBotGroup();

    abstract public void moveToNextBotGroup();
    abstract public boolean isTournamentEnded();

    abstract public String getCurrBotGroupName();
}
