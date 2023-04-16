import sys
import matplotlib.pyplot as plt
import pandas as pd
import numpy as np

if len(sys.argv) != 2:
    print('ERROR: Need path to csv file as CMD argument.')
    sys.exit(0)

df = pd.read_csv(sys.argv[1])
names = np.unique(df.iloc[:, 4:].values)
players_count = len(df.iloc[:, 4:].columns)

if players_count != 2:
    print("INFO: There is no support for more than 2 players right now.")

fig, axs = plt.subplots(len(names), len(names), figsize=(15, 12))
fig.suptitle('Competition statistics')

for i in range(len(names)):
    for j in range(len(names)):
        bots_names = ['bot \"' + names[i] + '\"', 'bot \"' + names[j] + '\"']
        bots_wins = np.array([0, 0])

        if i == j:
            bots_wins[0] = df.loc[( (df['bot_1'] == names[i]) | (df['bot_2'] == names[i]) ) & (df['winner_bot_name'] == names[i])].shape[0]
            bots_wins[1] = df.loc[( (df['bot_1'] == names[i]) | (df['bot_2'] == names[i]) ) & (df['winner_bot_name'] != names[i])].shape[0]
            bots_match_count = bots_wins.sum()

            p, tx, autotexts = axs[i, j].pie(bots_wins, colors=['tomato', 'orange'], autopct=lambda p : '')
            axs[i, j].set_title(bots_names[0] + ' total: ' + str(bots_match_count) + ' matches')

            bots_names[0] = bots_names[0] + ' wins'
            bots_names[1] = bots_names[1] + ' losts'
        else:
            bots_wins[0] = df.loc[(df['bot_1'] == names[i]) & (df['bot_2'] == names[j]) & (df['winner_bot_name'] == names[i])].shape[0]
            bots_wins[1] = df.loc[(df['bot_1'] == names[i]) & (df['bot_2'] == names[j]) & (df['winner_bot_name'] == names[j])].shape[0]
            bots_match_count = bots_wins.sum()

            if bots_match_count != 0:
                p, tx, autotexts = axs[i, j].pie(bots_wins, colors=['gold', 'yellowgreen'], autopct=lambda p : '')
                axs[i, j].set_title('Pair total: ' + str(bots_match_count) + ' matches')
            else:
                bots_wins = np.array([1, 1])
                bots_match_count = 1
                p, tx, autotexts = axs[i, j].pie(bots_wins, colors=['grey', 'grey'], autopct=lambda p : '')

                axs[i, j].set_title('Non-existent pair')

        for f, a in enumerate(autotexts):
            a.set_text("{}\n{}\n({:.1f}%)".format(bots_names[f], bots_wins[f], bots_wins[f]*100/bots_match_count))

    bot_name = 'bot \"' + names[i] + "\""
    axs[i, 0].set(ylabel=bot_name)
    axs[len(names)-1, i].set(xlabel=bot_name)

plt.savefig('output.png')