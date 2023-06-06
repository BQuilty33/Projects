from flask import Flask, render_template, request, redirect, url_for, session
from flask_socketio import SocketIO,join_room
import uuid
import University
import Researchers
import threading
import time

app = Flask(__name__)
socketio = SocketIO(app)


@app.route('/')
def Universityfunction():
    return render_template('UniversityHTML.html')

@socketio.on("onconnect")
def oncconnect():
    UniqueID = str(uuid.uuid4())
    join_room(UniqueID)
    data = {
        "UniqueID" : UniqueID
    }
    socketio.emit('connected',data,room=UniqueID)

class newListResearchers(threading.Thread):
    def __init__(self,data):
        super(newListResearchers,self).__init__()
        self.data = data
    def run(self):
        ListResearcherslist = Researchers.ListResearchers()
        i = 0
        UniqueID = self.data['UniqueID']
        socketio.emit('ClearElementsAccount',room=UniqueID)
        socketio.emit('ResearchSearchBar',room=UniqueID)
        while(i < len(ListResearcherslist)):
            data = {
                "Name" : ListResearcherslist[i][0],
                "ID" : ListResearcherslist[i][1],
                "HolderID" : i + 1
            }
            socketio.emit('ListResearchers', data,room=UniqueID)
            i += 1

@socketio.on("ListResearchers")
def ListResearchers(data):
    nListResearchers = newListResearchers(data)
    nListResearchers.start()

class newListResearcherAccounts(threading.Thread):
    def __init__(self,data):
        super(newListResearcherAccounts,self).__init__()
        self.data = data
    def run(self):
        UniqueID = self.data['UniqueID']
        ListResearcherAccount = University.ListResearchAccounts()
        i = 0 
        socketio.emit('ClearElementsAccount',room=UniqueID)
        while(i < len(ListResearcherAccount)):
            data = {
                "Name" : ListResearcherAccount[i][0],
                "ID" : ListResearcherAccount[i][1]
            }
            socketio.emit('ResearcherAccountsUni', data,room=UniqueID)
            i += 1

@socketio.on("ListResearcherAccounts")
def ListResearcherAccounts(data):
    nListResearcherAccounts = newListResearcherAccounts(data)
    nListResearcherAccounts.start()

class newResearcherAccountsUniInfo(threading.Thread):
    def __init__(self,data):
        super(newResearcherAccountsUniInfo,self).__init__()
        self.data = data
    def run(self):
        UniqueID = self.data['UniqueID']
        socketio.emit('ClearElementsAccount',room=UniqueID)
        ResearchAccountInfoList = University.ResearchAccountInfo(self.data['ID'])
        data = {
            "Title" : ResearchAccountInfoList[0][0],
            "Budget" : ResearchAccountInfoList[0][1],
            "EndDate" : ResearchAccountInfoList[0][2]
        }
        socketio.emit('UniResearcherInfo',data,room=UniqueID)

@socketio.on("ResearcherAccountsUniInfo")
def ResearcherAccountsUniInfo(data):
    nResearcherAccountsUniInfo = newResearcherAccountsUniInfo(data)
    nResearcherAccountsUniInfo.start()

class newUserUniInfo(threading.Thread):
    def __init__(self,data):
        super(newUserUniInfo,self).__init__()
        self.data = data
    def run(self):
        UniqueID = self.data['UniqueID']
        ID = self.data["ID"]
        UserUniInfoList = University.UserInfo(ID)
        UserResearchAccountsList = University.UserResearchAccounts(ID)
        data = {
            "FullName" : UserUniInfoList[0][0],
            "Email" : UserUniInfoList[0][1],   
        }
        socketio.emit('ClearElementsAccount',room=UniqueID)
        socketio.emit('UniUserInfo',data,room=UniqueID)
        i = 0
        while(i < len(UserResearchAccountsList)):
            data = {
                "AccountTitle" : UserResearchAccountsList[i][0]
            }
            socketio.emit('UniUserInfoResearchAccounts',data,room=UniqueID)
            i += 1

@socketio.on("UserUniInfo")
def UserUniInfo(data):
    nUserUniInfo = newUserUniInfo(data)
    nUserUniInfo.start()

class newListNotificationsUni(threading.Thread):
    def __init__(self,data):
        super(newListNotificationsUni,self).__init__()
        self.data = data
    def run(self):
        UniqueID = self.data['UniqueID']
        ListNotifications = University.UniversityNotifications()
        i = 0
        while(i < len(ListNotifications)):
            data = {
                "Date" : ListNotifications[i][0],
                "Budget" : ListNotifications[i][1],
                "Researcher" : ListNotifications[i][2],
                "ID" : ListNotifications[i][3]
            }
            socketio.emit('ListNotificationsUni',data,room=UniqueID)
            i += 1

@socketio.on("ListNotificationsUni")
def ListNotificationsUni(data):
    nListNotificationsUni = newListNotificationsUni(data)
    nListNotificationsUni.start()

class newListTransactions(threading.Thread):
    def __init__(self,data):
        super(newListTransactions,self).__init__()
        self.data = data
    def run(self):
        UniqueID = self.data['UniqueID']
        TransactionsList = University.ListTransactions()
        i = 0
        socketio.emit('ClearElementsAccount',room=UniqueID)
        socketio.emit('TransactionsUniInitial',room=UniqueID)
        while(i < len(TransactionsList)):
            Name = University.getResearcher(TransactionsList[i][4])
            data = {
                "Amount" : TransactionsList[i][0],
                "Date" : TransactionsList[i][1],
                "Reason" : TransactionsList[i][2],
                "FundingAmount" : TransactionsList[i][3],
                "Name" : Name
            }
            socketio.emit('ListTransactionsUni', data,room=UniqueID)
            i += 1

@socketio.on("ListTransactions")
def ListTransactions(data):
    nListTransactions = newListTransactions(data)
    nListTransactions.start()

class newAcknowledge(threading.Thread):
    def __init__(self,data):
        super(newAcknowledge,self).__init__()
        self.data = data
    def run(self):
        University.AcknowledgeNotification(self.data["ID"])

@socketio.on("Acknowledge")
def Acknowledge(data):
    nAcknowledge = newAcknowledge(data)
    nAcknowledge.start()


if __name__ == "__main__":
    socketio.run(app, debug = True,port=5000)
