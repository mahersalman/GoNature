/**
 * The Message class represents a generic message that can carry a string message and an object of any type.
 * It's commonly used for communication between different parts of a system.
 *
 */
package entities;

import java.io.Serializable;

/**
 * Suppressing serial warning for clarity, as this class is intended for data
 * transfer.
 */
@SuppressWarnings("serial")
public class Message<T> implements Serializable {

	/**
	 * The string message carried by the Message object.
	 */
	private String msg;

	/**
	 * The object carried by the Message object.
	 */
	private T object;

	/**
	 * Creates a new Message object with both a string message and an object.
	 *
	 * @param msg    The string message.
	 * @param object The object.
	 */
	public Message(String msg, T object) {
		this.msg = msg;
		this.object = object;
	}

	/**
	 * Creates a new Message object with only a string message (no object).
	 */
	public Message() {
		super();
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public T getObject() {
		return object;
	}

	public void setObject(T object) {
		this.object = object;
	}

}
