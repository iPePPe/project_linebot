package com.zygen.odata.model.message;

public class ZtextMessage implements Message {
	public String Ivchannel ;
	public String IvLmid ;
	public String IvString ;
	public String EvString;
	
	public ZtextMessage(String ch,String mid,String str){
		this.Ivchannel = ch;
		this.IvLmid = mid;
		this.IvString = str;
		
	}
/*	public ZtextMessage(String str){
		this.Ivchannel = "1472660011";
		this.IvLmid = "07fbfe1943d58b1d0e5257c04f9b203551aa7077f62429228057b45e3cc37e57e4";
		this.IvString = str;
	}	*/
	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return "IvLmid='"+this.IvLmid+"',IvChannel='"+this.Ivchannel+"',IvString='"+this.IvString+"'";
	}


}
