# School of Computing &mdash; Year 4 Project Proposal Form

> Edit (then commit and push) this document to complete your proposal form.
> Make use of figures / diagrams where appropriate.
>
> Do not rename this file.

## SECTION A

|                     |                   |
|---------------------|-------------------|
|Project Title:       | Autotracer           |
|Student 1 Name:      | BrianQuilty           |
|Student 1 ID:        | 18373856            |
|Student 2 Name:      | ConorMarsh            |
|Student 2 ID:        | 19728351            |
|Project Supervisor:  | PaulClarke            |

> Ensure that the Supervisor formally agrees to supervise your project; this is only recognised once the
> Supervisor assigns herself/himself via the project Dashboard.
>
> Project proposals without an assigned
> Supervisor will not be accepted for presentation to the Approval Panel.

## SECTION B

> Guidance: This document is expected to be approximately 3 pages in length, but it can exceed this page limit.
> It is also permissible to carry forward content from this proposal to your later documents (e.g. functional
> specification) as appropriate.
>
> Your proposal must include *at least* the following sections.


### Introduction

Our project is about a driving assistant which aims to help drivers driving on the road. It will aim to track the travel speed of the vehicle you are in and also notify you with voice assistance to help determine if you have gone over the speed limit of the road you are travelling. Through machine learning the application will alert the user on how fast they should be driving. Using object detection, traffic light's changing color and oncomming traffic alerts that will be detected through the phone's camera will be sent to the user.

### Outline

- System used for everyday people who drive.
- Using longitude co-ordinates will determine the travel speed of the vehicle.
- When speed limit is reached on the road you are on a voice message will be transmitted notifying the user.
- Machine learning to recommend if a user should be driving faster or slower.
- Based on the User's average speed in relation to the speed limit of the road, system will be able to analyse and draw conclusions
around the data on whether the user should be going faster or slower.
- Using the camera from the phone, the system will be able to detect oncoming traffic alerting the user if they are too close to a vehicle and alerting them with the correct distance the car should be from any vehicles infront.
- Aswell as this the camera will be able to detect traffic lights, alerting the user through voice if they should halt or proceed.

### Background

> The ideas were originally suggested by Prof. Paul Clarke.
> Both team members had the desire to create a project which would help facilitate car drivers and particularly learner drivers of student age.
> Both students have a desire to expand their knowledge in the niche areas of computer vision and machine learning.
> It was agreed that using a phone camera would be a convenient and interesting way to introduce computer vision in terms of traffic detection, in a way which could be easily tested in practice.
> Longitude co-ordinates if applicable with the Google/Bing Maps API will provide an accurate way of determining vehicle speed which will be integral to various aspects of the project.
### Achievements

> The main function of the project will be to improve road safety for the user.
> It is intended that the functioning of this project be as convenient as possible for the average mobile user.
> The project/app should provide the user with accurate and useful speed limit information which it will calculate itself.
> The project will explore to the best extent possible how voice messaging, machine learning and object detection can be implemented with a view to improving road safety measures.
> The target user of this project will be any car driver with a leaning towards learner drivers who are still familiarising with rules of the road.
### Justification

> The project/app will be particularly useful for any driver who is driving in an unfamiliar area where road rules and speed limits may not be immediately obvious to them.
> It is also intended to make it as useful as possible for learner drivers and act as an aid towards learning some of the fundamental concepts of safe road use.
> The students undertaking the project agree that driver assistance technology is a key developing area of IT and want to use the experience to test new possibilities and concepts in this area.
> We believe our concepts are novel and if we can put them into practice we believe we will be breaking some new ground in improving road safety measures through technology.
> It is the aim to make this project as user-friendly and practical as possible by enabling it to be used in any moving vehicle with no further requirements than a typical smartphone.
### Programming language(s)

Java
Python
XML
MySQL

### Programming tools / Tech stack

Android Studio
MySQL
Gitlab


### Hardware

Phones with android operating system.

### Learning Challenges

- Coding an application that is based on another machine(Car).
- Neither of us have experience with machine learning.
- Coding voice alerts.
- Object detection is a first for both of us, especially as using opencv and tensorflow through android studio is uncommon.
- Pair programming for both of us was never really accomplished in our third year project. We believe that placing an emphasis
on this will benefit our project and also give us a new learning obstacle to overcome.
- Making sure we set appropriate deadlines so we complete our project on time and are not rushing the project and leaving it to the 
last minute.

### Breakdown of work

Altough each of us have seperate responsibilities and different areas of the project we are working on, a collective effort is emphasised in each area. Meaning that there may be some crossover and we may help each other to fufill the different parts of the project. This will be achieved through pair programming. 

#### Student 1

BrianQuilty

- Implementing the speed calculation feature.
- Determining if the speed limit is reached.
- Using machine learning to recommend if a user should be driving faster or slower.

#### Student 2

ConorMarsh

- Implementing object detection of other road vehicles
- Implementing traffic light detection system
- Responsible for coding voice alerts

## Example

> Example: Here's how you can include images in markdown documents...

<!-- Basically, just use HTML! -->

<p align="center">
  <img src="./res/cat.png" width="300px">
</p>

