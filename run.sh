#!/bin/bash

# Kill any existing tmux session named jobapp
tmux kill-session -t jobapp 2>/dev/null

# Start tmux session and services
tmux new-session -d -s jobapp
# Start Application service in first pane
tmux send-keys -t jobapp 'cd /Users/arsh/Developer/Chitkara/Job-Application-Service/Application && ./mvnw spring-boot:run' C-m
# Split horizontally and start Authentication service
tmux split-window -h -t jobapp
tmux send-keys -t jobapp:0.1 'cd /Users/arsh/Developer/Chitkara/Job-Application-Service/Authentication && ./mvnw spring-boot:run' C-m
# Split vertically and start Job service
tmux split-window -v -t jobapp:0.1
tmux send-keys -t jobapp:0.2 'cd /Users/arsh/Developer/Chitkara/Job-Application-Service/Job && ./mvnw spring-boot:run' C-m
# Arrange panes
tmux select-layout -t jobapp tiled
tmux attach -t jobapp
