package com.nsw.miaosha.exception;

import com.nsw.miaosha.result.CodeMsg;

public class GlobalException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6762327688637067072L;

	private CodeMsg cm;
	
	public GlobalException(CodeMsg cm) {
		super(cm.toString());
		this.cm = cm;
	}

	public CodeMsg getCm() {
		return cm;
	}
	
	
}
