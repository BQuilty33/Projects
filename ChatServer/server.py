# Python 3 server example
from http.server import BaseHTTPRequestHandler, HTTPServer
import time
import socket
import os
from flask import Flask, render_template, request, redirect, url_for, session
from flask_socketio import SocketIO, join_room, leave_room, emit, rooms
from flask_session import Session
from flask_sock import Sock
template_dir = os.path.abspath('/Downloads')
app = Flask(__name__)
socketio = SocketIO(app)

comp_rooms = []




curr_room = ""



@app.route('/')
def index():
    return render_template('session.html')

# message to show client connected to server.
def messageReceived(methods=['GET', 'POST']):
    print('message was received!!!')

# client connected to server
@socketio.on('on conn')
def handle_my_custom_event(json, methods=['GET', 'POST']):
    print('received my event: ' + str(json))
    socketio.emit('my response', json, callback=messageReceived)

# Server listens for client messages
@socketio.on("send message")
def message(data, methods=['GET', 'POST']):
    room = data['room']
    # Server calls html code to display message
    socketio.emit('broadcast message', data, room=room, callback=messageReceived)

# Server listens for client creating room
@socketio.on('create')
def on_create(data):
    # create room based on data
    channel = data['room']
    if(channel not in comp_rooms):
        comp_rooms.append(channel)
    join_room(channel)


# Server listens for client joining room
@socketio.on('join')
def on_join(data):
    # join room based on data
    channel = data['room']
    if(channel not in comp_rooms):
        comp_rooms.append(channel)
    join_room(channel)

# Server listens for client wanting to list room
@socketio.on('listrooms')
def listroomz(data):
    # Server calls html code to display rooms
    socketio.emit('listrooms', data, callback=messageReceived)

# Called when client first enters server    
@socketio.on('listroomsfrst')
def listroomzyu():
    arr = []
    w = 0
    # loop all of the rooms except the one you are currently in.
    while(w < len(comp_rooms)):
        if(comp_rooms[w] != str(request.sid)):
            arr.append(comp_rooms[w])
        w += 1
    data = {
        "rooms" : arr
    }
    # call html code to list all the rooms when user connects
    socketio.emit('listroomsfrsty', data, room=request.sid, callback=messageReceived)

# Server listens for client leaving the room
@socketio.on('leave')
def on_leave(data):
    # leave the room based on data.
    roomtl = data['room']
    leave_room(roomtl)



if __name__ == '__main__':
    socketio.run(app, debug = True)






