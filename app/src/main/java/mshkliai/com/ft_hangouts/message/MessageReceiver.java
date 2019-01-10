package mshkliai.com.ft_hangouts.message;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import mshkliai.com.ft_hangouts.database.tables.ContactsTable;
import mshkliai.com.ft_hangouts.database.tables.MessagesTable;
import mshkliai.com.ft_hangouts.listView.Model;

public class MessageReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle  bundle = intent.getExtras();

        if (bundle != null) {
            SmsMessage[]    messageSegments;
            String          messageBody = "Received: ";
            String          messageSender;

            Object[]        dataPackets = (Object[])bundle.get("pdus");
            messageSegments = new SmsMessage[dataPackets.length];

            for (int i = 0; i < messageSegments.length; i++) {
                messageSegments[i] = SmsMessage.createFromPdu((byte[]) dataPackets[i]);
                messageBody = messageBody.concat(messageSegments[i].getMessageBody());
            }
            messageSender = messageSegments[0].getOriginatingAddress();

            Toast.makeText(context, "Received message from: " + messageSender + "\nBody: " +
                    messageBody, Toast.LENGTH_LONG).show();

            saveMessageToBd(messageBody, messageSender, context);
        }
    }

    private void    saveMessageToBd(String messageBody, String messageSender, Context context) {
        MessagesTable   messagesTable = new MessagesTable(context);
        ContactsTable   contactsTable = new ContactsTable(context);

        Model           contact = contactsTable.getContactByNumber(messageSender);

        if (contact.getName().equals("")) {
            contact.setName(messageSender);

            contactsTable.addContact("Number " + messageSender, messageSender, "@mail.com", null);
        }

        messagesTable.addMessage(contact.getName(), messageBody, contact.getPhoto());

        messagesTable.close();
        contactsTable.close();
    }
}