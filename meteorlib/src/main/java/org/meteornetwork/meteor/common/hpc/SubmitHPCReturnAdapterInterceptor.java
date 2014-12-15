/*******************************************************************************
 * Copyright 2002 National Student Clearinghouse
 * 
 * This code is part of the Meteor system as defined and specified 
 * by the National Student Clearinghouse and the Meteor Sponsors.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 ******************************************************************************/
package org.meteornetwork.meteor.common.hpc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;


/**
 * this class is to adapt the "SubmitHPCReturn" message that returned by the old SOAP lib from Salli Mae
 *  to "SubmitHPCResponse" message
 * 
 *  Author: Yu Yu
 */
public class SubmitHPCReturnAdapterInterceptor extends AbstractPhaseInterceptor<Message> {
    public SubmitHPCReturnAdapterInterceptor() {
        super(Phase.RECEIVE); 
        //addBefore(ServiceInvokerInterceptor.class.getName());
    }

    @Override
    public void handleMessage(Message message) throws Fault  {
        
        System.out.println("message "+message);
        message.put(Message.ENCODING, "UTF-8");
        InputStream is = message.getContent(InputStream.class);
       
        if(is!=null){
          CachedOutputStream bos = new CachedOutputStream();
          try{
            IOUtils.copy(is,bos);
            String soapMessage = new String(bos.getBytes());
            System.out.println("------------------incoming-------------------------");
            System.out.println("incoming message is " + soapMessage);
            System.out.println("-------------------------------------------");
            bos.flush();
            message.setContent(InputStream.class, is);
           
            //is.close();
            InputStream inputStream = new ByteArrayInputStream(formatMsgFromSalliMae(soapMessage).getBytes());
            message.setContent(InputStream.class, inputStream);
            bos.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
                throw new Fault(ioe);
          }
        }
      }

      private String formatMsgFromSalliMae(String soapMessage) {
        System.out.println("------------------changed -------------------------");
        if (soapMessage.indexOf("SubmitHPCReturn") > 0)
        {
            soapMessage = soapMessage.replaceAll("SubmitHPCReturn", "return");
            int indexStart = soapMessage.indexOf("Message") + 7;
            int indexEnd = soapMessage.indexOf("TimeStamp") + 9;

            soapMessage = soapMessage.replaceAll(soapMessage.substring(indexStart,indexEnd), " Timestamp");
            soapMessage = soapMessage.replaceAll("&lt;Package&gt;", "&lt;Package&gt;&lt;Special/&gt;");
            
            //soapMessage = soapMessage.replaceAll(" TimeStamp", "/&gt;&lt;Message Timestamp");
        }
       
        System.out.println("After change message is " + soapMessage);
        System.out.println("------------------changed -------------------------");
        return soapMessage;
      }
}
