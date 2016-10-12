package com.zygen.odata.model.message;

public class ZtextMessageV2 implements MessageV2 {
	public String Ivchannel ="";
	public String IvInput = "";
	public String IvUid = "";
	
	public ZtextMessageV2(String ch,String input,String uid){
		this.Ivchannel = ch;
		this.IvInput = input;
		this.IvUid = uid;
	}
	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return "IvChannel='"+this.Ivchannel+"',IvInput='"+this.IvInput+"',IvUid='"+this.IvUid+"'";
	}

}
