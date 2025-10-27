package org.sbolstandard.converter;

import java.net.URI;

import org.sbolstandard.core3.util.URINameSpace;

public class ConverterNameSpace extends URINameSpace {
	
	//public static final String BP_Namespace="https://sbols.org/backport#";
	public static final String BP_Namespace_2_3="https://sbols.org/backport/2_3#";
	public static final String BP_prefix_2_3="backport2_3";
	public static URINameSpace BackPort_2_3=BackPortNameSpace2_3.getInstance();
	public static final String BP_Namespace_3_2="https://sbols.org/backport/3_2#";
	public static final String BP_prefix_3_2="backport3_2";
	public static URINameSpace BackPort_3_2=BackPortNameSpace3_2.getInstance();
	
	
	public ConverterNameSpace(URI namespace, String prefix) {
		super(namespace, prefix);
	}

	/* 
	public static class BackPortNameSpace extends URINameSpace
	{
		private static BackPortNameSpace instance=null;
		private BackPortNameSpace () {
			super(URI.create(BP_Namespace), BP_prefix);
		}
		
		protected static URINameSpace getInstance()
		{
			if (instance == null)     
			{
				instance = new BackPortNameSpace ();
			}
			return instance;
		}
	}
	*/

	public static class BackPortNameSpace2_3 extends URINameSpace
	{
		private static BackPortNameSpace2_3 instance=null;
		private BackPortNameSpace2_3 () {
			super(URI.create(BP_Namespace_2_3), BP_prefix_2_3);
		}
		
		protected static URINameSpace getInstance(){
			if (instance == null)     {
				instance = new BackPortNameSpace2_3 ();
			}
			return instance;
		}
	}

	public static class BackPortNameSpace3_2 extends URINameSpace
	{
		private static BackPortNameSpace3_2 instance=null;
		private BackPortNameSpace3_2 () {
			super(URI.create(BP_Namespace_3_2), BP_prefix_3_2);
		}
		
		protected static URINameSpace getInstance(){
			if (instance == null){
				instance = new BackPortNameSpace3_2 ();
			}
			return instance;
		}
	}
}