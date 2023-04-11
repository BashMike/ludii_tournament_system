package ai;

import java.util.concurrent.ThreadLocalRandom;

import game.Game;
import main.collections.FastArrayList;
import other.AI;
import other.context.Context;
import other.move.Move;
import utils.AIUtils;

public class RandomBotAI extends BotAI {
    protected int playerId = -1;

    public RandomBotAI(String id) {
        super(id);
        this.friendlyName = "Example UCT";
    }

    @Override
    public Move selectAction(
        final Game    game,
        final Context context,
        final double  maxSeconds,
        final int     maxIterations,
        final int     maxDepth
    )
    {
        FastArrayList<Move> legalMoves = game.moves(context).moves();

        if (!game.isAlternatingMoveGame()) { legalMoves = AIUtils.extractMovesForMover(legalMoves, this.playerId); }

        final int r = ThreadLocalRandom.current().nextInt(legalMoves.size());
        return legalMoves.get(r);
    }

    @Override
    public void initAI(final Game game, final int playerID) { this.playerId = playerID; }
}
