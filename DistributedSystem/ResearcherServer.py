
from flask import Flask, render_template, request, redirect, url_for, session
from flask_socketio import SocketIO,join_room
import subprocess
import FundingAgency
import Researchers
import University
import uuid
from werkzeug.exceptions import HTTPException

from multiprocessing import Process


import threading
app = Flask(__name__)

socketio = SocketIO(app)
ResearcherIDentifier = 1

isExcp = False

def messageReceived(methods=['GET', 'POST']):
    print('message was received!!!')


class NewCreateTable(threading.Thread):
    def __init__(self):
        super(NewCreateTable,self).__init__()
    def run(self):
        FundingAgency.createTable()

@app.route('/')
def LandingPage():
    nthread = NewCreateTable()
    nthread.start()
    return render_template('LandingPage.html')


@app.route('/ResearchForm')
def ResearchForm():
    data = {'ResearcherId': ResearcherIDentifier}
    return render_template('ResearchersHTML.html', data=data)

@app.route('/CreateAccount')
def CreateAccountInitial():
    return render_template('CreateAccountHTML.html')

@app.route('/LoginAccount')
def LoginAccountInitial():
    return render_template('LoginAccountHTML.html')

@app.route('/ResearchProposals',methods=['GET', 'POST'])
def ResearchProposals():
    data = {'ResearcherId': ResearcherIDentifier}
    return render_template('ResearcherProposalsHTML.html', data=data)

@socketio.on("onconnect")
def oncconnect():
    # join room with unique id
    UniqueID = str(uuid.uuid4())
    join_room(UniqueID)
    data = {
        "UniqueID" : UniqueID
    }
    socketio.emit('connected',data,room=UniqueID)

class NewAccountsAdded(threading.Thread):
    def __init__(self,data):
        super(NewAccountsAdded,self).__init__()
        self.data = data
    def run(self):
        UniqueID = self.data['UniqueID']
        ID = self.data['ID']
        # get all accounts added from id
        AddedAcountsList = Researchers.getAddedAcounts(ID)
        i = 0
        while(i < len(AddedAcountsList)):
            # get title of research account using ID from AddedAcountsList
            title = Researchers.getResearchAccountTitles(AddedAcountsList[i][0])
            data = {
                    "Title" : title,
                    "AccountID" : AddedAcountsList[i][0],
                    "ResearcherId" : AddedAcountsList[i][1]
            }
            i += 1
            # send to html
            socketio.emit('ResearcherProposalTitlesAdd', data,room=UniqueID)


@socketio.on("AccountsAdded")
def AccountsAdded(data):
    nAccountAdd = NewAccountsAdded(data)
    nAccountAdd.start()

class NewListResearchers(threading.Thread):
    def __init__(self,data):
        super(NewListResearchers,self).__init__()
        self.data = data
    def run(self):
        # get list of researchers
        ListResearcherslist = Researchers.ListResearchers()
        i = 0
        UniqueID = self.data['UniqueID']
        # clearn html elements
        socketio.emit('ClearElementsAccount',room=UniqueID)
        socketio.emit('ResearchSearchBar',room=UniqueID)
        while(i < len(ListResearcherslist)):
            data = {
                "Name" : ListResearcherslist[i][0],
                "ID" : ListResearcherslist[i][1],
                "HolderID" : i + 1
            }
            # display html elements
            socketio.emit('ListResearchers', data,room=UniqueID)
            i += 1


@socketio.on("ListResearchers")
def ListResearchers(data):
    lResearch = NewListResearchers(data)
    lResearch.start()


class NewCreateAccount(threading.Thread):
    def __init__(self,data):
        super(NewCreateAccount,self).__init__()
        self.data = data
    def run(self):
        # create tables.
        Researchers.createTable()
        Researchers.CreateAccount(self.data['Name'],self.data['Email'],self.data['Password'])

@socketio.on("CreateAccount")
def CreateAccount(data):
    cAccount = NewCreateAccount(data)
    cAccount.start()

class NewLoginAccount(threading.Thread):
    def __init__(self,data):
        super(NewLoginAccount,self).__init__()
        self.data = data
    def run(self):
        UniqueID = self.data['UniqueID']
        # check if login credentials correct
        LoginData = Researchers.LoginAccount(self.data['EmailName'],self.data['Password'])
        if(LoginData[0] == True):
            global ResearcherIDentifier
            ResearcherIDentifier = LoginData[1]
            socketio.emit('LoginToAccount',room=UniqueID)

@socketio.on("LoginAccount")
def LoginAccount(data):
    lAccount = NewLoginAccount(data)
    lAccount.start()

class NewSubmitProposal(threading.Thread):
    def __init__(self,data):
        super(NewSubmitProposal,self).__init__()
        self.data = data
    def run(self):
        # submit proposal
        FundingAgency.ProposalSubmited(self.data['Title'],self.data['ProjectDescription'],self.data['FundAmount'],self.data['ResearcherId'])

@socketio.on("SubmitProposal")
def message(data):
    sProposal = NewSubmitProposal(data)
    sProposal.start()

class NewSearchR(threading.Thread):
    def __init__(self,data):
        super(NewSearchR,self).__init__()
        self.data = data
    def run(self):
        searchList = Researchers.SearchResearchers(self.data['searchString'])
        UniqueID = self.data['UniqueID']
        i = 0
        # clear html elements
        socketio.emit('ClearElementsAccount',room=UniqueID)
        socketio.emit('ResearchSearchBar',room=UniqueID)
        while(i < len(searchList)):
            data = {
                "Name" : searchList[i][0],
                "ID" : searchList[i][1]
            }
            # list researchers in html
            socketio.emit('ListResearchers', data,room=UniqueID)
            i += 1

@socketio.on("SearchForResearcher")
def SearchForResearcher(data):
    nSearch = NewSearchR(data)
    nSearch.start()

class NewAddResearcher(threading.Thread):
    def __init__(self,data):
        super(NewAddResearcher,self).__init__()
        self.data = data
    def run(self):
        Researchers.AddResearcher(self.data['ID'],self.data['Name'],self.data['ResearcherAddedID'],self.data['AccountID'],self.data['ResearcherID'])

@socketio.on("AddResearcher")
def AddResearcher(data):
    NAddR = NewAddResearcher(data)
    NAddR.start()


class NewProposalsResearcher(threading.Thread):
    def __init__(self,data):
        super(NewProposalsResearcher,self).__init__()
        self.data = data
    def run(self):
        UniqueID = self.data['UniqueID']
        # get research proposals from data
        proposalResearcherList = FundingAgency.getProposalsResearchers(self.data['proposal'],self.data['ResearcherId'])
        Table = self.data['proposal']
        i = 0
        UniqueID = self.data['UniqueID']
        # check which proposal table
        if(Table == "AcceptedProposals"):
            while(i < len(proposalResearcherList)):
                ProposalTitle = proposalResearcherList[i][0]
                data = {
                    "Title" : ProposalTitle,
                    "HolderID" : i + 1,
                    "ResearcherId" : self.data['ResearcherId'],
                    "Table" : Table
                }
                # list html accept title elements
                socketio.emit('ResearcherProposalTitlesAccept', data,room=UniqueID)
                i += 1
        else:
            while(i < len(proposalResearcherList)):
                ProposalTitle = proposalResearcherList[i][0]
                data = {
                    "Title" : ProposalTitle,
                    "ID" : i + 1,
                    "ResearcherId" : self.data['ResearcherId'],
                    "Table" : Table,
                }
                socketio.emit('ResearcherProposalTitles', data,room=UniqueID)
                i += 1


@socketio.on("getProposalsResearcher")
def getProposalsResearcher(data):
    NProposalsResearcher = NewProposalsResearcher(data)
    NProposalsResearcher.start()

class NewResearchAccountDetails(threading.Thread):
    def __init__(self,data):
        super(NewResearchAccountDetails,self).__init__()
        self.data = data
    def run(self):
        UniqueID = self.data['UniqueID']
        accountInfo = FundingAgency.researchAccountInformation(self.data['ID'],self.data['HolderID'])
        data = {
                "Title" : accountInfo[0][0],
                "Budget" : accountInfo[0][1],
                "EndDate" : accountInfo[0][2],
            }
        socketio.emit('ResearcherDetails', data,room=UniqueID)

@socketio.on("ResearchAccountDetails")
def ResearchAccountDetails(data):
    NewResearchAccDetails = NewResearchAccountDetails(data)
    NewResearchAccDetails.start()

class NewWithdrawFunds(threading.Thread):
    def __init__(self,data):
        super(NewWithdrawFunds,self).__init__()
        self.data = data
    def run(self):
        FundingAgency.WithdrawFunds(self.data['ID'], self.data['HolderID'], self.data['Funds'], self.data['Reason'])

@socketio.on("WithdrawFunds")
def WithdrawFunds(data):
    NWithdrawFunds = NewWithdrawFunds(data)
    NWithdrawFunds.start()

class NewTransactions(threading.Thread):
    def __init__(self,data):
        super(NewTransactions,self).__init__()
        self.data = data
    def run(self):
        UniqueID = self.data['UniqueID']
        # list all transactions given IDS
        Transactionsall = FundingAgency.Transactions(self.data['ID'],self.data['HolderID'])
        i = 0
        socketio.emit('ClearElementsAccount',room=UniqueID)
        while(i < len(Transactionsall)):
            data = {
                    "Date" : Transactionsall[i][0],
                    "Amount" : Transactionsall[i][1],
                    "Reason" : Transactionsall[i][2],
                    "TotalAmount" : Transactionsall[i][3],
                }
            socketio.emit('TransactionsShow', data,room=UniqueID)
            i += 1

@socketio.on("Transactions")
def Transactions(data):
    nTransactions = NewTransactions(data)
    nTransactions.start()

class NewRemoveResearcherList(threading.Thread):
    def __init__(self,data):
        super(NewRemoveResearcherList,self).__init__()
        self.data = data
    def run(self):
        UniqueID = self.data['UniqueID']
        # list all researchers that can be removed from account
        ListResearchersRemove = Researchers.ListResearchersRemove(self.data['HolderAccountID'])
        i = 0
        socketio.emit('ClearElementsAccount',room=UniqueID)
        while(i < len(ListResearchersRemove)):
            data = {
                    "Name" : ListResearchersRemove[i][0],
                    "ID" : ListResearchersRemove[i][1]
                }
            # display in html
            socketio.emit('ListResearchersRemove', data,room=UniqueID)
            i += 1

@socketio.on("RemoveResearcherList")
def RemoveResearch(data):
    NRemoveResearcherList = NewRemoveResearcherList(data)
    NRemoveResearcherList.start()

class NewRemoveResearcher(threading.Thread):
    def __init__(self,data):
        super(NewRemoveResearcher,self).__init__()
        self.data = data
    def run(self):
        Researchers.RemoveResearchers(self.data['ResearcherAddedID'],self.data['HolderAccountID'])

@socketio.on("RemoveResearcher")
def RemoveResearcher(data):
    NRemoveResearcher = NewRemoveResearcher(data)
    NRemoveResearcher.start()




if __name__ == "__main__":
    socketio.run(app, debug = True,port=5002)