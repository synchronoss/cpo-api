/*
 *  Copyright 2008-2010. X-Factor Communications, 3 Empire Blvd
 *  South Hackensack, New Jersey, 07606, U.S.A.  All Rights Reserved.
 * 
 *  This source code is the confidential and proprietary information
 *  of X-Factor Communications ("Confidential Information"). You shall
 *  not disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into
 *  with X-Factor Communications.
 */

package org.synchronoss.cpo.helper;

/**
 *
 * @author dberry
 */
public class ExceptionHelper {
	public static String getMessage(Throwable e){
		String msg="";

		if (e!=null)
			msg=e.getMessage();

    if (msg==null && e.getCause()!=null)
      msg = e.getCause().getMessage();
    
		return msg;
	}

	public static String getLocalizedMessage(Throwable e){
		String msg="";

		if (e!=null)
			msg=e.getLocalizedMessage();

    if (msg==null && e.getCause()!=null)
      msg = e.getCause().getLocalizedMessage();
		
		return msg;
	}

}
