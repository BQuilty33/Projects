from flask import Flask, render_template, request, redirect, url_for, session
from flask_socketio import SocketIO,join_room
import subprocess
import FundingAgency
import Researchers
import University
import threading
import uuid

from multiprocessing import Process

app = Flask(__name__)
socketio = SocketIO(app)




@socketio.on("onconnect")
def oncconnect():
    UniqueID = str(uuid.uuid4())
    join_room(UniqueID)
    data = {
        "UniqueID" : UniqueID
    }
    socketio.emit('connected',data,room=UniqueID)

class NewRoute(threading.Thread):
    def __init__(self):
        super(NewRoute,self).__init__()
    def run(self):
        FundingAgency.createTable()

@app.route('/')
def Funding():
    print("fsas")
    nRoute = NewRoute()
    nRoute.start()
    return render_template('FundingAgencyHTML.html')

class newGetProposals(threading.Thread):
    def __init__(self,data):
        super(newGetProposals,self).__init__()
        self.data = data
    def run(self):
        i = 0
        # get proposals based on table.
        UniqueID = self.data['UniqueID']
        Tble = self.data['proposal']
        getProposalsList = FundingAgency.getProposals(Tble)
        while(i < len(getProposalsList)):
            ProposalTitle = getProposalsList[i][0]
            # info only used to get information for idle proposals
            data = {
                "Title" : ProposalTitle,
                "ID" : getProposalsList[i][1],
                "Table" : Tble,
                "InfoOnly" : self.data['InfoOnly']
            }
            socketio.emit('ProposalTitles', data,room=UniqueID)
            i += 1

@socketio.on("getProposals")
def getProposals(data):
    nGetProposals = newGetProposals(data)
    nGetProposals.start()

class newProposalInformation(threading.Thread):
    def __init__(self,data):
        super(newProposalInformation,self).__init__()
        self.data = data
    def run(self):
        UniqueID = self.data['UniqueID']
        # get proposal information from table + id
        proposalInformationList = FundingAgency.getProposalInformation(self.data['proposal'],self.data['ID'])
        i = 0
        # get researcher from ID just obtained
        Name = FundingAgency.getResearcher(proposalInformationList[i][3])
        data = {
            "Title" : proposalInformationList[i][0],
            "ProjectDescription" : proposalInformationList[i][1],
            "FundAmount" : proposalInformationList[i][2],
            "ID" : self.data['ID'],
            "Name" : Name
        }
        socketio.emit('ProposalInformation', data,room=UniqueID)
        i += 1

@socketio.on("ProposalInformation")
def getInformation(data):  
    nProposalInformation = newProposalInformation(data)
    nProposalInformation.start()

class newSetProposalAccepted(threading.Thread):
    def __init__(self,data):
        super(newSetProposalAccepted,self).__init__()
        self.data = data
    def run(self):
        FundingAgency.ProposalAccepted(self.data['ID'],self.data['Budget'] , self.data['endDate'])

@socketio.on("SetProposalAccepted")
def setStatus(data):
    nSetProposalAccepted = newSetProposalAccepted(data)
    nSetProposalAccepted.start()

class newSetProposalRejected(threading.Thread):
    def __init__(self,data):
        super(newSetProposalRejected,self).__init__()
        self.data = data
    def run(self):
        FundingAgency.ProposalRejected(self.data['ID'])

@socketio.on("SetProposalRejected")
def setStatus(data):
    nSetProposalRejected = newSetProposalRejected(data)
    nSetProposalRejected.start()

if __name__ == "__main__":
    socketio.run(app, debug = True,port=5001)
