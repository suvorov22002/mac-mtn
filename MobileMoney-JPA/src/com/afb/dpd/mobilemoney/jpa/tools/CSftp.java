/**
 * 
 */
package com.afb.dpd.mobilemoney.jpa.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Vector;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.UserInfo;

/**
 * @author alex_jaza
 *
 */
public class CSftp {

	private String strHost;
	private String strUser;
	private String strPasswd;
	private Session session;
	private String strPortSSHZone;
	private ChannelSftp cannal;
	private static boolean annuler;


	public CSftp(String strLeHost, String strLeUser, String strLePasswd){
		this.strHost = strLeHost;
		this.strUser = strLeUser;
		this.strPasswd = strLePasswd;
		annuler = false;
	}

	/**
	* 
	* @param lfile
	* @param user
	* @param host
	* @param rfile
	* @param password
	* @throws Exception
	*/
	public static Boolean send(String lfile, String user, String host, String ftpDir, String rfile, String password)  throws Exception {

		try{

			JSch jsch = new JSch();
			Session  session = jsch.getSession(user,host,22);
			UserInfo ui = new MyUserInfo(password);
			session.setUserInfo(ui);
			session.connect();

			Channel channel = session.openChannel("sftp");
			channel.connect();
			ChannelSftp cannal = ((ChannelSftp)channel);

			if(!ftpDir.trim().isEmpty())cannal.cd(ftpDir);

			File remoteFileName = new File(lfile);
			// cannal.put(lfile, rfile, null, 0);
			cannal.put(new FileInputStream(remoteFileName), remoteFileName.getName());
			// cannal.rm(remoteFileName.getName());
			cannal.exit();
			channel.disconnect();
			session.disconnect();


		}catch (Exception e){
			e.printStackTrace();
			return Boolean.FALSE;
		}

		return Boolean.TRUE;

	}




	@SuppressWarnings("unchecked")
	public static Boolean searchFileRejet(String lfile, String user, String host, String ftpDir, String rfile, String password)  throws Exception {

		boolean res = false;
		try{

			JSch jsch = new JSch();
			Session  session = jsch.getSession(user,host,22);
			UserInfo ui = new MyUserInfo(password);
			session.setUserInfo(ui);
			session.connect();

			Channel channel = session.openChannel("sftp");
			channel.connect();
			ChannelSftp cannal = ((ChannelSftp)channel);

			if(!ftpDir.trim().isEmpty())cannal.cd(ftpDir);
			
			Vector<ChannelSftp.LsEntry>  fileList = cannal.ls(lfile);

			for(ChannelSftp.LsEntry entry : fileList){
				if(!entry.getAttrs().isDir()){
					String name = entry.getFilename();
					if(name.equals(lfile)){
						res = true;
						OutputStream output = new FileOutputStream(MoMoHelper.getPiecesJointesDir()+name);
						cannal.get(name,MoMoHelper.getPiecesJointesDir()+name);
						output.close();
					}
									
				}
			}
			
	
			/*
			if(cannal.get(lfile).read() != 0){
				res = true;

				cannal.get(lfile);
				System.out.println("************* cannal.get(lfile, new FileOutputStream(PenSalaireUtils.getPiecesJointesDir()+lfile)) *****************");
				OutputStream output = new FileOutputStream(PenSalaireUtils.getPiecesJointesDir()+lfile);
				cannal.get(lfile,PenSalaireUtils.getPiecesJointesDir()+lfile);
				output.close();
			} 
			*/

			cannal.exit();
			channel.disconnect();
			session.disconnect();


		}catch (Exception e){
			e.printStackTrace();
		}

		return res;

	}



	public static File locateFile(String lfile, String user, String host, String ftpDir, String rfile, String password)  throws Exception {

		File file = null;
		try{

			JSch jsch = new JSch();
			Session  session = jsch.getSession(user,host,22);
			UserInfo ui = new MyUserInfo(password);
			session.setUserInfo(ui);
			session.connect();

			Channel channel = session.openChannel("sftp");
			channel.connect();
			ChannelSftp cannal = ((ChannelSftp)channel);

			if(!ftpDir.trim().isEmpty())cannal.cd(ftpDir);
			file = new File(lfile);

			//cannal.put(new FileInputStream(remoteFileName), remoteFileName.getName());

			cannal.exit();
			channel.disconnect();
			session.disconnect();


		}catch (Exception e){
			e.printStackTrace();
		}

		return file;

	}



	@SuppressWarnings("unchecked")
	public static void find(String user, String host, String ftpDir, String password,String pathFile) {

		try{

			JSch jsch = new JSch();
			Session  session = jsch.getSession(user,host,22);
			UserInfo ui = new MyUserInfo(password);
			session.setUserInfo(ui);
			session.connect();

			Channel channel = session.openChannel("sftp");
			channel.connect();
			ChannelSftp cannal = ((ChannelSftp)channel);
			if(!ftpDir.trim().isEmpty())cannal.cd(ftpDir);


			//File remoteFileName = new File(lfile);
			Vector<ChannelSftp.LsEntry>  fileList = cannal.ls("*");

			for(ChannelSftp.LsEntry entry : fileList){
				if(!entry.getAttrs().isDir()){
					String name = entry.getFilename();
					//System.out.println("------------name-----------"+name);
					OutputStream output = new FileOutputStream(pathFile+File.separator+name);
					cannal.get(name,pathFile+File.separator+name);
					output.close();

					// Suppression du Fichier
					cannal.rm(name);
				}
			}

			cannal.exit();
			channel.disconnect();
			session.disconnect();


		}catch (Exception e){
			e.printStackTrace();
		}
	}


	public boolean openSFTP(){

		try{
			JSch jsch = new JSch();
			this.session = jsch.getSession(this.strUser, this.strHost, Integer.parseInt(strPortSSHZone));
			UserInfo ui = new MyUserInfo(this.strPasswd);
			this.session.setUserInfo(ui);
			this.session.connect();
			Channel channel = this.session.openChannel("sftp");
			channel.connect();
			this.cannal = ((ChannelSftp)channel);
			return true;
		}catch (Exception e){
			e.printStackTrace(System.out);
		}

		return false;

	}

	public boolean closeSFTP(){

		try{

			this.session.disconnect();
			return true;
		}
		catch (Exception e){
			e.printStackTrace();
		}

		return false;
	}

	public boolean envoieSFTP(String strLocalPath, String strRemotePath){
		try
		{
			annuler = false;
			this.cannal.put(strLocalPath, strRemotePath, null, 0);
			if (annuler)
			{
				File remoteFileName = new File(strLocalPath);
				this.cannal.cd(strRemotePath);
				this.cannal.rm(remoteFileName.getName());
				return false;
			}
			return true;
		}
		catch (SftpException e)
		{
			return false;
		}
		catch (Exception exc)
		{
		}
		return false;
	}

	public static class MyUserInfo implements UserInfo{
		String strPasswd;

		public MyUserInfo(String strLePasswd)
		{
			this.strPasswd = strLePasswd;
		}

		public String getPassword()
		{
			return this.strPasswd;
		}

		public boolean promptYesNo(String str)
		{
			return true;
		}

		public String getPassphrase()
		{
			return null;
		}

		public boolean promptPassphrase(String strMessage)
		{
			return true;
		}

		public boolean promptPassword(String strMessage)
		{
			return true;
		}

		public void showMessage(String strMessage) {}
	}

}