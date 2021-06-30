# cs0320 Term Project 2021

[Link to repository on GitHub](https://github.com/cs0320-2021/term-project-btracy2-ko19-pkrupski-xdou3)


## The Team

| Name (cslogin) | GitHub | Strengths | Weaknesses |
| :------------- | :----- | :-------- | :--------- |
| Brian Tracy (btracy2)   | btracy2    | Networking, API Design, JS | CSS, GameDev |
| Kwon Sok Oh (ko19)      | kwonsok    | Java, Algorithms | CSS, JS, HTML |
| Paul Krupski (pkrupski) | pjkrupski  | Algorithms, AI | JS, HTML |
| Jonathan Dou (xdou3)    | JonathanDou| Java, Game Development | HTML, CSS, Networking |

In addition, we have a mentor TA: Prithu Dasgupta (prithu_dasgupta@brown.edu)

## Meeting Dates

| Meeting | Date | Notes |
| :------ | :--- | :---- |
| Specs, Mockup, Design | `TBD` (before Mar15) | |
| 4-Way Checkpoint      | `TBD` (before Apr5)  | |
| Adversary Checkpoint  | `TBD` (before Apr12) | |

## Project Ideas

### Idea 0

**Summary:** Idea 0 is to build an HTTP server + framework from scratch in Java. All other ideas are built around this core and will use it to facilitate the creation of multi-user, networked applications.

HTTP is the workhorse of the web, and we want to take a deep dive in understanding what makes it all possible. Given just the two primitives of TCP and Threading, we will create an HTTP 1.1 compliant server. Around this, we will create a fully featured server framework that applications can actually use.

**Requirement 1: Standards Compliance**

Our low-level HTTP code must be standards compliant. The clients connecting to our backend will be mainstream browsers, and as such, will expect the server they are talking with to behave. One problem here is that browsers have a right to refuse non standard conforming responses, so to ensure that applications built with our framework can be useful, all of HTTP 1.1 must be supported. Fortunately, [the full specification](https://tools.ietf.org/html/rfc2616) is available online and HTTP is a plain-text protocol, making it easier to parse and debug.

**Requirement 2: Performance**

Nobody likes waiting for webpages to load. For this reason, creating a performant server will be a must to keep up with incoming requests. Single threaded servers are prone to blocking on requests and stalling everyone waiting. The biggest challenge here will be to develop *safe* multithreading. Our best ally in this effort will be the synchronization primitives offered by Java.

In addition, offering performant disk reads will be important to serving static files. Implementing caching will be necessary to satisfy this demand.

**Requirement 3: Rich Feature Set**

Non ergonomic APIs make people not want to use your product. Instead of guessing what people want, we have asked them directly! Piazza post @1425 is an open ended question to the class about what features they would like to see in a web server. Here are some commodity features that we are planning on implementing, and a quick snippet about potential difficulties.

1. *Serving static files:* This is a super common use case for spinning up an HTTP server and needs to come standard. The difficulty here will be how do protect the host computer's file system from illegal requests.
2. *Dynamic routing:* Creating routes such as `/course/{coursenum}/hw/{hwnum}` and having user provided values populate the variable path components is a very powerful way to create usable web APIs. Parsing will be the challenge here, and we would like to avoid the use of regular expressions (see security below).
3. *CSRF Protection:* Cross Site Request Forgery is an authentication based attack that allows malicious users to pose as credible ones. It can be prevented through the use of random tokens and HTTP cookies, and as such, should be built in natively to our server.
4. *Session Management:* While HTTP is meant to be stateless, modern web applications are anything but. Keeping track of sessions is critical to offering a useful service, but it comes with many associated challenges. To name a few, we have memory bloat from unused sessions, session forgery, and simultaneous sessions from a single user.

**Requirement 4: Security**

Opening up a host to the Internet is a scary thing. When providing a service such as an HTTP server, it is our responsibility to ensure that we have made our code as secure as possible. Below are some classes of vulnerabilities that have to be watched out for.

1. *Information Leakage:* Only requests to legitimately accessible resources should be respected, and only publicly avaialable information should be contained in any responses. The classic example of this is a malicious user requesting acces to a file like: `home/index.html/../../../../../../etc/passwd`. We will be addressing this by allowing the sandboxing of files to specific directories.
2. *Denial of Service:* Programmers require that the platform they are using is robust enough to carry out their vision. Denail of Service attacks can come from tricky places and need to be anticipated. One example is the use of regular expressions in parsing user input. Nefarious input can cause regular expression engines to take exponential time to decide on, halting the server for everyone. One way we can address this is by perforing all parsing via fix sized linear operations.
3. *Authentication:* Identity and Authentication are tricky concepts on the web, and users historically have a low tolerance for account information leaks (see Sony, Capital One, ..., data breaches). Ensuring, via the generation and appropriate usage of cryptographic tokens, that users can only claim to be who they really are will be a big challenge.

TA Approval (dlichen): I think you would have to go to Tim if you are serious about pursuing this idea. It's very different than what we usually expect. I think there is a significant level of complexity here but Tim would have to be the one to discuss it with you as it is pretty far outside the bounds of our class.


---

### Idea 1
#### Game Description

**Triple Deck**:
This multiplayer card game is loosely based on Rummy and Spoons

**Objective**: 
Put down the most sequences of three (a triple deck) before the cards run out

**Hand**: 
Each player will start out with a hand containing six cards 

**Turn**: 
On each turn, the player will search their hand for a sequence of three. If they have one, they will put it down and draw three more cards from the deck concluding their turn. If they don’t already have a sequence, they can either trade one card for a card in the deck or trade one card with a card from another player

**Triple Decks**: 
Aloud triple decks are (2, 3, 4) or (5, 6, 7) or (8, 9, 10) or (J, Q, K). Suits do not matter and Ace is a wild card

The game concludes when there are not enough cards left for each player to maintain a hand of 6, at which point the sequences are counted and the player with the most wins


#### Features

•	Visual representation of specific cards to appear on the screen to show the user what they hold

•	Sound effects for drawing, discarding, and laying down a sequence of three

•	List of players displayed on the side in order of who is winning 

•	Optional help menu that will display rules on how to play the game


#### Requirements

•	Generate deck of 52 cards and deal a hand of six to each player

    Random generator can be used to shuffle and deal the deck of cards
  
•	Provide user the option to put down three cards or pull from the deck

    When pulling from the deck, random generator will be used again on the set of remaining cards that haven’t been dealt
  
•	Verify cards put down by a user are one of the allowed sequences of three

    If a user attempts to put down a sequence of three, then their attempted sequence will be checked to see if it is a 
    member of a predetermined set of allowed sequences
    
•	Prevent user from putting down more than one sequence within a single turn

    If a user attempts to put down a second sequence in the same turn, a message will display instructing the user 
    to draw three more cards and end their turn
    
•	Allow user to draw three more cards from the deck if they put down a sequence or allow the user to discard a card if they drew from the deck

    If user chooses to discard, then that single card will be re added to the set that is the current deck and be available to draw 
    later in the game
    
•	Halt the game when there are no longer enough cards to sustain each user having a hand of three

    At the start of each turn, it will be checked if there are less than three cards remaining in the deck. If this is true, 
    the game will end

TA Approval (dlichen): I think this game sounds interesting but there's no significant algorithmic complexity here. Therefore, this is denied unless you can come up with some significant algorithm (i.e. AI? Generation of something?)

### Idea 2

Haunted Maze Party Game? Kill your friends?? 

Setting: 

- All of the players are trapped in a 2d haunted maze with unknown dangers and the goal
is to try to survive and find an exit. Hunters will try to kill off all the players.
  
Roles:

-Player (tries to escape) (possibly some players may get power roles)
-Hunter(s) (tries to kill every player in the maze)

Gameplay: 

- All Players and Hunters have limited vision (implement a fog of war effect)
- Players will spawn in random locations around the map while the hunter spawns right in the middle
- Players have to collect keys that are randomly spawned around the map in order to be able to escape 

Map Generation:
- The map will be procedurally generated using a maze/dungeon type generation algorithm

Game Modes:

- Vanilla: Players that escaped all win
- Last Man Standing: Multiple-rounds, last player that survives wins (reduce map size each round)
- Kill or be Killed: Players get some method/way to kill hunters (gather enough items or after a certain period of time)

This idea is still very open-ended, gameplay can change a lot, nothing i wrote here is really set in stone lol.

TA Approval (dlichen): Approved, contingent on you guys having a focus on the map generation algorithm and maybe some kind of AI Player algorithm for there to enough algorithmic complexity. 

### Idea 3


Covid International Response App

#### Setting

- We are in a coronavirus pandemic. The main problems are people dying, medical systems going overload, and international travel being banned. We want to know what is still possible and what is not.
  
#### Features

- Inform people regarding the death of people so that people are more aware, and perhaps let friends and family know about who died while preserving privacy
- Alert people regarding the condition of hospitals, including how many ICU beds are remaining, how many more mild-condition patienst can be admitted
- Update people regarding which countries are open under what conditions

#### Requirements

- The crux of this project is real-time information. Hence, there should be some way of automating the above process.
- Language is also a problem since this is a worldwide pandemic, so formal UN languages, mainly English and French should be supported.


This idea could actually be reusable since we might face multiple pandemics again and again in the future.

TA Approval (dlichen): Denied, unless you can come up with some algorithmic focus and also I'm not sure how you would be able to get death information as that's usually protected.

Do not need to resubmit.

