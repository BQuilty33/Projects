import os 


import subprocess
from datetime import date, datetime
from dateutil.relativedelta import relativedelta
import threading                                                                
import functools                                                                
import time  
import sqlite3 
    
def synchronized(wrapped):                                                      
    lock = threading.Lock()                                                     
    print (lock, id(lock)   )                                                     
    @functools.wraps(wrapped)                                                   
    def _wrap(*args, **kwargs):                                                 
        with lock:                                                              
            print ("Calling '%s' with Lock %s from thread %s [%s]"              
                   % (wrapped.__name__, id(lock),                               
                   threading.current_thread().name, time.time()))               
            result = wrapped(*args, **kwargs)                                   
            print ("Done '%s' with Lock %s from thread %s [%s]"                 
                   % (wrapped.__name__, id(lock),                               
                   threading.current_thread().name, time.time()))               
            return result                                                       
    return _wrap 

@synchronized
def createTable():
    conn = sqlite3.connect('test.db')
    c = conn.cursor()
    # create all tables.
    c.execute('''CREATE TABLE IF NOT EXISTS AcceptedProposals
            (ID INT PRIMARY KEY     NOT NULL,
            ResearcherID    Int       NOT NULL,
            Title           TEXT    NOT NULL,
            ProjectDescription     TEXT     NOT NULL,
            FundingAmount    Int       NOT NULL);''')
    c.execute('''CREATE TABLE IF NOT EXISTS RejectedProposals
            (ID INT PRIMARY KEY     NOT NULL,
            ResearcherID    Int       NOT NULL,
            Title           TEXT    NOT NULL,
            ProjectDescription     TEXT     NOT NULL,
            FundingAmount    Int       NOT NULL);''')
    c.execute('''CREATE TABLE IF NOT EXISTS IdleProposals
            (ID INT PRIMARY KEY     NOT NULL,
            ResearcherID    Int       NOT NULL,
            Title           TEXT    NOT NULL,
            ProjectDescription     TEXT     NOT NULL,
            FundingAmount    Int       NOT NULL);''')
    c.execute('''CREATE TABLE IF NOT EXISTS ResearchAccount
            (ID INT PRIMARY KEY     NOT NULL,
            HolderID        Int     NOT NULL,
            ResearcherID    Int       NOT NULL,
            Title           Text        NOT NULL,
            Budget           TEXT    NOT NULL,
            EndDate     TEXT     NOT NULL);''')
    c.execute('''CREATE TABLE IF NOT EXISTS AddResearchers
            (ID INT PRIMARY KEY     NOT NULL,
            ResearcherID        Int     NOT NULL,
            ResearcherAddedID   Int     NOT NULL,
            ResearchAccountID   Int     NOT NULL,
            ResearcherName           TEXT    NOT NULL,
            OriginialResearchID     Int    NOT NULL );''')
    c.execute('''CREATE TABLE IF NOT EXISTS Transactions
            (ID INT PRIMARY KEY     NOT NULL,
            ResearcherID    Int       NOT NULL,
            AccountID       Int         NOT NULL,
            date           TEXT    NOT NULL,
            amount     Int     NOT NULL,
            Reason     Text     NOT NULL,
            FundingAmount    Int       NOT NULL);''')
    c.execute('''CREATE TABLE IF NOT EXISTS UniversityNotifications
            (ID INT PRIMARY KEY     NOT NULL,
            AccountID       Int         NOT NULL,
            date           TEXT    NOT NULL,
            Budget     Int     NOT NULL,
            Researcher     Text     NOT NULL);''')
    print("connected")
    conn.close()


@synchronized
def ProposalSubmited(Title, ProjectDescription,FundAmount, ResearchID):
    conn = sqlite3.connect('test.db')
    c = conn.cursor()
    # get last ID, current ID = Last + 1
    c.execute(' SELECT ID FROM AcceptedProposals ')
    infoID = c.fetchall()
    if(len(infoID) == 0):
        ID = 0
    else:
        ID = infoID[len(infoID) - 1][0]
    # get ID of proposal on position in the list
    sql = "SELECT count(*) FROM AcceptedProposals WHERE ResearcherID = ?"
    val = (ResearchID,)
    c.execute(sql, val)
    HolderID = c.fetchone()[0]
    print("fsa")
    # if 200-500 = accepted
    if(int(FundAmount) >= 200 and int(FundAmount) <= 500):
        sql = "insert into AcceptedProposals (ID, ResearcherID, Title, ProjectDescription,FundingAmount) values (?, ?, ?,?,? )"
        val = (ID + 1,ResearchID, Title, ProjectDescription,FundAmount)
        c.execute(sql, val)
        # default to 3 months for automatic acception 
        currentTimeDate = date.today() + relativedelta(months=3)
        currentTime = currentTimeDate.strftime('%Y-%m-%d')
        sql = "insert into ResearchAccount (ID, HolderID, ResearcherID, Title, Budget, EndDate) values (?, ?, ?,?,?,? )"
        val = (ID + 1,HolderID + 1,ResearchID,Title,FundAmount,currentTime)
        c.execute(sql, val)
    # > 1000 rejected
    elif(int(FundAmount) >= 1000):
        c.execute(' SELECT ID FROM RejectedProposals ')
        infoID = c.fetchall()
        if(len(infoID) == 0):
            IDRej = 0
        else:
            IDRej = infoID[len(infoID) - 1][0]
        sql = "insert into RejectedProposals (ID, ResearcherID, Title, ProjectDescription,FundingAmount) values (?, ?, ?,?,?)"
        val = (IDRej + 1,ResearchID, Title, ProjectDescription,FundAmount)
        c.execute(sql,val)
    # neither = idle
    else:
        c.execute(' SELECT ID FROM IdleProposals ')
        infoID = c.fetchall()
        if(len(infoID) == 0):
            IDIdle = 0
        else:
            IDIdle = infoID[len(infoID) - 1][0]
        sql = "insert into IdleProposals (ID, ResearcherID, Title, ProjectDescription,FundingAmount) values (?, ?, ?,?,?)"
        val = (IDIdle + 1,ResearchID, Title, ProjectDescription,FundAmount)
        c.execute(sql,val)
    conn.commit()
    conn.close()

@synchronized
def WithdrawFunds(ResearcherID, HolderID, WithdrawAmount,Reason):
    conn = sqlite3.connect('test.db')
    c = conn.cursor()
    sql = 'SELECT Budget,EndDate FROM ResearchAccount WHERE HolderID= ? AND ResearcherID= ?' 
    vals = (HolderID,ResearcherID)
    c.execute(sql, vals)
    information = c.fetchall()
    # new balance is the current budget - withdraw amount
    WithdrawNew = int(information[0][0]) - int(WithdrawAmount)
    EndDate = information[0][1]
    # get current date
    today = date.today()
    date_format = '%Y-%m-%d'
    date_obj = datetime.strptime(EndDate, date_format).date()
    #  ensure date of account hasnt passeds
    if(WithdrawNew >= 0 and today <= date_obj):
        currentTimeDate = date.today()
        c.execute(' SELECT ID FROM Transactions ')
        infoID = c.fetchall()
        if(len(infoID) == 0):
            ID = 0
        else:
            ID = infoID[len(infoID) - 1][0]
        # set budget to new budget got from withdrawnew
        sql = 'UPDATE ResearchAccount SET Budget = ? WHERE HolderID = ? AND ResearcherID = ?'
        val = (WithdrawNew, HolderID,ResearcherID)
        c.execute(sql, val)
        sql = "insert into Transactions (date, amount, Reason, FundingAmount,ID,ResearcherID,AccountID) values (?, ?, ?,?,?,?,?)"
        vals = (currentTimeDate,WithdrawAmount,Reason,WithdrawNew,ID + 1 ,ResearcherID,HolderID)
        c.execute(sql, vals)
        conn.commit()
        conn.close()

@synchronized
def Transactions(ResearcherID, HolderID):
    conn = sqlite3.connect('test.db')
    c = conn.cursor()
    sql = 'SELECT date,amount,Reason,FundingAmount FROM Transactions WHERE AccountID=? AND ResearcherID= ?'
    val = (HolderID,ResearcherID)
    c.execute(sql, val)
    information = c.fetchall()
    return information

@synchronized
def ProposalAccepted(ID,Budget,EndDate):
    conn = sqlite3.connect('test.db')
    c = conn.cursor()
    c.execute(' SELECT ID FROM AcceptedProposals ')
    infoID = c.fetchall()
    if(len(infoID) == 0):
        IDtmp = 0
    else:
        IDtmp = infoID[len(infoID) - 1][0]
    # get idle proposal info
    sql = 'SELECT Title,ResearcherID,ProjectDescription,FundingAmount FROM IdleProposals WHERE ID= ?'
    val = (ID,)
    c.execute(sql, val)
    information = c.fetchall()
    #  get id based of research account from idle proposal
    sql = ' SELECT count(*) FROM ResearchAccount WHERE ResearcherID=?'
    val = (str(information[0][1]),)
    c.execute(sql, val)
    HolderID = c.fetchone()[0]
    # insert idle proposal into accepted
    sql = "insert into AcceptedProposals (ID,Title, ResearcherID, ProjectDescription,FundingAmount) values (?,?, ?,?,?)"
    val = (IDtmp + 1, information[0][0], information[0][1], information[0][2],information[0][3])
    c.execute(sql, val)
    c.execute(' SELECT ID FROM ResearchAccount ')
    infoID = c.fetchall()
    if(len(infoID) == 0):
        AccountIDtmp = 0
    else:
        AccountIDtmp = infoID[len(infoID) - 1][0]
    # also insert into researchaccount
    sql = "insert into ResearchAccount (ID, HolderID, Title, ResearcherID, Budget, EndDate) values (?,?, ? , ?, ?,?)"
    val = (AccountIDtmp + 1,HolderID + 1, information[0][0], information[0][1], Budget,EndDate)
    c.execute(sql, val)
    #  then delete it from idle proposals
    sql = "DELETE FROM IdleProposals WHERE ID = ?"
    val = (ID,)
    #  notify university with details
    c.execute(sql, val)
    c.execute(' SELECT ID FROM UniversityNotifications ')
    infoID = c.fetchall()
    if(len(infoID) == 0):
        IDtmp3 = 0
    else:
        IDtmp3 = infoID[len(infoID) - 1][0]
    ResearcherName = getResearcher(information[0][1])[0][0]
    sql = "insert into UniversityNotifications (ID,AccountID,date,Budget,Researcher) values(?,?,?,?,?)"
    val = (IDtmp3 + 1,AccountIDtmp + 1,EndDate,Budget,ResearcherName)
    c.execute(sql, val)
    conn.commit()
    conn.close()

@synchronized
def ProposalRejected(ID):
    conn = sqlite3.connect('test.db')
    c = conn.cursor()
    c.execute(' SELECT ID FROM RejectedProposals ')
    infoID = c.fetchall()
    if(len(infoID) == 0):
        IDtmp = 0
    else:
        IDtmp = infoID[len(infoID) - 1][0]
    #  get idle proposal info
    sql = 'SELECT Title,ResearcherID,ProjectDescription,FundingAmount FROM IdleProposals WHERE ID=?'
    val = (ID,)
    c.execute(sql, val)
    information = c.fetchall()
    # insert into rejected proposals
    sql = "insert into RejectedProposals (ID, Title, ResearcherID,  ProjectDescription,FundingAmount) values (?, ?, ?,?,?)"
    val = (IDtmp + 1, information[0][0],information[0][1], information[0][2],information[0][3])
    c.execute(sql, val)
    # then remove from idle proposals
    sql = "DELETE FROM IdleProposals WHERE ID = ?"
    val = (ID,)
    c.execute(sql, val)
    conn.commit()
    conn.close()  

@synchronized
def getProposals(TableToGet):
    #  get proposals based on table
    conn = sqlite3.connect('test.db')
    c = conn.cursor()
    sql = 'SELECT Title,ID FROM ' + TableToGet
    c.execute(sql)
    titles = c.fetchall()
    conn.close()
    return titles

@synchronized
def getProposalsResearchers(TableToGet,ResearcherID):
    conn = sqlite3.connect('test.db')
    c = conn.cursor()
    c.execute('SELECT * FROM ' + TableToGet)
    tlkf = c.fetchall()
    sql = 'SELECT Title FROM ' + TableToGet + ' WHERE ResearcherID = ?'
    val = (ResearcherID,)
    c.execute(sql,val)
    titles = c.fetchall()
    return titles

@synchronized
def researchAccountInformation(ResearcherID,HolderID):
    #  get research account info based on holder ID = position in the list, and researcher ID
    conn = sqlite3.connect('test.db')
    c = conn.cursor()
    sql = 'SELECT Title,Budget,EndDate,HolderID,ResearcherID FROM ResearchAccount WHERE HolderID= ? AND ResearcherID= ?'
    val = (HolderID,ResearcherID)
    c.execute(sql, val)
    information = c.fetchall()
    return information

@synchronized
def getProposalInformation(TableToGet,ID):
    #  get proposal info
    conn = sqlite3.connect('test.db')
    c = conn.cursor()
    sql = 'SELECT Title,ProjectDescription,FundingAmount,ResearcherID FROM ' + TableToGet + ' WHERE ID=?'
    val = (ID,)
    c.execute(sql, val)
    information = c.fetchall()
    conn.close()
    return information

@synchronized
def getResearcher(ID):
    conn = sqlite3.connect('test.db')
    c = conn.cursor()
    c.execute('SELECT * FROM ResearcherAccounts')
    sql = 'SELECT FullName FROM ResearcherAccounts WHERE ID=?'
    val = (str(ID),)
    c.execute(sql, val)
    information = c.fetchall()
    return information