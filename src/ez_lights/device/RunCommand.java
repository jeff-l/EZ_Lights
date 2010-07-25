package ez_lights.device;


import java.util.Timer;
import java.util.TimerTask;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;

public final class RunCommand extends TimerTask {

	/**
	 * Construct and use a TimerTask and Timer.
	 */
	public static void main (String... arguments ) {
		TimerTask command  = new RunCommand();

		//perform the task once a day at 4 a.m., starting tomorrow morning
		//(other styles are possible as well)
		Timer timer = new Timer();
		//    timer.scheduleAtFixedRate(command, getTomorrowMorning4am(), fONCE_PER_DAY);
		timer.scheduleAtFixedRate(command, getTenSecondsFromNow(), 5000);
	}

	/**
	 * Implements TimerTask's abstract run method.
	 */
	public void run(){
		//toy implementation
		System.out.println("Fetching mail...");
	}

	// PRIVATE ////

	//expressed in milliseconds
	private final static long fONCE_PER_DAY = 1000*60*60*24;

	private final static int fONE_DAY = 1;
	private final static int fFOUR_AM = 4;
	private final static int fZERO_MINUTES = 0;

	private static Date getTenSecondsFromNow(){
		Calendar today = new GregorianCalendar();
		System.out.println("Hour of day is: " + Integer.toString(today.get(Calendar.HOUR_OF_DAY)));
		System.out.println("Minutes: " + Integer.toString(today.get(Calendar.MINUTE)));
		System.out.println(String.format("Time: %1$tH%1$tM", today));
		today.set(Calendar.HOUR_OF_DAY, 2);
		System.out.println(String.format("Time: %1$tH%1$tM", today));
		Calendar time = new GregorianCalendar(0,0,0,8,30);
		

		//System.out.println(today.toString());
		today.add(Calendar.SECOND, 10);
		//	    today.getT
		//	    Calendar.
		//	    today.
		//	    tomorrow.add(Calendar.DATE, fONE_DAY);
		//	    Calendar result = new GregorianCalendar(
		//	      tomorrow.get(Calendar.YEAR),
		//	      tomorrow.get(Calendar.MONTH),
		//	      tomorrow.get(Calendar.DATE),
		//	      fFOUR_AM,
		//	      fZERO_MINUTES
		//	    );
		//	    return result.getTime();
		return today.getTime();
	}

	private static Date getTomorrowMorning4am(){
		Calendar tomorrow = new GregorianCalendar();
		tomorrow.add(Calendar.DATE, fONE_DAY);
		Calendar result = new GregorianCalendar(
				tomorrow.get(Calendar.YEAR),
				tomorrow.get(Calendar.MONTH),
				tomorrow.get(Calendar.DATE),
				fFOUR_AM,
				fZERO_MINUTES
		);
		return result.getTime();
	}
}
