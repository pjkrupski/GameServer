
window.addEventListener('DOMContentLoaded', (event) => {
    const joinButton = document.getElementById('join-button');
    const lobbyInput = document.getElementById('lobby-input');
    const createGameButton = document.getElementById('create-game-button');

    joinButton.onclick = (event) => {
        const lobbyId = lobbyInput.value;
        fetch('/api/islobby', {
            method: 'POST',
            body: lobbyId
        }).then(r => r.text()).then((valid) => {
            if (valid === 'true') {
                document.getElementById('index-container').style.display = 'none';
                document.getElementById('lobby-container').style.display = 'block';
                joinLobby(lobbyId);
            } else {
                alert('invalid lobby id: ' + lobbyId);
            }
        });
    };

    createGameButton.onclick = (event) => {
        fetch('/api/newlobby').then(r => r.text()).then(lobbyId => {
            lobbyInput.value = lobbyId;
        });
    };


});