Created by: Mr. Subhasis Mukhopadhyay
Mentored by: Mr. Saurabh Kumar
Project Head: Dr. Sandeep K Shukla

Work done by the Application:

Reading all the contacts, call logs and messages from a Android Phone and upload it in a database.

App Name: Spy

Currently the app can store data in local database and the phone should be connected in the same network. To use the app 
in any device following things needs to be done: 

1) Create a website.
2) Put the three php files namely log.php, cont.php and message.php in the home directory of the website.
3) Open the source code of the Android Application.
4) You can find 'public static String ip="172.27.27.89";' in MainActivity.java, call_logs.java and messages.java
5) Just change the "172.27.27.89" to the website name you have created. Example: public static String ip="www.mysite.com";

You are done.

For some other info, MainActivity deals with the contacts part, messages deals with the message part, call_logs deals with
the call logs part and PostData.java send all the data to the database.
