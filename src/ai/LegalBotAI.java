package ai;

import java.util.concurrent.ThreadLocalRandom;

import game.Game;
import main.collections.FastArrayList;
import other.AI;
import other.context.Context;
import other.move.Move;
import utils.AIUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import game.Game;
import main.collections.FastArrayList;
import other.AI;
import other.RankUtils;
import other.context.Context;
import other.move.Move;

public class LegalBotAI extends BotAI {
    protected int playerId = -1;

    public LegalBotAI(String id) {
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

        return legalMoves.get(0);
    }

    @Override
    public void initAI(final Game game, final int playerID) { this.playerId = playerID; }
}
