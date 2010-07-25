package transceiver.cm19a;

public class CM19aException extends Exception
{

		CM19aException() {
				super();
		}

		CM19aException( String message ) {
				super( message );
		}

		CM19aException( String message, Throwable cause ) {
				super( message, cause );
		}
}
