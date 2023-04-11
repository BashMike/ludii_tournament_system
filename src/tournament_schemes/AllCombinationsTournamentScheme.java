package tournament_schemes;

import ai.BotAI;
import core.TournamentScheme;
import game.Game;
import other.AI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AllCombinationsTournamentScheme extends TournamentScheme {
    private List<List<Integer>> _botGroupIndexes = Arrays.asList(Arrays.asList(-1));
    private int                 _botGroupCursor  = 0;
    private int                 _playersCount    = 0;

    @Override
    public boolean _isValidForGame() { return true; }

    @Override
    protected void _organizeBotGroups() {
        this._playersCount = this._game.players().count();
        for (int i=0; i<this._playersCount; i++) {
            List<List<Integer>> bufferGroups = new ArrayList<>();

            for (var botGroup : this._botGroupIndexes) {
                for (int j=0; j<this._allBots.size(); j++) {
                    if (!botGroup.contains(j)) {
                        List<Integer> bufferGroup = new ArrayList<>(botGroup);
                        bufferGroup.add(j);
                        bufferGroups.add(bufferGroup);
                    }
                }
            }

            this._botGroupIndexes = bufferGroups;
        }
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

        String result = bots.get(1).id;
        for (int i=2; i<bots.size(); i++) { result += "_" + bots.get(i).id; }

        return result;
    }
}
