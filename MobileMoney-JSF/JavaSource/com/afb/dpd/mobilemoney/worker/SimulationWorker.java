package com.afb.dpd.mobilemoney.worker;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimerTask;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.afb.dpd.mobilemoney.jpa.entities.Parameters;
import com.afb.dpd.mobilemoney.jpa.entities.RequestMessage;
import com.afb.dpd.mobilemoney.jpa.entities.Subscriber;
import com.afb.dpd.mobilemoney.jpa.entities.Transaction;
import com.afb.dpd.mobilemoney.jpa.enums.TypeOperation;
import com.afb.dpd.mobilemoney.jsf.tools.MobileMoneyViewHelper;

public class SimulationWorker{
	
private static Log logger = LogFactory.getLog(TransactionWorker.class);
	
	private static TimerTask task;
	
	private static java.util.Timer timer;
	static Transaction trx = null;
	private static RequestMessage message;
	private static Map<String, String> map = new HashMap<String, String>();
	
	/**
	 * 
	 */
	public void initChecking(){
		try{
			
			if(task != null) task.cancel();
			if(timer != null) timer.cancel();
				

			task = new TimerTask(){
				@Override
				public void run(){
					
					try {
												
						Parameters params = MobileMoneyViewHelper.appManager.findParameters();
								logger.info("********************************* ROBOT SIMULATION STARTED *********************************");
								processSimulation();
						params = null;
					}catch(Exception e){
						//e.printStackTrace();
					}
				}	
			};

			timer = new java.util.Timer(true);
			int sec = 60;
			int min = 60;
			timer.schedule(task, DateUtils.addMinutes(new Date(), 5) , min*sec*1000);	

		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
  class Trx  implements Runnable {
		   
	    // implémentation de la méthode run() de l'interface Runnable
	    public  void run() {
	    	List<TypeOperation> list = new ArrayList<>(); 
			list.add(TypeOperation.PULL);
			list.add(TypeOperation.PUSH);
			message = new RequestMessage(TypeOperation.PULL, "", "237", 0d, "");
			message.setOperation(getRandomElement(list));
	       int n =  0 ; 
	       while (n++ <  5000) {
	          try {
	      
	        	  final double dbl = RandomUtils.nextDouble(100d, 1000d);
				  message.setAmount(dbl);
			      sendMessageECW();
	         }  catch (Exception e) {
	        	 e.printStackTrace();
	             // gestion de l'erreur
	         }
	      }
	   }
	}
	
	private void processSimulation() {
		
		String phones[] = {"237650430603","237670147445"};
	
		for(String phone : phones) {
		   message.setPhoneNumber(phone);
	       Trx trx = new Trx();
	       Thread thread =  new Thread(trx) ;
	       thread.start() ;
	   }
	}
	
	private static void sendMessageECW() {
		List<Transaction> listTrx = new ArrayList<Transaction>();
		try {
			
			// Test des preconditions
			if(!preConditionOK()) return;
			

			if(message.getOperation().equals(TypeOperation.PULL)){
				map = MobileMoneyViewHelper.appManager.pullTransactionECW(null, message.getPhoneNumber(), message.getAmount());
			}
			else if(message.getOperation().equals(TypeOperation.PUSH)){
				map = MobileMoneyViewHelper.appManager.pushTransactionECW(null, message.getPhoneNumber(), message.getAmount());
			}
			else if(message.getOperation().equals(TypeOperation.BALANCE))
				map = MobileMoneyViewHelper.appManager.getBalanceECW(message.getPhoneNumber());
						
			//sendSMSConfirmation();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private static boolean preConditionOK() throws Exception {
		
		trx = null;
		logger.info("SUB: "+message.getPhoneNumber());
		Subscriber subs = MobileMoneyViewHelper.appManager.findSubscriberFromPhoneNumber(message.getPhoneNumber());
		
		// Lecture du compte
		message.setAccount( subs.getAccounts().get(0) );
		// Initialisation de la transaction
		trx = new Transaction(message.getOperation(), subs, message.getAmount(), message.getAccount(), message.getPhoneNumber(), "");
		
		// Tout est OK
		return true;
	}
	
	public static TypeOperation getRandomElement(List<TypeOperation> list) 
    { 
        Random rand = new Random(); 
        return list.get(rand.nextInt(list.size())); 
    } 
	
}
