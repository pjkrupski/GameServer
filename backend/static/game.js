
// class Main {
//   main() {
//     const maze = new Maze(mazestr);
//     const canvas = document.getElementById('gameboard');
//     const renderer = new GameRenderer(canvas, maze);
//     renderer.drawMaze();
//     //renderer.drawPlayers([{x:0,y:0},{x:3,y:3}]);

//     const player = {x: 0, y: 0};
//     new UserInput((dir) => {
//       if (!maze.canMove(player, dir)) {
//         console.log('wall');
//         return;
//       }
//       if (dir == Maze.Direction.NORTH) {
//         player.y--;
//       }
//       if (dir == Maze.Direction.SOUTH) {
//         player.y++;
//       }
//       if (dir == Maze.Direction.EAST) {
//         player.x++;
//       }
//       if (dir == Maze.Direction.WEST) {
//         player.x--;
//       }
//       renderer.drawMaze();
//       renderer.drawPlayers([player]);
//     });



//   }
// }

class GameLogic {

}

class Maze {
  static Direction = {
      NORTH: 1,
      EAST: 2,
      SOUTH: 4,
      WEST: 8
  };
  constructor(serialized) {

    //console.log(serialized);
    const data =
      serialized
      .trim().split('\n')
      .map(row => row.trim().split(''));
    // Ensure that each row in the serialization is the same length
    // and that each entry can be parsed as a valid maze number.
    const rows = data.length;
    const expectedCols = data[0].length;
    for (let i = 0; i < rows; i++) {
      const actualCols = data[i].length;
      if (actualCols !== expectedCols) {
        throw new Error('mismatched serialized maze size');
      }

      for (let j = 0; j < actualCols; j++) {
        const entry = parseInt(data[i][j], 16);
        if (isNaN(entry) || entry < 0 || entry >= 16) {
          throw new Error('invalid maze entry: ' + entry);
        }
        data[i][j] = entry | 0;
      }
    }

    // Now we convert data (which has been validated and is full of integers)
    // to the correct coordinate space (essentially, we transpose the matrix).
    const maze = [];
    // The number of columns in the old data is now the number of rows.
    for (let i = 0; i < expectedCols; i++) {
      maze.push([]);
    }
    // Perform the transpose
    for (let i = 0; i < rows; i++) {
      for (let j = 0; j < expectedCols; j++) {
        maze[j][i] = data[i][j];
      }
    }

    //console.log(maze);
    this.maze = maze;
    this.serialized = serialized;
  }

  width() {
    return this.maze.length;
  }
  height() {
    return this.maze[0].length;
  }

  canMove(player, dir) {
    //console.log(player);
    const cell = this.maze[player.x][player.y];
    return (cell & dir) !== dir;
  }
}

class GameRenderer {

  constructor(canvas, maze) {
    this.maze = maze;
    this.canvas = canvas;
    this.ctx = canvas.getContext('2d');

    this.inset = 5;

    this.radii = {
      ghost: 5,
      maze: 5,
      player: 9
    };
  }


  endGame(winner) {
    this.ctx.font = '50px serif';
    this.ctx.fillStyle = 'black';
    this.ctx.textAlign = 'center';
    const text = 'Game Over! The ' + winner + ' won!';
    this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
    this.ctx.fillText(text, this.canvas.width / 2, this.canvas.height / 2);
    this.ctx.fillText('(a new round will start shortly)', this.canvas.width / 2, this.canvas.height / 2 + 60);
  }

  cellWidth() {
    return (this.canvas.width - (2 * this.inset)) / this.maze.width();
  }
  cellHeight() {
    return (this.canvas.height - (2 * this.inset)) / this.maze.height();
  }

  /*
    [{_alive,_id,_name,_loc:{_x, _y}}]
  */
  drawPlayers(players, isPlayers) {
    //console.log('drawing players', players);
    const objectRadius = (Math.min(this.cellWidth(), this.cellHeight()) / 2) - 4;
    this.ctx.fillStyle = isPlayers ? 'blue' : 'red';
    const myLoc = state.me._loc;
    const visibleRadius = isPlayers ? this.radii.player : this.radii.ghost;
    for (const player of players) {
      //console.log('drawing path', player);
      if (Math.abs(player._loc.x - myLoc.x) > visibleRadius || Math.abs(player._loc.y - myLoc.y) > visibleRadius) {
        continue;
      }
      if (!player._alive && isPlayers) {
        continue;
      }
      const x = this.inset + (player._loc.x * this.cellWidth()) + this.cellWidth() / 2;
      const y = this.inset + (player._loc.y * this.cellHeight()) + this.cellHeight() / 2;
      //console.log(x, y, objectRadius);
      this.ctx.beginPath();
      this.ctx.arc(x, y, objectRadius, 0, Math.PI * 2);
      this.ctx.fill();
      this.ctx.strokeStyle = 'black';
      this.ctx.font = '14px serif';
      this.ctx.fillText(player._name, x + 10, y - 10);

    }

  }

  drawMaze() {

    const usableWidth = this.canvas.width - (2 * this.inset);
    const usableHeight = this.canvas.height - (2 * this.inset);


    const NORTH = 1, EAST = 2, SOUTH = 4, WEST = 8;

    this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);

    // this.ctx.beginPath();
    // this.ctx.strokeStyle = 'gray';
    // this.ctx.lineWidth = 2;
    // this.ctx.rect(this.inset, this.inset, usableWidth, usableHeight);
    // this.ctx.stroke();

    const myLoc = state.me._loc;

    this.ctx.beginPath();
    this.ctx.setLineDash([5, 3]);
    this.ctx.strokeStyle = 'gray';
    this.ctx.lineWidth = .5;
    for (let x = 0; x < this.maze.width(); x++) {
        for (let y = 0; y < this.maze.height(); y++) {
          if (Math.abs(myLoc.x - x) > this.radii.grid || Math.abs(myLoc.y - y) > this.radii.grid) {
              continue;
            }
            this.ctx.rect(this.inset + x * this.cellWidth(), this.inset + y * this.cellHeight(), this.cellWidth(), this.cellHeight());

        }
    }
    this.ctx.stroke();


    this.ctx.beginPath();
    this.ctx.strokeStyle = "#2cd47d";
    this.ctx.lineWidth = 5;
    this.ctx.lineCap = 'round';
    this.ctx.setLineDash([]);



    for (let x = 0; x < this.maze.width(); x++) {
        for (let y = 0; y < this.maze.height(); y++) {

            if (Math.abs(myLoc.x - x) > this.radii.maze || Math.abs(myLoc.y - y) > this.radii.maze) {
              continue;
            }

            const value = this.maze.maze[x][y];

            if ((value & NORTH) != 0) {
                this.ctx.moveTo(x * this.cellWidth() + this.inset, y * this.cellHeight() + this.inset);
                this.ctx.lineTo((x+1) * this.cellWidth() + this.inset, (y) * this.cellHeight() + this.inset);
            }
            if ((value & EAST) != 0) {
                this.ctx.moveTo((x+1) * this.cellWidth() + this.inset, y * this.cellHeight() + this.inset);
                this.ctx.lineTo((x+1) * this.cellWidth() + this.inset, (y+1) * this.cellHeight());
            }
            if ((value & SOUTH) != 0) {
                this.ctx.moveTo(x * this.cellWidth() + this.inset, (y+1) * this.cellHeight() + this.inset);
                this.ctx.lineTo((x+1) * this.cellWidth() + this.inset, (y+1) * this.cellHeight() + this.inset);
            }

            if ((value & WEST) != 0) {
                this.ctx.moveTo((x) * this.cellWidth() + this.inset, y * this.cellHeight() + this.inset);
                this.ctx.lineTo((x) * this.cellWidth() + this.inset, (y+1) * this.cellHeight() + this.inset);
            }
        }
    }

    this.ctx.stroke();

  }

}


class UserInput {

  clear() {
    state.currentMove = 0;
  }

  constructor() {
    let keydown = false;
    document.onkeydown = function (e) {
      keydown = true;
      switch (e.key) {
          case 'ArrowUp':
          case 'w':
              state.currentMove = Maze.Direction.NORTH;
              console.log('N', state.currentMove);
              break;
          case 'ArrowDown':
          case 's':
              state.currentMove = Maze.Direction.SOUTH;
              console.log('S', state.currentMove);
              break;
          case 'ArrowLeft':
          case 'a':
              state.currentMove = Maze.Direction.WEST;
              console.log('E', state.currentMove);
              break;
          case 'ArrowRight':
          case 'd':
              state.currentMove = Maze.Direction.EAST;
              console.log('W', state.currentMove);
      }
    };
    document.onkeyup = function(e) {
      console.log("keyup")
      keydown = false;
    };
  }
}

const state = {
  currentMove: 0,
  lastGameUpdate: null,
  me: null
}

function getMyLocation() {
  state.me = state.lastGameUpdate.players.filter(p => p._name === playerInfo.name)[0];
  //console.log('me', state.me);
}


function joinGame() {

  const userInput = new UserInput();

  let maze = null;
  let renderer = null;

  console.log('joining game as player: ', playerInfo);
  setInterval(() => {
    //console.log('sending update');
    const move = state.currentMove;
    console.log('sending move', state.currentMove);
    if (!userInput.keydown) {
        userInput.clear();
    }
    fetch('/api/update', {
      method: 'POST',
      body: playerInfo.lobby + '\n' + playerInfo.id + '\n' + `${move}`
    }).then(r => r.json()).then((game) => {

      state.lastGameUpdate = game;
      getMyLocation();
      updateScoreboard();
      //console.log(game);
      if (maze === null || maze.serialized != game.maze) {
        console.log('maze, game')
        //console.log(maze.maze);
        console.log(game.maze);
        console.log('maze is null, setting up');
        maze = new Maze(game.maze);
        console.log(maze.maze);

        renderer = new GameRenderer(document.getElementById('gameboard'), maze);
      }
      if (game.gamestopped === false) {
        renderer.drawMaze();
        renderer.drawPlayers(game.players, true);
        renderer.drawPlayers(game.ghosts, false)
      }
      if (game.gamestopped === true) {
        // round is over
        // display round over dialog

        renderer.endGame(game.winner);
        setTimeout(() => {
          // tell the server to restart the game
          fetch('/api/restartgame', {
            method: 'POST',
            body: playerInfo.lobby
          });
        }, 3000);
      } else {

      }

    });
  }, 250)
}

function showRoundOverScreen(winner) {
  document.getElementById('round-over').innerHTML = 'Game Over! The ' + winner + 'won.';
  document.getElementById('round-over').style.display = 'block';
}


function updateScoreboard() {
  const game = state.lastGameUpdate;
  const round = `<div id="round-number"><u>Round ${game.round}</u></div>`;
  let table = '<table border="3"><thead><td>Player</td><td>Score</td><td>Status</td><tbody>';
  for (const p of game.players) {
    const aliveClass = p._alive ? 'alive' : 'dead';
    table += `<tr><td>${p._name}</td><td>${p._score}</td><td class="${aliveClass}">${aliveClass}</td></tr>`;
  }
  table += '</tbody></table>';
  document.getElementById('scoreboard').innerHTML = round + table;
}
