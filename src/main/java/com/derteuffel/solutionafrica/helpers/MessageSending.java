package com.derteuffel.solutionafrica.helpers;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import lombok.Value;

public class MessageSending {

    public static final String ACCOUNT_SID = "AC35f0c30ae49ffd245417d53e804a5e5f";

    public static final String AUTH_TOKEN = "2779c07a4535ad0579a1c72cf87767c5";
    public static final String PHONE_NUMBER = "+14693332777";

    public void sendMessage(String receiverPhone, String body){

        System.out.println(ACCOUNT_SID);
        System.out.println(AUTH_TOKEN);
        String phone = "";
        if (receiverPhone.startsWith("0")){
            phone = "+243"+receiverPhone.substring(1);
        }else if (receiverPhone.startsWith("+243")){
            phone = receiverPhone;
        }
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        Message message = Message.creator(
                new com.twilio.type.PhoneNumber(phone),
                new com.twilio.type.PhoneNumber(PHONE_NUMBER),
                body)
                .create();

        System.out.println(message.getSid());
    }
}
