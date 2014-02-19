package annoyaml.exception;

public class AnnoYAMLException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public AnnoYAMLException() {
		super();
	}

	public AnnoYAMLException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public AnnoYAMLException(String message, Throwable cause) {
		super(message, cause);
	}

	public AnnoYAMLException(String message) {
		super(message);
	}

	public AnnoYAMLException(Throwable cause) {
		super(cause);
	}

}
