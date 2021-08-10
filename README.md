# tCHu_projetBA2

## Usage :

For a local game : run Stage11Test which is under TEST/ch.epfl/tchu/gui/Stage11Test
If you wanna play with friends through the internet, install ngrok and follow my youtube tutorial : https://youtu.be/KMK-xSsbpQs

## Prerequisites : 

Depending on if you use intellij or eclipse, you have to install javafx on your device :
https://cs108.epfl.ch/archive/21/g/openjfx.html


on intellij, I made a youtube tutorial video on How to test out the game :
https://youtu.be/KMK-xSsbpQs


how to use ngrok :
1) create a free account :  https://dashboard.ngrok.com/login
2) download ngrok : https://ngrok.com/download
3) connect ngrok to your account using the command :

./ngrok authtoken <your_token> (if you run ngrok directly, no need to use ./)

your authentification token is here : https://dashboard.ngrok.com/get-started/your-authtoken

4) ./ngrok tcp 5108 --region eu (without ./ if writing on ngrok directly)
    this will establish an access to your localhost on the socket 5108 (the tChu server default socket)


Session Status                online                                                                                              
Account                       Nom Prénom (Plan: Free)                                                                         
Version                       2.3.40                                                                                              
Region                        Europe (eu)                                                                                   
Web Interface                 http://127.0.0.1:4040                                                                               
Forwarding                    tcp://2.tcp.ngrok.io:14491 -> localhost:5108                                                        
                                                                                                                                  
Connections                   ttl     opn     rt1     rt5     p50     p90                                                         
                              2       0       0.00    0.00    948.48  1750.43



5) under "environment variable" of the class ServerMain, write:  2.tcp.ngrok.io:14491 (you will have a different number)
  Run the ServerMain, then you can ask your friend to run his/her ClientMain.java class.
  
  Enjoy :D







