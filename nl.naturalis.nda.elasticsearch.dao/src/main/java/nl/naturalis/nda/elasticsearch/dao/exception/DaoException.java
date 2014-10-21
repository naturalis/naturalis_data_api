package nl.naturalis.nda.elasticsearch.dao.exception;

@SuppressWarnings("serial")
public class DaoException extends RuntimeException {

    public DaoException() {
    }


    public DaoException(String message) {
        super(message);
    }


    public DaoException(Throwable cause) {
        super(cause);
    }


    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }


    public DaoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
