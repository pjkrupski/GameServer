

const playerInfo = {
    id: Math.random().toString(36).substring(0, 12),
    name: 'un-named player',
    lobby: ''
};

function joinLobby(lobbyId) {

    playerInfo.lobby = lobbyId;

    document.getElementById('lobby-title').innerHTML = `Pre-Game Lobby (${lobbyId})`;

    function addPlayer(name) {
        fetch('/api/addplayer', {
            method: 'POST',
            body: playerInfo.lobby + '\n' + playerInfo.id + '\n' + name
        })
    }

    addPlayer(playerInfo.name);
    document.getElementById('player-name').value = playerInfo.name;

    document.getElementById('lobby-name-button').onclick = (event) => {
        playerInfo.name = document.getElementById('player-name').value;
        addPlayer(playerInfo.name);
    }

    function fetchPlayers() {
        fetch('/api/players', {
            method: 'POST',
            body: lobbyId
        }).then(r => r.json()).then((playerNames) => {
            const list = document.getElementById('lobby-players');
            list.innerHTML = '';
            for (const p of playerNames) {
                const el = document.createElement('li');
                el.appendChild(
                    document.createTextNode(p)
                );
                list.appendChild(el);
            }
        });
    }

    const fetchInterval = setInterval(fetchPlayers, 1000);

    document.getElementById('lobby-join-game').onclick = (event) => {
        clearInterval(fetchInterval);

        document.getElementById('lobby-container').style.display = 'none';
        document.getElementById('game-container').style.display = 'block';

        joinGame();
    };

}