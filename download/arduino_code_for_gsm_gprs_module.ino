#include "SIM900.h"
#include <SoftwareSerial.h>
//#include "inetGSM.h"
#include "sms.h"
//#include "call.h"
#include "gps.h"


//To change pins for Software Serial, use the two lines in GSM.cpp.

//GSM Shield for Arduino
//www.open-electronics.org
//this code is based on the example of Arduino Labs.

//Simple sketch to start a connection as client.

//InetGSM inet;
//CallGSM call;
SMSGSM sms;
GPSGSM gps;

char lon[15];
char lat[15];
char alt[15];
char time[20];
char vel[15];
int i=0;
boolean started=false;

char msg[60];

char phone_num[20]; // array for the phone number string
char sms_text[100]; // array for the SMS text string
void setup() 
{
  //Serial connection.
  Serial.begin(9600);
  Serial.println("GSM Shield testing.");
  //Start configuration of shield with baudrate.
  //For http uses is raccomanded to use 4800 or slower.
  if (gsm.begin(2400)){
    Serial.println("\nstatus=READY");
    gsm.forceON();  //To ensure that SIM908 is not only in charge mode
    started=true;  
  }
  else Serial.println("\nstatus=IDLE");
  
  if(started){
    //GPS attach
    Serial.println("Attaching gps...");
    gps.attachGPS();
    Serial.println("Getting coordinates....");
    gps.getPar(alt,time,lat,lon,vel);
    strcat(msg,"{\"lat\":\"");
    Serial.println(lat);
    strcat(msg,lat);
    strcat(msg,"\",\"log\":\"");
    Serial.println(lon);
    strcat(msg,lon);
    strcat(msg,"\"}");
    Serial.println(msg);
    //if (sms.SendSMS("+918686406554", msg))
      //Serial.println("\nSMS sent OK");
    //gsm.SendATCmdWaitResp(F("AT+CGNSPWR=0"), 500, 50, "OK", 5);
    gsm.SendATCmdWaitResp(F("AT+CMGF=1"), 500, 50, "OK", 5);
    gsm.SendATCmdWaitResp(F("AT+CPMS=\"SM\""), 500, 50, "OK", 5); 
    Serial.println("Reading sms....");     
    //gsm.SendATCmdWaitResp(F("AT+CMGL=\"ALL\""), 500, 50, "OK", 5); 
    //gsm.SendATCmdWaitResp(F("AT+CMGR=1,0"), 500, 50, "OK", 5);           
  }
};

void loop() 
{
  while(true){
      strcpy(sms_text,"");
      sms.GetSMS(1,phone_num, sms_text, 100);
      //Serial.println(phone_num);
      //Serial.println(sms_text);
      if(strcmp(sms_text,"es")==0){
        Serial.println(phone_num);
        Serial.println("Getting coordinates....");
          gps.getPar(alt,time,lat,lon,vel);
          strcpy(msg,"");
          strcat(msg,"{\"lat\":\"");
          Serial.println(lat);
          strcat(msg,lat);
          strcat(msg,"\",\"log\":\"");
          Serial.println(lon);
          strcat(msg,lon);
          strcat(msg,"\"}");
          //Serial.println(msg);
          sms.GetSMS(1,phone_num, sms_text, 100);
          Serial.println(phone_num);
          Serial.println(msg);
        if (sms.SendSMS(phone_num, msg))
            Serial.println("\nSMS sent OK");
          if(sms.DeleteSMS(1)==1)
          {
            Serial.println("Deleted");
          }
        break;
      }
      else{
        Serial.println("Incorrect");  
        if(sms.DeleteSMS(1)==1)
        {
            Serial.println("Deleted");
        }
        break;
      } 
  }
  
};

