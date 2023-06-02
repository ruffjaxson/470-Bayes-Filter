To run the software, you must first compile the server and client programs (run `javac *.java` in each
folder). Then, to run the server, go to the Server world in a terminal, and type:

`java BayesWorld [world] [motor_probability] [sensor_probability] [known/unknown]`

where world can be any of the worlds specified in the “Mundo” directory,[motor_probably] is a
value between 0 and 1 specifying pm, [sensor_probability] is a value between 0 and 1 specifying
ps, and “known” specifies that the robot’s initial position is given to the robot at the start of the
simulation, and “unknown” is specified to say that the robot’s initial position is not given to the robot at
the start of the simulation. For example:

`java BayesWorld mundo_maze.txt 0.9 0.8 unknown`

starts the server in the world mundo_maze.txt, with pm= 0.9, ps= 0.8, and the robot’s initial position is
unknown. Several worlds are already provided (you can create your own if you would like).
Note: in this lab you should always set the last parameter to “unknown.”
Once the server is running, you can connect the robot (client) to it. In a separate terminal, go to the
“Robot” folder and type:

`java theRobot [manual/automatic] [decisionDelay]`

where “manual” specifies that the user (you) will specify the robot’s actions, “automatic” specifies that
the robot will control it’s own actions, and [decisionDelay] is a time in milliseconds used to slow
down the robot’s movements when it chooses automatically (so you can see it move). In manual mode,
you press keys to have the robot move when the client GUI window is active. ‘i’ is up, ‘,’ is down, ‘j’ is
left, ‘l’ is right, and ‘k’ is stay. Note that the client GUI must be the active window in order for the key
commands to work.

Note: the “automatic” mode is not needed for this lab. Likewise, for this lab you can just specify 0 for
“decisionDelay” for this lab. Thus, you can simply type:

`java theRobot manual 0`

to run the program.