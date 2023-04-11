package tournament_schemes;

import ai.BotAI;
import core.TournamentScheme;
import other.AI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SingleEliminationTournamentScheme extends TournamentScheme {
    private List<List<Integer>> _botGroupIndexes = Arrays.asList(Arrays.asList(-1));
    private int                 _botGroupCursor  = 0;
    private int                 _stageIndex      = 0;

    @Override
    public boolean _isValidForGame() { return (this._game.players().count() == 2); }

    @Override
    protected void _organizeBotGroups() {
    }

    @Override
    public List<BotAI> _getCurrBotGroup() {
        List<BotAI> result = new ArrayList<>();
        if (!this.isTournamentEnded()) {
            List<Integer> botGroupIndexes = this._botGroupIndexes.get(this._botGroupCursor);

            result.add(null);
            for (int i=1; i<botGroupIndexes.size(); i++) { result.add(this._allBots.get( botGroupIndexes.get(i) )); }
        }

        return result;
    }

    @Override
    public void moveToNextBotGroup() { this._botGroupCursor++; }

    @Override
    public boolean isTournamentEnded() { return (this._botGroupCursor >= this._botGroupIndexes.size()); }

    @Override
    public String getCurrBotGroupName() {
        List<BotAI> bots = this._getCurrBotGroup();
        return "stage_" + (this._stageIndex+1) + "__" + bots.get(1).id + bots.get(2).id;
    }
}
