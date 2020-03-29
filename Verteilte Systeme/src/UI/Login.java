package UI;

import javax.swing.*;

public class Login {

    Login(){   
		//Popup 
		JFrame popup=new JFrame("Client Login"); 
		//Buton Sign up
		JButton signUp=new JButton("Sign up");    
		signUp.setBounds(40,150,80, 40);
		//Button login
		JButton login=new JButton("Login");    
		login.setBounds(160,150,80, 40);     
		//Username
		JLabel username = new JLabel();		
		username.setText("Enter Username :");
		username.setBounds(10, 10, 100, 100);
		//Textfield Username
		JTextField usernameInput= new JTextField();
        	usernameInput.setBounds(110, 50, 130, 30);
       		//Password
        	JLabel password = new JLabel();		
		password.setText("Enter Pasword :");
		password.setBounds(10, 60, 100, 100);
		//Textfield password
		JTextField passwordInput= new JTextField();
		passwordInput.setBounds(110, 100, 130, 30);
		//Add fields to frame
		popup.add(login);
		popup.add(signUp);
	    	popup.add(username);
		popup.add(usernameInput);
        	popup.add(password); 
        	popup.add(passwordInput); 
		popup.setSize(300,300);    
		popup.setLayout(null);    
		popup.setVisible(true);    
		popup.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   
        } 
	   
	//run
    	public static void main (String[] args){

        new Login();

    }
}
