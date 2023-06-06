

import sqlite3

import threading                                                                
import functools    
import time   

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
    #create researcher accounts table
    c.execute('''CREATE TABLE IF NOT EXISTS ResearcherAccounts
           (ID INT PRIMARY KEY     NOT NULL,
            FullName           TEXT    NOT NULL,
            Email     TEXT     NOT NULL,
            Password    TEXT       NOT NULL);''')
    conn.commit()         
 
@synchronized
def dropTables():
    conn = sqlite3.connect('test.db')
    c = conn.cursor()
    c.execute('DROP TABLE ResearcherAccounts')
    c.execute('DROP TABLE AcceptedProposals')
    c.execute('DROP TABLE RejectedProposals')
    c.execute('DROP TABLE IdleProposals')
    c.execute('DROP TABLE ResearchAccount')
    c.execute('DROP TABLE AddResearchers')
    c.execute('DROP TABLE Transactions')
    c.execute('DROP TABLE UniversityNotifications')

@synchronized
def CreateAccount(Name, Email, Password):
    conn = sqlite3.connect('test.db')
    c = conn.cursor()
    # get last ID, new ID + 1
    c.execute(' SELECT ID FROM ResearcherAccounts ')
    infoID = c.fetchall()
    if(len(infoID) > 0):
        IDtmp = infoID[len(infoID) - 1][0]
    else:
        IDtmp = 0
    # add details into researcher accounts 
    sql = "INSERT INTO ResearcherAccounts (ID, FullName, Email,Password) VALUES (?,?,?,?)"
    val = (IDtmp + 1, Name, Email,Password)
    c.execute(sql, val)
    conn.commit()

@synchronized
def LoginAccount(NameEmail,Password):
    conn = sqlite3.connect('test.db')
    c = conn.cursor()
    c.execute('SELECT FullName,Email,Password,ID FROM ResearcherAccounts')
    information = c.fetchall()
    i = 0
    # if details in database log in
    while(i < len(information)):
        if((information[i][0] == NameEmail or information[i][1] == NameEmail) and information[i][2] == Password):
            ytupl = (True,information[i][3])
            return ytupl
        i += 1
    return (False,0)

@synchronized
def ListResearchers():
    conn = sqlite3.connect('test.db')
    c = conn.cursor()
    c.execute('SELECT FullName,ID FROM ResearcherAccounts')
    information = c.fetchall()
    return information

@synchronized
def ListResearchersRemove(AccountHolderID):
    conn = sqlite3.connect('test.db')
    c = conn.cursor()
    sql = 'SELECT ResearcherName,ResearcherAddedID FROM AddResearchers WHERE ResearchAccountID = ?'
    val = (AccountHolderID,)
    c.execute(sql, val)
    information = c.fetchall()
    return information

@synchronized
def RemoveResearchers(ResearcherAddedID,HolderAccountID):
    conn = sqlite3.connect('test.db')
    c = conn.cursor()
    sql = "DELETE FROM AddResearchers WHERE ResearcherAddedID = ? AND ResearchAccountID = ?"
    val = (ResearcherAddedID,HolderAccountID)
    c.execute(sql, val)
    conn.commit()
    c.close()

@synchronized
def SearchResearchers(SearchString):
    conn = sqlite3.connect('test.db')
    c = conn.cursor()
    SearchString = SearchString.upper()
    # search for researchers based on if similar string using LIKE
    sql = 'SELECT FullName,ID FROM ResearcherAccounts WHERE UPPER(FullName) LIKE ?'
    val = ('%'+ SearchString +'%',)
    c.execute(sql, val)
    information = c.fetchall()
    return information

@synchronized
def AddResearcher(ID,Name,ResearcherAddedID,AccountID,ResearcherID):
    conn = sqlite3.connect('test.db')
    c = conn.cursor()
    c.execute(' SELECT ID FROM AddResearchers ')
    # last ID + 1
    infoID = c.fetchall()
    if(len(infoID) == 0):
        IDtmp = 0
    else:
        IDtmp = infoID[len(infoID) - 1][0]
    # check if researcher already added
    sql = ' SELECT count(*) FROM AddResearchers WHERE ResearcherAddedID = ? AND  ResearchAccountID = ? '
    val = (ResearcherAddedID,AccountID)
    c.execute(sql, val)
    researcherAlready = c.fetchall()
    # if not add into database
    if(researcherAlready[0][0] == 0):
        sql = "insert into AddResearchers (ID, ResearcherID,  ResearcherAddedID,ResearchAccountID, ResearcherName, OriginialResearchID) values (?, ?, ?,?,?,?  )"
        val = (IDtmp + 1,ResearcherID, ResearcherAddedID, AccountID,Name,ID)
        c.execute(sql, val)
        conn.commit()
        c.close()

@synchronized
def getAddedAcounts(ResearcherID):
    conn = sqlite3.connect('test.db')
    c = conn.cursor()
    sql = "SELECT ResearchAccountID,ResearcherID FROM AddResearchers WHERE ResearcherAddedID = ?"
    val = (ResearcherID,)
    c.execute(sql, val)
    information = c.fetchall()
    return information

@synchronized
def getResearchAccountTitles(AccountID):
    conn = sqlite3.connect('test.db')
    c = conn.cursor()
    sql = "SELECT Title FROM ResearchAccount WHERE ID = ?"
    val = (str(AccountID),)
    c.execute(sql, val)
    information = c.fetchall()
    return information
