Problem specification:
Researchers write research proposals to apply for funding from major research funding agencies (e.g., Irish Research Council, Science Foundation Ireland, European Council, etc.). 


When a research project is approved, an associated research account is created at the university level and provisioned with the requested funding amount (in Euro). Funds on a research account can be used by university staff (mainly lecturers and post-doctoral researchers) working on the research project to buy research material (e.g., laptops and other consumables) and pay for their conference attendance.




Task:
Using distributed objects (e.g., RMI), Message Queuing System (e.g., RabbitMQ) or web services (REST) you should develop a distributed system implementing an online research funding management system with the following specifications: 


Researchers are able to submit research proposals to funding agencies with an acronym (can be seen as the id), title, project description, requested amount of funding. 

A funding agency has a defined call for applications with a total budget it is able to fund (e.g., research projects with a total of €1M). 

Assume that the funding agency approves all applications as long as they request between €200K and €500K and that the agency does not run out of money. 

The funding agency should confirm to the researcher whether the project is approved or not. Furthermore, the funding agency should notify DCU with the characteristics of the approved project (including its budget, the researcher who applied for it, and the end date). 

The university stores all information related to research accounts, users, and transactions in files (i.e., no database needed). 

The researcher who gets a project approved can: 

Add/remove additional researchers who can use the funds in the approved research account. 

Access details of their account (e.g., remaining budget and end date). 

List all the transactions on their research account (with date and amount). 

Researchers who are approved to use a research account can withdraw funds as long as the account has enough funds and that the end date is not passed. Your distributed system should have at least three hosts (i.e., one for a researcher, one for DCU, and one for the funding agency) and should be able to manage simultaneous requests from multiple researchers.


