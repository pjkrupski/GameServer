

This is the top level README for our CS32 project. The actual README is in the doc/ directory.



To run the program,

$ cd backend
$ mvn package -Dmaven.test.skip=true
$ ./run

Then, open a browser and go to http://localhost:8080/static/index.html



To play the game over the internet, install ngrok to expose the local server

ngrok http 8080 --host-header rewrite
