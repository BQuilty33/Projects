CA4006 Assignment 1 - Description and Submission

Simulate a bookstore 



Problem specification:
A bookstore is open 24 hours a day, 365 days a year. The bookstore has multiple sections such as fiction, horror, romance, fantasy, poetry, and history.

Time in the bookstore is measured in ticks. There are 1000 ticks in a day. Every 100 ticks (on average) a delivery is made of 10 books, with a random number of books for each of the above categories (totaling 10) e.g., 4 for horror, 2 for poetry, 1 for history, etc.

When a delivery of books arrives, they are put into a box, where an assistant who works in the bookstore can put (stock) the books into their respective sections (assuming they are not busy). Only one person (e.g. assistant) can take books from the box at the same time. Once they are finished another assistant can take books from the box. Each assistant can carry up to 10 books at once. 

Every 10 ticks (on average) a customer will buy a book from one of the sections (randomly). If the section is empty, the customer will wait until a book for that section becomes available. This means there may be times where a particular section does not contain any books, and the customer will wait until a book for that section is available.  

It takes an assistant 10 ticks to walk from where the deliveries arrive to a particular section (e.g., to the fiction section), and 1 tick extra for every book they are carrying to that section. Additionally, for every book they put on the shelf, it takes 1 tick. In this example, it would take 20 ticks for an assistant to carry 10 books to the fiction section, and another 10 ticks to stock that section with all 10 books. If they return to the delivery area where books arrive from any section, it will take 10 ticks. 

If an assistant is carrying books to stock multiple sections, it will take them 10 ticks to walk from one section to another section to begin stocking that section plus 1 tick for every remaining book (to be stocked) that they are carrying. For example, they may stock some books in the fiction section first, and then carry the remaining books to another section. When they are finished, they return to the delivery area to see if there are more books to be stocked, if not, they wait. The journey from any section back to the delivery area where books arrive takes 10 ticks. You can assume that it takes 0 ticks for an assistant to  take books from the box.

Note: Given that 100 books arrive (on average) per day to the bookstore, and 100 books will be bought (on average) per day by customers, there will be times where some sections may not contain any books. In these instances, customers will need to wait for the book to be available. 


Task:
Design a software system to simulate the concurrent operation of the bookshop. Assume that books are the resources, and the different activities are conducted in threads e.g., assistants, customers, etc. 


