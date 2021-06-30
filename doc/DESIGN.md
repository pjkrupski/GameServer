

Project Design
==============

**Intro / Refresher:** Our project is made up of two parts (refer to Ideas 0 and 2 from `README.md`). The more techinical aspect is the creation of an HTTP server + framework using only the primitives of TCP and Java threads. On top of this, we will demonstrate the effectiveness and usability of our server by using it as backend for a multiplayer party game in which a group of players has to escape programatically generated mazes. 


## Design Snapshot

The project is broken down into three main components: http core, game backend, and game frontend.

### HTTP Core

The HTTP core is responsible for setting up an HTTP server that can be used by other applications. The HTTP core itself can be further divided into two components: the low level networking, and the high level framework.

##### Networking

This is where the "rubber meets the road" and the actual network is interfaced with. This encapsulates things like creating a server socket (`listen`), accepting new clients (`accept`), and reading/writing binary data to TCP connections. The core class here will be `ServerSocket` that is responsible for creating new `Connection` objects that represent a given transaction over the network. The only functionality needed at this layer is to be able to `sendBytes()` and `receiveBytes()` from a `Connection`.


##### Framework

Wrapped around the networking component of the HTTP core is the framework component. This is where the HTTP protocol is enforced and high level HTTP constructs are exposed. Here are some of the major players.

1. `HeaderSet` a mapping from HTTP Header to a string value (`get` and `set`).
2. `Request` a representation of a single HTTP request. Relevant fields are things like headers in this request, which HTTP verb was used, what URL was requested, and what body was included (if any) in the request.
3. `Response` a representation of a single HTTP response. Relevant fields are things like status, headers, and an optional body.

The framework essentially provides ergonimic ways to transform given `Request` objects into the desired (by the application using the framework) `Response` objects. Here is a small snippet of to provide an idea of the concise nature of the API we want to provide.

```
serverInstance.post("/newRoom", (Request req, Response res) -> {
	if (!req.headers.get("X-password").equals("hunter2")) {
		res.send(403, "Sorry, you can't make a new room");
	} else {
		final String roomName = request.body();
		// create room
		res.send(200, "Successfully created room: " + roomName);
	}
});
```

Finally, a critical component of the `Response` object will be its `toString` that will serialize the contents of the request (including its headers, status line, and body) into the proper HTTP required format before network transmission.

In addition, each `Request`/`Response` pair will have access to the underlying `Connection` object that they are being carried over.

At the heart of the framework will be the `Dispatcher`, which will contain a thread pool of workers to send `Request`/`Response` pairs to.

### Game Backend

After the HTTP core is complete, we can use it to create the backend for our game. Each party will be uniquely identified by a `roomId`, and each user within a party will identified by a `userId`. Network requests to the backend will include this information in the URL (for example, a player might move left by issuing a `POST` request to the `/move` route with a body of `roomId=abc123&userId=john&move=left`).

Managing rooms and players will be done with the `Room` and `Player` class (where a `Room` contains a list of `Players`).

The actual game logic itself will reside in the "pure" class (pure in the sense that there will be no notion of this being a networked game, or one with a graphical frontend. It is completely agnostic to how it is used, it just knows about a game and its rules) `GameBoard` which keeps track of which players are where, and what the maze looks like.

Mazes are stored as two dimensional arrays of `MazeRoom` enums, which have a case for each type of possible room (empty, northWall, southWall, ...). Maze generation will be accomplished through a selection of algorithms such as drunken walks and geometric patterns.

One of the core features of the backend will be to keep track of the game state and ensure that users are not making illegal moves. When an illegal move is made, the backend will refuse to recognize the change in position (this can happen when there is a bug in the frontend, or malicious requests are being forged). In addition, the backend will broadcast the moves of every player to every other player so that every client stays updated and can draw an accurate board.

At a high level, the server backend will intercept network requests, and then forward the desired action to the appropriate `Room` object. This `Room` will then update its `GameBoard`, and the modifications will be sent back out to each client.

### Game Frontend

Our frontend will be created as a single dynamic webpage (one HTML file, DOM modifications made through JavaScript). This is done to reduce asset loading pressure on our backend.

User interaction will be done through the keyboard using JavaScript keyboard APIs. The `Listener` component is responsible for detecting user input and forwarding it to a `Game` object.

The `Game` object keeps track of the server generated maze, as well as the locations of all other players.

At every time interval on the client side (this parameter can be tuned, most likely from 0.1 - 1 seconds), each client will draw its updated game state with the `Renderer` class. This class wraps an HTML `<canvas>` element as is responsible for tracing out the paths the will be the game's visualization.


### Testing Plan

**HTTP Core:** Testing our backend will be accomplished through differential testing of several HTTP frameworks. A test suite will be set up as follows:

1. Implement a basic echo-like server with our backend, a NodeJS (express) backend, and a Python (Flask) backend.
2. Create a benchmark program in JavaScript that issues and times hundreds of requests to a given backend.
3. Compute performance metrics such as total request throughput and latency for each backend.
4. Ensure correctness of responses from each backend.

This type of end-to-end test will allow us to touch all layers of our http core as well as get a general sense of how it performs.

**Game Backend:** The game backend will be tested through the simulation of games. The game itself is a pure function from `(board , input) -> board`, so we can provide complicated test inputs and then ensure that the final result is what we expect. This will work much like the system tests for previous assignments:

```
create maze (random seed = 14531)
player1 left
player2 right
player3 up
...
...
...
ASSERT maze looks like: ....
```

**Game Frontend:** This one will be much harder to test and we actually don't quite yet have a great design. This is one point we will be keeping in mind moving forward. Testing graphical output (like how a canvas looks after a certain input) is hard for us to automate, so we will likely have to set up a suite of inputs to manually verify.


### Project Layout

The project source will be layed out as follows

```
backend/
	httpcore/
		net/ -- low level networking
		framework/ -- HTTP framework
	maze/ -- game server backend
	
frontend/
	html/
		index.html -- single game page
	js/
		net/ -- shims for backend communication
		game/ -- game logic and rule enforcement
		render/ -- canvas and UI related code

```


### Project Implementation and Timeline

The order of implementation is as follows, with later components building on earlier ones. Due dates are in bold with milestones alongside.

**Mar 15:** Design finishes, coding starts

**Mar 22:** HTTP core and maze logic

**Mar 29:** HTTP Framework and maze drawing. Maze logic testing

**April 5:** HTTP Framework testing and frontend logic.

**April 12:** Game backend coordination.

Coding done, presentation details start


### Expected Issues

We are building a moderately "real time" game, where players will be giving rapid inputs and the game state will be updating frequently. One issue we forsee running into is the lag between a user inputting their move and the move showing up on their screen due to the round trip time necessary to validate their move.
