/**
 * Thrown if data is not compatible
 */

public class BadDataException extends RuntimeException
{
	public BadDataException(){
		super();
	}
	public BadDataException(String error){
		super(error);
	}
}
