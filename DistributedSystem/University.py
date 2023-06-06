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
def ListResearchAccounts():
    conn = sqlite3.connect('test.db')
    c = conn.cursor()
    c.execute('SELECT Title, ID FROM ResearchAccount')
    information = c.fetchall()
    return information

@synchronized
def ListTransactions():
    # list all transactions in database
    print("fsa")
    conn = sqlite3.connect('test.db')
    c = conn.cursor()
    c.execute('SELECT amount,date,Reason,FundingAmount,ResearcherID FROM Transactions')
    information = c.fetchall()
    print(information)
    return information

@synchronized
def getResearcher(ID):
    conn = sqlite3.connect('test.db')
    c = conn.cursor()
    # get researcher name based on id parameter
    sql = 'SELECT FullName FROM ResearcherAccounts WHERE ID = ?'
    val = (str(ID),)
    c.execute(sql, val)
    information = c.fetchall()
    return information

@synchronized
def UniversityNotifications():
    conn = sqlite3.connect('test.db')
    c = conn.cursor()
    #  list all university notifications
    c.execute('SELECT date,budget,researcher,ID FROM UniversityNotifications ')
    information = c.fetchall()
    return information

@synchronized
def ResearchAccountInfo(ID):
    conn = sqlite3.connect('test.db')
    c = conn.cursor()
    # get research account info based around id 
    sql = 'SELECT Title, Budget, EndDate FROM ResearchAccount WHERE ID = ?'
    val = (ID,)
    c.execute(sql, val)
    information = c.fetchall()
    return information

@synchronized
def UserInfo(ID):
    conn = sqlite3.connect('test.db')
    c = conn.cursor()
    sql = 'SELECT FullName, Email FROM ResearcherAccounts WHERE ID = ?'
    val = (ID,)
    c.execute(sql, val)
    information = c.fetchall()
    return information

@synchronized
def UserResearchAccounts(ID):
    conn = sqlite3.connect('test.db')
    c = conn.cursor()
    sql = 'SELECT Title FROM ResearchAccount WHERE ResearcherID = ?'
    val = (ID)
    c.execute(sql, val)
    information = c.fetchall()
    return information

@synchronized
def AcknowledgeNotification(ID):
    conn = sqlite3.connect('test.db')
    c = conn.cursor()
    # acknowledge notification = delete from database
    sql = "DELETE FROM UniversityNotifications WHERE id = ?"
    val = (ID,)
    c.execute(sql, val)
    conn.commit()
    conn.close()
    return 
